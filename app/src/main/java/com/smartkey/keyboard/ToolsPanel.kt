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

class ToolsPanel(private val ctx: Context) {

    interface Callbacks {
        fun insert(text: String)
        fun transform(mode: Int)
        fun copySelection()
    }

    private var popup: PopupWindow? = null

    fun show(anchor: View, callbacks: Callbacks) {
        dismiss()
        val prefs = ctx.getSharedPreferences("smartkey", Context.MODE_PRIVATE)
        val theme = ThemeConfig.byName(prefs.getString(SmartPrefs.KEY_THEME, ThemeConfig.KEY_LIGHT) ?: ThemeConfig.KEY_LIGHT)

        val scroll = ScrollView(ctx)
        scroll.setBackgroundColor(theme.panel)
        scroll.setPadding(dp(8))
        val list = LinearLayout(ctx)
        list.orientation = LinearLayout.VERTICAL

        fun addTitle(text: String) {
            val tv = TextView(ctx)
            tv.text = text
            tv.setTextColor(theme.keyText)
            tv.textSize = 15f
            tv.isAllCaps = true
            tv.setPadding(dp(4), dp(6), dp(4), dp(6))
            list.addView(tv)
        }

        fun addButton(text: String, onClick: () -> Unit) {
            val b = TextView(ctx)
            b.text = text
            b.setTextColor(theme.keyText)
            b.textSize = 14f
            b.setPadding(dp(8), dp(10), dp(8), dp(10))
            b.setBackgroundColor(theme.keyDark)
            b.setOnClickListener { onClick() }
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = dp(4)
            b.layoutParams = lp
            list.addView(b)
        }

        addTitle("Quick text")
        addButton("email@example.com") { callbacks.insert("email@example.com") }
        addButton("+1 555 0123") { callbacks.insert("+1 555 0123") }
        addButton("123 Street, City") { callbacks.insert("123 Street, City") }
        addButton("https://www.example.com") { callbacks.insert("https://www.example.com") }
        addButton(":-)") { callbacks.insert(":-)") }
        addButton("OK") { callbacks.insert("OK") }

        addTitle("Text case")
        addButton("UPPERCASE") { callbacks.transform(0) }
        addButton("lowercase") { callbacks.transform(1) }
        addButton("Capitalized") { callbacks.transform(2) }

        addTitle("Clipboard")
        addButton("Copy selection") { callbacks.copySelection() }

        scroll.addView(list)
        popup = PopupWindow(scroll, (anchor.width * 0.8f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
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