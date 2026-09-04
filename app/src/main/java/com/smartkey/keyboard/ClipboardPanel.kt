package com.smartkey.keyboard

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView

class ClipboardPanel(
    private val ctx: Context,
    private val clipboard: SmartClipboard
) {
    private var popup: PopupWindow? = null

    fun show(
        anchor: View,
        onSelect: (String) -> Unit,
        onDelete: (SmartClipboard.ClipItem) -> Unit
    ) {
        dismiss()
        val theme = ThemeConfig.byName(
            ctx.getSharedPreferences("smartkey", Context.MODE_PRIVATE).getString(SmartPrefs.KEY_THEME, ThemeConfig.KEY_LIGHT)
                ?: ThemeConfig.KEY_LIGHT
        )
        val items = clipboard.items()

        val scroll = ScrollView(ctx)
        scroll.setBackgroundColor(theme.panel)
        scroll.setPadding(dp(8))
        val list = LinearLayout(ctx)
        list.orientation = LinearLayout.VERTICAL
        list.setBackgroundColor(theme.panel)

        val title = TextView(ctx)
        title.text = "Clipboard"
        title.setTextColor(theme.keyText)
        title.textSize = 15f
        title.isAllCaps = true
        title.setPadding(dp(4), dp(6), dp(4), dp(6))
        list.addView(title)

        if (items.isEmpty()) {
            val empty = TextView(ctx)
            empty.text = "No copied items yet"
            empty.setTextColor(theme.dimText)
            empty.textSize = 13f
            empty.setPadding(dp(4), dp(4), dp(4), dp(4))
            list.addView(empty)
        } else {
            for (item in items) {
                val row = LinearLayout(ctx)
                row.orientation = LinearLayout.HORIZONTAL
                row.gravity = Gravity.CENTER_VERTICAL
                row.setPadding(dp(2), dp(6), dp(4), dp(6))

                val label = TextView(ctx)
                label.text = item.text
                label.setTextColor(theme.keyText)
                label.textSize = 14f
                label.maxLines = 1
                label.ellipsize = android.text.TextUtils.TruncateAt.MIDDLE
                label.setPadding(dp(4), 0, dp(4), 0)
                label.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                row.addView(label)

                val del = TextView(ctx)
                del.text = "✕"
                del.setTextColor(theme.dimText)
                del.textSize = 14f
                del.setPadding(dp(14), dp(4), dp(14), dp(4))
                del.setOnClickListener {
                    onDelete(item)
                    show(anchor, onSelect, onDelete)
                }
                row.addView(del)

                row.setOnClickListener {
                    onSelect(item.text)
                }
                list.addView(row)
            }
        }

        scroll.addView(list)
        popup = PopupWindow(scroll, (anchor.width * 0.9f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        popup?.isFocusable = true
        popup?.isOutsideTouchable = true
        popup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup?.setOnDismissListener { popup = null }
        popup?.showAtLocation(anchor, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, dp(60))
    }

    fun dismiss() {
        popup?.dismiss()
        popup = null
    }

    private fun dp(v: Int): Int = (v * ctx.resources.displayMetrics.density).toInt()
}