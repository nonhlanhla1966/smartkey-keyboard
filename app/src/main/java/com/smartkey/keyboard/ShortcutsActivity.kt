package com.smartkey.keyboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class ShortcutsActivity : AppCompatActivity() {

    private val engine by lazy { TextShortcuts(SmartPrefs(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcuts)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        findViewById<TextView>(R.id.btn_shortcuts_add).setOnClickListener { showEditor() }
        buildList()
    }

    private fun buildList() {
        val theme = ThemeConfig.byName(
            SmartPrefs(this).getString(SmartPrefs.KEY_THEME) ?: ThemeConfig.KEY_LIGHT, this
        )
        findViewById<View>(R.id.shortcuts_list).setBackgroundColor(theme.bg)
        val container = findViewById<LinearLayout>(R.id.shortcuts_list)
        container.removeAllViews()

        val entries = engine.all()
        if (entries.isEmpty()) {
            val empty = TextView(this)
            empty.text = getString(R.string.shortcuts_empty)
            empty.setTextColor(theme.dimText)
            empty.textSize = 13f
            empty.setPadding(dp(8), dp(16), dp(8), dp(16))
            container.addView(empty)
            return
        }
        for ((shortcut, replacement) in entries) {
            container.addView(createRow(theme, shortcut, replacement))
        }
    }

    private fun createRow(theme: Theme, shortcut: String, replacement: String): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.VERTICAL
        row.setPadding(dp(12), dp(6), dp(12), dp(6))
        row.setBackgroundColor(theme.key)

        val top = LinearLayout(this)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL

        val label = TextView(this)
        label.text = shortcut
        label.setTextColor(theme.accent)
        label.textSize = 16f
        label.setTypeface(null, android.graphics.Typeface.BOLD)
        label.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        top.addView(label)

        val edit = TextView(this)
        edit.text = "✎"
        edit.setTextColor(theme.dimText)
        edit.textSize = 14f
        edit.setPadding(dp(10), dp(6), dp(10), dp(6))
        edit.setOnClickListener { showEditor(shortcut, replacement) }
        top.addView(edit)

        val del = TextView(this)
        del.text = "✕"
        del.setTextColor(theme.dimText)
        del.textSize = 14f
        del.setPadding(dp(10), dp(6), dp(10), dp(6))
        del.setOnClickListener {
            engine.remove(shortcut)
            buildList()
        }
        top.addView(del)
        row.addView(top)

        val sub = TextView(this)
        sub.text = replacement
        sub.setTextColor(theme.keyText)
        sub.textSize = 13f
        sub.maxLines = 1
        sub.ellipsize = android.text.TextUtils.TruncateAt.MIDDLE
        sub.setPadding(dp(2), dp(2), dp(2), dp(0))
        row.addView(sub)

        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.bottomMargin = dp(6)
        row.layoutParams = lp
        return row
    }

    private fun showEditor(existingShortcut: String? = null, existingText: String? = null) {
        val input = EditText(this)
        input.hint = getString(R.string.shortcuts_shortcut_hint)
        input.setPadding(dp(8), dp(4), dp(8), dp(4))
        val input2 = EditText(this)
        input2.hint = getString(R.string.shortcuts_replacement_hint)
        if (existingShortcut != null) input.setText(existingShortcut)
        if (existingText != null) input2.setText(existingText)

        val who = this
        AlertDialog.Builder(this)
            .setTitle(if (existingShortcut != null) getString(R.string.shortcuts_edit) else getString(R.string.shortcuts_add))
            .setView(who.shortcutsDialogView(input, input2))
            .setPositiveButton(getString(R.string.shortcuts_save)) { _, _ ->
                val ok = engine.put(input.text.toString(), input2.text.toString())
                if (ok) {
                    buildList()
                } else {
                    AlertDialog.Builder(who)
                        .setMessage(R.string.shortcuts_invalid)
                        .setPositiveButton(getString(R.string.shortcuts_ok), null)
                        .show()
                }
            }
            .setNegativeButton(getString(R.string.shortcuts_cancel), null)
            .show()
    }

    private fun shortcutsDialogView(a: EditText, b: EditText): View {
        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.setPadding(dp(20), 0, dp(20), 0)
        col.addView(a, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        col.addView(b, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        return col
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}