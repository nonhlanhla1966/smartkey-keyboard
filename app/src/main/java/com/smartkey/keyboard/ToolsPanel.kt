package com.smartkey.keyboard

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.setPadding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToolsPanel(private val ctx: Context) {

    interface Callbacks {
        fun insert(text: String)
        fun transform(mode: Int)
        fun copySelection()
    }

    private var popup: PopupWindow? = null
    private var callbacks: Callbacks? = null
    private val converter = UnitConverter()

    private var lastResult: String? = null

    fun show(anchor: View, callbacks: Callbacks) {
        dismiss()
        this.callbacks = callbacks
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
            tv.setTypeface(null, android.graphics.Typeface.BOLD)
            tv.setPadding(dp(4), dp(8), dp(4), dp(4))
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

        fun addLabel(text: String) {
            val tv = TextView(ctx)
            tv.text = text
            tv.setTextColor(theme.dimText)
            tv.textSize = 12f
            tv.setPadding(dp(4), dp(6), dp(4), dp(2))
            list.addView(tv)
        }

        fun addSpinner(items: List<String>): Spinner {
            val s = Spinner(ctx)
            val adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            s.adapter = adapter
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = dp(4)
            s.layoutParams = lp
            list.addView(s)
            return s
        }

        fun addEditText(hint: String, number: Boolean): EditText {
            val et = EditText(ctx)
            et.hint = hint
            et.setTextColor(theme.keyText)
            et.setHintTextColor(theme.dimText)
            et.setTextSize(14f)
            et.isShowSoftInputOnFocus = false
            et.inputType = if (number) InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            else InputType.TYPE_CLASS_TEXT
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = dp(4)
            et.layoutParams = lp
            list.addView(et)
            return et
        }

        fun addResultText(): TextView {
            val tv = TextView(ctx)
            tv.setTextColor(theme.accent)
            tv.textSize = 16f
            tv.setPadding(dp(4), dp(4), dp(4), dp(6))
            list.addView(tv)
            return tv
        }

        // ---- Date & time ----
        addTitle(ctx.getString(R.string.tools_datetime))
        addButton(ctx.getString(R.string.tools_insert_date)) { callbacks.insert(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
        addButton(ctx.getString(R.string.tools_insert_time)) { callbacks.insert(SimpleDateFormat("HH:mm", Locale.US).format(Date())) }

        // ---- Unit converter ----
        addTitle(ctx.getString(R.string.tools_converter))
        val categories = UnitConverter.Category.entries.map { it.label }
        val catSpinner = addSpinner(categories)
        val row = LinearLayout(ctx)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val fromSpinner = Spinner(ctx)
        val toSpinner = Spinner(ctx)
        row.addView(fromSpinner, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(toSpinner, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        list.addView(row)

        fun unitSymbols(cat: UnitConverter.Category): List<String> = UnitConverter.unitsFor(cat).map { it.symbol }

        fun populateUnitSpinners(cat: UnitConverter.Category, keepFrom: String? = null) {
            val syms = unitSymbols(cat)
            fun fill(s: Spinner, position: Int) {
                val a = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, syms)
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                s.adapter = a
                s.setSelection(position.coerceIn(0, syms.size - 1))
            }
            val fromPos = if (keepFrom != null) syms.indexOf(keepFrom).coerceAtLeast(0) else syms.size / 2
            val toPos = if (keepFrom != null) syms.size / 2 else 0
            fill(fromSpinner, fromPos)
            fill(toSpinner, toPos)
        }

        populateUnitSpinners(UnitConverter.Category.LENGTH)
        catSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val keep = (fromSpinner.selectedItem as? String)
                populateUnitSpinners(UnitConverter.Category.entries[position], keep)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        val valueInput = addEditText(ctx.getString(R.string.tools_converter_value), true)
        val resultText = addResultText()
        addButton(ctx.getString(R.string.tools_converter_convert)) {
            val cat = UnitConverter.Category.entries[catSpinner.selectedItemPosition.coerceIn(0, UnitConverter.Category.entries.size - 1)]
            val from = fromSpinner.selectedItem as? String ?: return@addButton
            val to = toSpinner.selectedItem as? String ?: return@addButton
            val v = valueInput.text.toString().toDoubleOrNull() ?: return@addButton
            val r = converter.convert(cat, v, from, to)
            val text = if (r == null) "Error" else fmt(r)
            resultText.text = "$v $from = $text $to"
            lastResult = text
        }
        addButton(ctx.getString(R.string.tools_converter_insert)) {
            lastResult?.let { callbacks.insert(it) }
        }

        // ---- Password generator ----
        addTitle(ctx.getString(R.string.tools_password))
        val lengthInput = addEditText(ctx.getString(R.string.tools_password_length), true)
        lengthInput.setText("16")

        val opts = LinearLayout(ctx)
        opts.orientation = LinearLayout.HORIZONTAL
        opts.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        fun addCheck(label: String, checked: Boolean): android.widget.CheckBox {
            val cb = android.widget.CheckBox(ctx)
            cb.text = label
            cb.setTextColor(theme.keyText)
            cb.isChecked = checked
            cb.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            opts.addView(cb)
            return cb
        }
        val cbUpper = addCheck(ctx.getString(R.string.tools_password_upper), true)
        val cbDigits = addCheck(ctx.getString(R.string.tools_password_digits), true)
        val cbSymbols = addCheck(ctx.getString(R.string.tools_password_symbols), true)
        list.addView(opts)

        val pwResult = addResultText()
        addButton(ctx.getString(R.string.tools_password_generate)) {
            val len = lengthInput.text.toString().toIntOrNull() ?: 16
            val pw = PasswordGenerator.generate(len, cbUpper.isChecked, cbDigits.isChecked, cbSymbols.isChecked)
            pwResult.text = pw
            lastPw = pw
        }
        addButton(ctx.getString(R.string.tools_password_insert)) {
            lastPw?.let { callbacks.insert(it) }
        }

        // ---- Symbols ----
        addTitle(ctx.getString(R.string.tools_symbols))
        val symbolGroups = listOf(
            listOf("©", "®", "™", "€", "£", "¥"),
            listOf("¢", "§", "¶", "•", "°", "±"),
            listOf("≠", "≤", "≥", "≈", "∞", "×"),
            listOf("÷", "√", "→", "←", "↑", "↓"),
            listOf("…", "—", "–", "✓", "✗", "▪")
        )
        for (group in symbolGroups) {
            val r2 = LinearLayout(ctx)
            r2.orientation = LinearLayout.HORIZONTAL
            r2.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            for (sym in group) {
                val b = TextView(ctx)
                b.text = sym
                b.setTextColor(theme.keyText)
                b.textSize = 16f
                b.gravity = Gravity.CENTER
                b.setPadding(0, dp(8), 0, dp(8))
                b.setOnClickListener { callbacks.insert(sym) }
                r2.addView(b, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            }
            list.addView(r2)
        }

        // ---- Quick text ----
        addTitle(ctx.getString(R.string.tools_quick))
        addButton(ctx.getString(R.string.text_email)) { callbacks.insert("email@example.com") }
        addButton(ctx.getString(R.string.text_phone)) { callbacks.insert("+1 555 0123") }
        addButton(ctx.getString(R.string.text_home)) { callbacks.insert("123 Street, City") }
        addButton(ctx.getString(R.string.text_url)) { callbacks.insert("https://www.example.com") }
        addButton(ctx.getString(R.string.text_smile)) { callbacks.insert(":-)") }
        addButton(ctx.getString(R.string.text_ok)) { callbacks.insert("OK") }

        // ---- Text case ----
        addTitle(ctx.getString(R.string.tools_case))
        addButton(ctx.getString(R.string.tools_uppercase)) { callbacks.transform(0) }
        addButton(ctx.getString(R.string.tools_lowercase)) { callbacks.transform(1) }
        addButton(ctx.getString(R.string.tools_capitalize)) { callbacks.transform(2) }

        addTitle(ctx.getString(R.string.clipboard_title))
        addButton(ctx.getString(R.string.tools_copy_all)) { callbacks.copySelection() }

        scroll.addView(list)
        popup = PopupWindow(scroll, (anchor.width * 0.92f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        popup?.isFocusable = true
        popup?.isOutsideTouchable = true
        popup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup?.setOnDismissListener { popup = null }
        popup?.showAtLocation(anchor, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, dp(40))
    }

    fun dismiss() {
        popup?.dismiss()
        popup = null
    }

    private var lastPw: String? = null

    private fun fmt(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "Error"
        if (v == Math.floor(v) && Math.abs(v) < 1e15) return v.toLong().toString()
        return String.format(Locale.US, "%.6f", v).trimEnd('0').trimEnd('.')
    }

    private fun dp(v: Int): Int = (v * ctx.resources.displayMetrics.density).toInt()
}