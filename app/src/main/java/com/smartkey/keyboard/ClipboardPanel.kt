package com.smartkey.keyboard

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding

class ClipboardPanel(
    private val ctx: Context,
    private val clipboard: SmartClipboard
) {
    private var popup: PopupWindow? = null
    private var anchor: View? = null
    private var query = ""
    private var theme: Theme = ThemeConfig.light()

    private var onSelect: (String) -> Unit = {}

    fun show(anchor: View, onSelect: (String) -> Unit) {
        this.anchor = anchor
        this.onSelect = onSelect
        rebuild()
    }

    private fun rebuild() {
        dismiss()
        val a = anchor ?: return
        theme = ThemeConfig.byName(
            ctx.getSharedPreferences("smartkey", Context.MODE_PRIVATE).getString(SmartPrefs.KEY_THEME, ThemeConfig.KEY_LIGHT)
                ?: ThemeConfig.KEY_LIGHT
        )
        val items = if (query.isBlank()) clipboard.items() else clipboard.search(query)

        val scroll = ScrollView(ctx)
        scroll.setBackgroundColor(theme.panel)
        scroll.setPadding(dp(8))
        val list = LinearLayout(ctx)
        list.orientation = LinearLayout.VERTICAL
        list.setBackgroundColor(theme.panel)

        fun title(text: String) {
            val tv = TextView(ctx)
            tv.text = text
            tv.setTextColor(theme.keyText)
            tv.textSize = 15f
            tv.setTypeface(null, android.graphics.Typeface.BOLD)
            tv.isAllCaps = true
            tv.setPadding(dp(4), dp(6), dp(4), dp(6))
            list.addView(tv)
        }

        title(ctx.getString(R.string.clipboard_title))

        val search = EditText(ctx)
        search.hint = ctx.getString(R.string.clipboard_search)
        search.setText(query)
        search.setTextColor(theme.keyText)
        search.setHintTextColor(theme.dimText)
        search.setTextSize(13f)
        search.showSoftInputOnFocus = false
        search.setPadding(dp(6), dp(6), dp(6), dp(6))
        list.addView(search)
        search.setOnClickListener {
            val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(a.windowToken, 0)
        }
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val filterRunnable = object : Runnable {
            override fun run() {
                val current = search.text?.toString() ?: ""
                if (current != query) {
                    query = current
                    rebuild()
                }
            }
        }
        search.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                handler.removeCallbacks(filterRunnable)
                handler.postDelayed(filterRunnable, 300)
            }
        })

        if (items.isEmpty()) {
            val empty = TextView(ctx)
            empty.text = ctx.getString(R.string.clipboard_empty)
            empty.setTextColor(theme.dimText)
            empty.textSize = 13f
            empty.setPadding(dp(4), dp(8), dp(4), dp(8))
            list.addView(empty)
        } else {
            for (item in items) {
                val row = LinearLayout(ctx)
                row.orientation = LinearLayout.HORIZONTAL
                row.gravity = Gravity.CENTER_VERTICAL
                row.setPadding(dp(2), dp(4), dp(4), dp(4))

                val label = TextView(ctx)
                val display = item.displayName
                label.text = display
                label.setTextColor(theme.keyText)
                label.textSize = 14f
                label.maxLines = 1
                label.ellipsize = TextUtils.TruncateAt.MIDDLE
                label.setPadding(dp(4), 0, dp(4), 0)
                label.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                row.addView(label)

                val pin = TextView(ctx)
                pin.text = if (item.pinned) "🔒" else "📌"
                pin.setTextColor(if (item.pinned) theme.accent else theme.dimText)
                pin.textSize = 14f
                pin.setPadding(dp(10), dp(4), dp(10), dp(4))
                pin.setOnClickListener {
                    clipboard.togglePin(item)
                    rebuild()
                }
                row.addView(pin)

                val rename = TextView(ctx)
                rename.text = "✎"
                rename.setTextColor(theme.dimText)
                rename.textSize = 14f
                rename.setPadding(dp(10), dp(4), dp(10), dp(4))
                rename.setOnClickListener {
                    showRename(item)
                }
                row.addView(rename)

                val del = TextView(ctx)
                del.text = "✕"
                del.setTextColor(theme.dimText)
                del.textSize = 14f
                del.setPadding(dp(10), dp(4), dp(10), dp(4))
                del.setOnClickListener {
                    clipboard.delete(item)
                    rebuild()
                }
                row.addView(del)

                row.setOnClickListener {
                    onSelect(item.text)
                }
                list.addView(row)
            }
        }

        val clearAll = TextView(ctx)
        clearAll.text = ctx.getString(R.string.clipboard_clear_all)
        clearAll.setTextColor(theme.accent)
        clearAll.textSize = 14f
        clearAll.gravity = Gravity.CENTER
        clearAll.setPadding(0, dp(10), 0, dp(6))
        clearAll.setOnClickListener {
            AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.clipboard_clear_all))
                .setMessage(ctx.getString(R.string.clipboard_confirm_clear))
                .setPositiveButton(ctx.getString(R.string.clipboard_action_ok)) { _, _ ->
                    clipboard.clearAll()
                    rebuild()
                }
                .setNegativeButton(ctx.getString(R.string.clipboard_action_cancel), null)
                .show()
        }
        list.addView(clearAll)

        scroll.addView(list)
        popup = PopupWindow(scroll, (a.width * 0.92f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        popup?.isFocusable = true
        popup?.isOutsideTouchable = false
        popup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup?.setOnDismissListener { popup = null }
        popup?.showAtLocation(a, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, dp(50))
    }

    private fun showRename(item: SmartClipboard.ClipItem) {
        val input = EditText(ctx)
        input.setText(item.displayName)
        input.setSelection(input.text.length)
        input.showSoftInputOnFocus = false
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(dp(20), 0, dp(20), 0)
        input.layoutParams = lp
        AlertDialog.Builder(ctx)
            .setTitle(ctx.getString(R.string.clipboard_rename_title))
            .setView(input)
            .setPositiveButton(ctx.getString(R.string.clipboard_action_ok)) { _, _ ->
                clipboard.rename(item, input.text.toString())
                rebuild()
            }
            .setNegativeButton(ctx.getString(R.string.clipboard_action_cancel), null)
            .show()
    }

    fun dismiss() {
        popup?.dismiss()
        popup = null
    }

    private fun dp(v: Int): Int = (v * ctx.resources.displayMetrics.density).toInt()
}