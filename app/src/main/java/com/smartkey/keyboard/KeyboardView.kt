package com.smartkey.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View

interface KeyboardListener {
    fun onKey(spec: KeySpec)
    fun onSuggestion(text: String)
    fun getCalcDisplay(): String
}

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var listener: KeyboardListener? = null

    private val dp: Float = resources.displayMetrics.density
    private val sp: Float = resources.displayMetrics.scaledDensity

    var theme: Theme = ThemeConfig.light()
        set(value) {
            field = value
            invalidate()
        }

    var mode = KeyboardLayout.MODE_LETTERS
        set(value) {
            field = value
            layoutKeys()
            invalidate()
        }

    var shiftState = 0
    var capsLock = false
    var numberRowEnabled = false
        set(value) {
            field = value
            layoutKeys()
            invalidate()
        }
    var keyHeightMult = 1f
        set(value) {
            field = value
            layoutKeys()
            invalidate()
        }
    var oneHandedSide = 0
        set(value) {
            field = value
            layoutKeys()
            invalidate()
        }

    private val isLandscape: Boolean get() = width > height

    var suggestions: List<String> = emptyList()
    var typedPrefix: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var emojiCategory = 0
        private set
    var emojiPage = 0
        private set

    var emojiSearchActive = false
        set(value) {
            field = value
            layoutKeys()
            invalidate()
        }
    var emojiQuery = ""
        set(value) {
            field = value
            invalidate()
        }
    var emojiResults: List<String> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private var showSuggestions = true

    private class RK(var spec: KeySpec, var x: Int, var y: Int, var w: Int, var h: Int) {
        var pressed = false
        fun contains(px: Float, py: Float): Boolean = px >= x && px <= x + w && py >= y && py <= y + h
    }

    private val keys = ArrayList<RK>()
    private var pressedId = -1
    private val handler = Handler(Looper.getMainLooper())
    private var repeatId = -1

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val round = RectF()

    private var suggestionsBarH = dp(46f)
    private var displayH = dp(52f)
    private var actionBarH = dp(46f)
    private var categoryBarH = dp(38f)
    private var keyGap = dp(2f)
    private var margin = dp(3f)

    private fun dp(v: Float): Int = (v * dp).toInt()
    private fun sp(v: Float): Float = v * sp

    private val queryW: Float get() = dp(150f).toFloat()
    private val clearW: Float get() = dp(28f).toFloat()
    private val matchesX: Float get() = queryW + clearW + dp(6f)

    private fun suggestionsBarHeight(): Int =
        if (emojiSearchActive && mode == KeyboardLayout.MODE_LETTERS) dp(76f) else suggestionsBarH

    private val LONG_PRESS_MS = 400L

    private val altMap = mapOf(
        'a' to listOf("á", "à", "â", "ä", "ã", "å", "ā", "æ"),
        'e' to listOf("é", "è", "ê", "ë", "ē", "ė", "ę"),
        'i' to listOf("í", "ì", "î", "ï", "ī", "į"),
        'o' to listOf("ó", "ò", "ô", "ö", "õ", "ō", "ø", "œ"),
        'u' to listOf("ú", "ù", "û", "ü", "ū", "ů"),
        'y' to listOf("ý", "ÿ"),
        'c' to listOf("ç", "ć", "č"),
        'n' to listOf("ñ", "ń", "ň"),
        's' to listOf("ß", "ś", "š"),
        'z' to listOf("ž", "ź", "ż"),
        'l' to listOf("ł"),
        'd' to listOf("đ", "ð"),
        'k' to listOf("ķ"),
        'g' to listOf("ğ", "ģ"),
        'r' to listOf("ŕ", "ř"),
        't' to listOf("ť", "ţ"),
        'w' to listOf("ŵ"),
        ',' to listOf(",", "'", "\"", "“", "”", "„"),
        '.' to listOf(".", "!", "?", ";", ":", "…"),
        '?' to listOf("?", "¿"),
        '!' to listOf("!", "¡"),
        '\'' to listOf("'", "`", "´", "’"),
        '"' to listOf("\"", "”", "„", "«", "»"),
        '-' to listOf("-", "–", "—", "·"),
        '/' to listOf("/", "\\", "÷"),
        '1' to listOf("1", "¹", "½", "⅓", "¼"),
        '0' to listOf("0", "°", "⁰")
    )

    private var longPressActive = false
    private var longPressKeyId = -1
    private var longPressAlts: List<String> = emptyList()
    private var longPressSelected = 0
    private val longPressRunnable = object : Runnable {
        override fun run() {
            if (pressedId >= 0 && pressedId < keys.size) {
                val k = keys[pressedId]
                val ch = k.spec.text.firstOrNull()?.lowercaseChar()
                val alts = ch?.let { altMap[it] } ?: emptyList()
                if (alts.isNotEmpty()) {
                    longPressActive = true
                    longPressKeyId = pressedId
                    longPressAlts = listOf(k.spec.text) + alts
                    longPressSelected = 0
                    try {
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    } catch (e: Exception) {
                        // ignore
                    }
                    handler.removeCallbacks(repeatRunnable)
                    invalidate()
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutKeys()
    }

    fun layoutKeys() {
        keys.clear()
        val w = width
        val h = height
        if (w <= 0 || h <= 0) return
        when (mode) {
            KeyboardLayout.MODE_EMOJI -> layoutEmoji(w, h)
            KeyboardLayout.MODE_CALC -> layoutCalc(w, h)
            else -> layoutNormal(w, h)
        }
    }

    private fun buildRow(row: List<KeySpec>, y: Int, rowH: Int, stripX: Int, stripW: Int) {
        val totalWeight = row.sumOf { it.weight.toDouble() }.toFloat()
        val n = row.size
        val gaps = keyGap * (n - 1)
        val avail = stripW - 2 * margin - gaps
        var x = stripX + margin
        for (i in row.indices) {
            val spec = row[i]
            val kw = (spec.weight / totalWeight * avail).toInt()
            val key = RK(spec, x, y, kw, rowH)
            keys.add(key)
            x += kw + keyGap
        }
    }

    private fun oneHandedStrip(w: Int): Pair<Int, Int> {
        return when {
            oneHandedSide < 0 -> {
                val sw = (w * 0.72f).toInt()
                margin to sw - 2 * margin
            }
            oneHandedSide > 0 -> {
                val sw = (w * 0.72f).toInt()
                (w - sw - margin) to (sw - 2 * margin)
            }
            else -> margin to (w - 2 * margin)
        }
    }

    private fun layoutNormal(w: Int, h: Int) {
        val sbH = suggestionsBarHeight()
        keys.add(RK(KeySpec(KeyKind.SPACE), 0, 0, w, sbH))
        val numberRow = numberRowEnabled || isLandscape
        val rows = KeyboardLayout.rowsFor(mode, numberRow)
        val totalH = h - sbH
        val rowsH = (totalH / rows.size * keyHeightMult).toInt().coerceAtLeast(dp(28f))
        val usedH = rowsH * rows.size + keyGap * (rows.size - 1)
        val top0 = suggestionsBarH + ((totalH - usedH) / 2).coerceAtLeast(0)
        val (stripX, stripW) = oneHandedStrip(w)
        for (r in rows.indices) {
            val y = top0 + (rowsH + keyGap) * r
            buildRow(rows[r], y, rowsH, stripX, stripW)
        }
        if (oneHandedSide != 0) {
            keys.add(RK(KeySpec(KeyKind.HIDE), 0, 0, margin + dp(2f), h))
        }
    }

    private fun layoutCalc(w: Int, h: Int) {
        showSuggestions = false
        keys.add(RK(KeySpec(KeyKind.CHAR), 0, 0, w, displayH))
        val rows = KeyboardLayout.CALC
        val rowsH = (h - displayH) / rows.size
        for (r in rows.indices) {
            val y = displayH + (rowsH + keyGap) * r
            buildRow(rows[r], y, rowsH, margin, w - 2 * margin)
        }
    }

    private fun layoutEmoji(w: Int, h: Int) {
        showSuggestions = false
        val gridH = h - categoryBarH - actionBarH
        val emojiRowH = gridH / 4
        val emojiList = EmojiData.pageEmoji(emojiCategory, emojiPage)
        var idx = 0
        for (r in 0 until 4) {
            val y = (emojiRowH + keyGap) * r
            for (col in 0 until 10) {
                val x = margin + col * ((w - 2 * margin) / 10f)
                val cw = ((w - 2 * margin) / 10f)
                val spec = if (idx < emojiList.size) KeySpec(KeyKind.EMOJI, emojiList[idx]) else KeySpec(KeyKind.CHAR, text = " ")
                idx++
                keys.add(RK(spec, x.toInt(), y, cw.toInt(), emojiRowH))
            }
        }
        val catY = gridH
        val categories = ArrayList<KeySpec>()
        categories.add(KeySpec(KeyKind.EMOJI_PREV, "◀"))
        for (i in 0 until EmojiData.CATEGORY_COUNT) {
            categories.add(KeySpec(KeyKind.EMOJI_CATEGORY, EmojiData.categoryIcon(i)))
        }
        categories.add(KeySpec(KeyKind.EMOJI_NEXT, "▶"))
        val cellW = w / 9f
        for (i in categories.indices) {
            val x = (cellW * i).toInt()
            val cw = cellW.toInt()
            keys.add(RK(categories[i], x, catY, cw, categoryBarH))
        }
        val actionY = catY + categoryBarH
        val actionRow = listOf(
            KeySpec(KeyKind.EMOJI_SEARCH, "🔍"), KeySpec(KeyKind.HIDE, "▾"), KeySpec(KeyKind.MODE_LETTERS, "abc"),
            KeySpec(KeyKind.CLIPBOARD, "⧉"), KeySpec(KeyKind.BACKSPACE, "⌫"),
            KeySpec(KeyKind.ENTER, "⏎")
        )
        buildRow(actionRow, actionY, actionBarH, margin, w - 2 * margin)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(theme.bg)
        drawPanels(canvas)
        for (k in keys) {
            drawKey(canvas, k)
        }
        if (longPressActive && longPressKeyId >= 0 && longPressKeyId < keys.size) {
            drawLongPressPopup(canvas, keys[longPressKeyId])
        } else if (pressedId >= 0 && pressedId < keys.size) {
            val k = keys[pressedId]
            if (k.spec.kind == KeyKind.CHAR && k.spec.text.length <= 1) {
                drawPopup(canvas, k)
            }
        }
    }

    private fun drawPanels(canvas: Canvas) {
        when (mode) {
            KeyboardLayout.MODE_LETTERS, KeyboardLayout.MODE_SYMBOLS_1, KeyboardLayout.MODE_SYMBOLS_2 -> {
                panelPaint.color = theme.panel
                val barH = suggestionsBarHeight()
                canvas.drawRect(0f, 0f, width.toFloat(), barH.toFloat(), panelPaint)
                if (emojiSearchActive && mode == KeyboardLayout.MODE_LETTERS) {
                    drawEmojiSearchBar(canvas, barH)
                } else {
                    drawSuggestionsBar(canvas)
                }
            }
            KeyboardLayout.MODE_CALC -> {
                panelPaint.color = theme.panel
                canvas.drawRect(0f, 0f, width.toFloat(), displayH.toFloat(), panelPaint)
                drawCalcDisplay(canvas)
            }
        }
    }

    private fun drawSuggestionsBar(canvas: Canvas) {
        val s = suggestions
        val prefix = typedPrefix
        hintPaint.color = theme.dimText
        hintPaint.textSize = sp(12f)
        if (prefix.isNotBlank()) {
            val shown = if (prefix.length > 34) "…" + prefix.takeLast(33) else prefix
            canvas.drawText(shown, (margin + dp(8f)).toFloat(), suggestionsBarH / 2f - (hintPaint.ascent() + hintPaint.descent()) / 2f, hintPaint)
        }
        if (!showSuggestions) return
        if (s.isEmpty()) return
        val startX = (dp(120f)).toFloat() + if (prefix.isBlank()) 0 else dp(60f)
        val cellW = (width - startX - margin) / s.size
        textPaint.color = theme.accent
        textPaint.textSize = sp(14f)
        for (i in s.indices) {
            val label = s[i]
            val cx = startX + cellW * i + cellW / 2f
            canvas.drawText(label, cx - textPaint.measureText(label) / 2f, suggestionsBarH / 2f - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint)
        }
    }

    private fun drawEmojiSearchBar(canvas: Canvas, barH: Int) {
        val cy = barH / 2f - (hintPaint.ascent() + hintPaint.descent()) / 2f

        hintPaint.textSize = sp(12f)
        if (emojiQuery.isBlank()) {
            hintPaint.color = theme.dimText
            canvas.drawText(SEARCH_PLACEHOLDER, (margin + dp(8f)).toFloat(), cy, hintPaint)
        } else {
            hintPaint.color = theme.accent
            val shown = if (emojiQuery.length > 18) emojiQuery.take(17) + "…" else emojiQuery
            canvas.drawText("🔍  " + shown, (margin + dp(4f)).toFloat(), cy, hintPaint)
        }

        if (emojiQuery.isNotEmpty()) {
            panelPaint.color = theme.keyDark
            canvas.drawRoundRect(
                RectF(queryW + dp(4f), barH / 2f - dp(10f), queryW + clearW - dp(4f), barH / 2f + dp(10f)),
                dp(6f).toFloat(), dp(6f).toFloat(), panelPaint
            )
            hintPaint.color = theme.keyText
            hintPaint.textSize = sp(14f)
            canvas.drawText("✕", queryW + clearW / 2f - hintPaint.measureText("✕") / 2f, cy, hintPaint)
        }

        val list = emojiResults
        if (list.isEmpty()) {
            if (emojiQuery.isNotBlank()) {
                hintPaint.color = theme.dimText
                hintPaint.textSize = sp(11f)
                canvas.drawText("no matches", matchesX, cy, hintPaint)
            }
            return
        }
        val avail = (width - matchesX - margin).toFloat()
        val cellW = avail / list.size
        textPaint.textSize = sp(18f)
        for (i in list.indices) {
            val cx = matchesX + cellW * i + cellW / 2f
            if (i == 0) {
                keyPaint.color = theme.accent
                val cell = RectF(matchesX + cellW * i + dp(1f), dp(2f).toFloat(), matchesX + cellW * (i + 1) - dp(1f), (barH - dp(2f)).toFloat())
                canvas.drawRoundRect(cell, dp(6f).toFloat(), dp(6f).toFloat(), keyPaint)
            }
            textPaint.color = if (i == 0) theme.accentText else theme.keyText
            canvas.drawText(list[i], cx - textPaint.measureText(list[i]) / 2f, barH / 2f - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint)
        }
    }

    private fun drawCalcDisplay(canvas: Canvas) {
        val text = listener?.getCalcDisplay() ?: "0"
        panelPaint.color = theme.panel
        canvas.drawRect(0f, 0f, width.toFloat(), displayH.toFloat(), panelPaint)
        var size = sp(20f)
        textPaint.color = theme.keyText
        textPaint.textSize = size
        while (size > sp(9f) && textPaint.measureText(text) > width - dp(16f)) {
            size -= sp(1f)
            textPaint.textSize = size
        }
        val y = displayH / 2f - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText(text, width - margin - dp(8f) - textPaint.measureText(text), y, textPaint)
        hintPaint.color = theme.dimText
        hintPaint.textSize = sp(10f)
        canvas.drawText("=", (margin + dp(8f)).toFloat(), y, hintPaint)
    }

    private fun drawKey(canvas: Canvas, k: RK) {
        val spec = k.spec
        val isSpecial = spec.kind in SPECIAL
        val accentActive = (spec.kind == KeyKind.SHIFT && (shiftState != 0 || capsLock)) ||
            (spec.kind == KeyKind.ENTER) || (spec.kind == KeyKind.CALC_EQUALS) || spec.kind == KeyKind.EMOJI_CATEGORY

        keyPaint.color = when {
            accentActive -> theme.accent
            k.pressed -> theme.keyDark
            isSpecial -> theme.keyDark
            else -> theme.key
        }

        round.set(k.x + keyGap / 2f, k.y + keyGap / 2f, k.x + k.w - keyGap / 2f, k.y + k.h - keyGap / 2f)
        canvas.drawRoundRect(round, dp(6f).toFloat(), dp(6f).toFloat(), keyPaint)

        textPaint.color = if (accentActive) theme.accentText else theme.keyText
        textPaint.textSize = sp(16f)
        if (spec.kind == KeyKind.SPACE) {
            hintPaint.color = theme.dimText
            hintPaint.textSize = sp(10f)
            val label = "space"
            canvas.drawText(label, k.x + k.w / 2f - hintPaint.measureText(label) / 2f, k.y + k.h / 2f - (hintPaint.ascent() + hintPaint.descent()) / 2f, hintPaint)
            return
        }

        val label = labelFor(spec)
        if (label == null || label.isEmpty()) return
        if (label.isBlank()) return

        val needShrink = spec.kind == KeyKind.BACKSPACE || spec.kind == KeyKind.ENTER || spec.kind == KeyKind.CALC_OP || spec.kind == KeyKind.EMOJI_CATEGORY || spec.kind in EDIT_SHRINK
        if (needShrink) textPaint.textSize = sp(14f)
        val tw = textPaint.measureText(label)
        val cx = k.x + k.w / 2f - tw / 2f
        val cy = k.y + k.h / 2f - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText(label, cx, cy, textPaint)

        if (spec.kind == KeyKind.SHIFT && capsLock) {
            hintPaint.color = theme.accentText
            hintPaint.textSize = sp(8f)
            val dot = "•"
            canvas.drawText(dot, k.x + k.w / 2f - hintPaint.measureText(dot) / 2f, k.y + dp(10f).toFloat(), hintPaint)
        }
        if (spec.kind == KeyKind.CALC_EQUALS) {
            hintPaint.color = theme.accentText
            hintPaint.textSize = sp(8f)
            val hint = "0"
            canvas.drawText(hint, k.x + k.w - dp(6f) - hintPaint.measureText(hint), (k.y + k.h - dp(3f)).toFloat(), hintPaint)
        }
    }

    private fun labelFor(spec: KeySpec): String? = when (spec.kind) {
        KeyKind.CHAR -> {
            val t = spec.text
            if (t.length == 1 && t[0] in 'A'..'Z' && mode == KeyboardLayout.MODE_LETTERS && shiftState == 0 && !capsLock) {
                t.lowercase()
            } else {
                t
            }
        }
        KeyKind.SHIFT -> if (capsLock) "⇪" else "⇧"
        KeyKind.BACKSPACE -> "⌫"
        KeyKind.ENTER -> "⏎"
        KeyKind.SPACE -> " "
        KeyKind.MODE_LETTERS -> "abc"
        KeyKind.MODE_SYMBOLS_1 -> "?123"
        KeyKind.MODE_SYMBOLS_2 -> "=\\<"
        KeyKind.MODE_EMOJI -> "😀"
        KeyKind.MODE_CALC -> "∑"
        KeyKind.SETTINGS -> "⚙"
        KeyKind.CLIPBOARD -> "⧉"
        KeyKind.TOOLS -> "✎"
        KeyKind.HIDE -> "▾"
        KeyKind.EMOJI_PREV -> "◀"
        KeyKind.EMOJI_NEXT -> "▶"
        KeyKind.EMOJI_CATEGORY -> spec.text
        KeyKind.EMOJI_SEARCH -> "🔍"
        KeyKind.EMOJI_CLEAR -> "✕"
        KeyKind.CALC_AC -> "AC"
        KeyKind.CALC_BSP -> "⌫"
        KeyKind.CALC_EQUALS -> "="
        KeyKind.CALC_OP -> spec.text
        KeyKind.CURSOR_LEFT -> "◀"
        KeyKind.CURSOR_RIGHT -> "▶"
        KeyKind.CURSOR_HOME -> "⇤"
        KeyKind.CURSOR_END -> "⇥"
        KeyKind.COPY -> "⧉"
        KeyKind.CUT -> "✂"
        KeyKind.PASTE -> "📋"
        KeyKind.SELECT_ALL -> "⌑"
        KeyKind.UNDO -> "↶"
        KeyKind.REDO -> "↷"
        KeyKind.NEWLINE -> "⏎"
        KeyKind.CALC_HISTORY -> "🕘"
        else -> spec.text.ifEmpty { null }
    }

    private fun drawPopup(canvas: Canvas, k: RK) {
        val text = k.spec.text
        if (text.isBlank()) return
        val pw = dp(52f)
        val ph = dp(50f)
        var px = k.x + k.w / 2f - pw / 2f
        val py = k.y - ph - dp(4f).toFloat()
        if (px < margin) px = margin.toFloat()
        if (px + pw > width - margin) px = (width - margin - pw).toFloat()
        val popupPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        popupPaint.color = theme.keyDark
        canvas.drawRoundRect(px, py, px + pw, py + ph, dp(6f).toFloat(), dp(6f).toFloat(), popupPaint)
        val tp = Paint(Paint.ANTI_ALIAS_FLAG)
        tp.color = theme.keyText
        tp.textSize = sp(22f)
        canvas.drawText(text, px + pw / 2f - tp.measureText(text) / 2f, py + ph / 2f - (tp.ascent() + tp.descent()) / 2f, tp)
    }

    private fun drawLongPressPopup(canvas: Canvas, k: RK) {
        val alts = longPressAlts
        if (alts.isEmpty()) return
        val cellW = dp(46f)
        val popupW = cellW * alts.size + dp(8f)
        var px = k.x + k.w / 2f - popupW / 2f
        px = px.coerceIn(margin.toFloat(), (width - popupW - margin).toFloat())
        val popupH = dp(48f)
        val py = k.y - popupH - dp(4f).toFloat()

        val popupPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        popupPaint.color = theme.keyDark
        val rect = RectF(px, py, px + popupW, py + popupH)
        canvas.drawRoundRect(rect, dp(6f).toFloat(), dp(6f).toFloat(), popupPaint)

        val tp = Paint(Paint.ANTI_ALIAS_FLAG)
        tp.textSize = sp(20f)
        for (i in alts.indices) {
            val cx = px + dp(4f) + cellW * i
            if (i == longPressSelected) {
                keyPaint.color = theme.accent
                canvas.drawRoundRect(
                    RectF(cx, py + dp(2f), cx + cellW - dp(2f), py + popupH - dp(2f)),
                    dp(4f).toFloat(), dp(4f).toFloat(), keyPaint
                )
                tp.color = theme.accentText
            } else {
                tp.color = theme.keyText
            }
            val label = alts[i]
            canvas.drawText(label, cx + cellW / 2f - tp.measureText(label) / 2f, py + popupH / 2f - (tp.ascent() + tp.descent()) / 2f, tp)
        }
    }

    private fun updateLongPressSelection(x: Float) {
        if (longPressAlts.isEmpty()) return
        val cellW = dp(46f)
        val popupW = cellW * longPressAlts.size + dp(8f)
        var px = keys[longPressKeyId].x + keys[longPressKeyId].w / 2f - popupW / 2f
        px = px.coerceIn(margin.toFloat(), (width - popupW - margin).toFloat())
        val idx = ((x - px - dp(4f)) / cellW).toInt().coerceIn(0, longPressAlts.size - 1)
        if (idx != longPressSelected) {
            longPressSelected = idx
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressedId = findKey(event.x, event.y)
                onPressed(pressedId)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (longPressActive) {
                    updateLongPressSelection(event.x)
                    return true
                }
                val id = findKey(event.x, event.y)
                if (id != pressedId) {
                    val prev = if (pressedId in keys.indices) keys[pressedId] else null
                    prev?.pressed = false
                    pressedId = id
                    onPressed(id)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (longPressActive) {
                    cancelLongPressTimer()
                    val k = if (longPressKeyId in keys.indices) keys[longPressKeyId] else null
                    val alts = longPressAlts
                    if (k != null) {
                        k.pressed = false
                        val sel = longPressSelected.coerceIn(0, alts.size - 1)
                        if (sel in alts.indices && alts[sel] != k.spec.text) {
                            listener?.onKey(KeySpec(KeyKind.CHAR, alts[sel]))
                        } else {
                            listener?.onKey(k.spec)
                        }
                    }
                    longPressKeyId = -1
                    pressedId = -1
                    invalidate()
                    return true
                }
                val id = pressedId
                cancelRepeat()
                if (id in keys.indices) {
                    keys[id].pressed = false
                    val spec = keys[id].spec
                    if (containsKeySuggestion(event.x, event.y)) {
                        val s = suggestionAt(event.x, event.y)
                        if (s != null) listener?.onSuggestion(s)
                    } else {
                        fire(spec, id)
                    }
                }
                pressedId = -1
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                cancelLongPressTimer()
                cancelRepeat()
                if (pressedId in keys.indices) keys[pressedId].pressed = false
                pressedId = -1
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onPressed(id: Int) {
        if (id !in keys.indices) return
        val k = keys[id]
        k.pressed = true
        invalidate()
        val spec = k.spec
        handler.removeCallbacks(longPressRunnable)
        longPressActive = false
        if (spec.kind == KeyKind.BACKSPACE || spec.kind == KeyKind.CALC_BSP) {
            repeatId = id
            handler.removeCallbacks(repeatRunnable)
            handler.postDelayed(repeatRunnable, 350)
        } else if (spec.kind == KeyKind.CHAR && spec.text.length == 1 && spec.text.isNotBlank()) {
            longPressKeyId = -1
            handler.postDelayed(longPressRunnable, LONG_PRESS_MS)
        }
    }

    private val repeatRunnable = object : Runnable {
        override fun run() {
            if (repeatId in keys.indices) {
                val k = keys[repeatId]
                if (k.pressed) {
                    fire(k.spec, repeatId)
                    handler.postDelayed(this, 42)
                } else {
                    repeatId = -1
                }
            }
        }
    }

    private fun cancelRepeat() {
        repeatId = -1
        handler.removeCallbacks(repeatRunnable)
    }

    private fun cancelLongPressTimer() {
        handler.removeCallbacks(longPressRunnable)
    }

    private fun findKey(x: Float, y: Float): Int {
        for (i in keys.indices) {
            if (keys[i].contains(x, y)) return i
        }
        return -1
    }

    private fun fire(spec: KeySpec, id: Int) {
        listener?.onKey(spec)
    }

    private fun containsKeySuggestion(x: Float, y: Float): Boolean {
        if (mode == KeyboardLayout.MODE_LETTERS || mode == KeyboardLayout.MODE_SYMBOLS_1 || mode == KeyboardLayout.MODE_SYMBOLS_2) {
            return y <= suggestionsBarHeight()
        }
        return false
    }

    private fun suggestionAt(x: Float, y: Float): String? {
        if (y > suggestionsBarHeight()) return null
        if (emojiSearchActive) return searchHitAt(x, y)
        val s = suggestions
        if (s.isEmpty()) return null
        val startX = dp(120f) + if (typedPrefix.isBlank()) 0 else dp(60f)
        val cellW = (width - startX - margin) / s.size
        for (i in s.indices) {
            val sx = startX + cellW * i
            if (x >= sx && x <= sx + cellW) return s[i]
        }
        return null
    }

    private fun searchHitAt(x: Float, y: Float): String? {
        val sbH = suggestionsBarHeight()
        if (y > sbH) return null
        if (emojiQuery.isNotEmpty() && x >= queryW && x <= queryW + clearW) return SEARCH_CLEAR
        if (x < matchesX) return null
        val list = emojiResults
        if (list.isEmpty()) return null
        val avail = (width - matchesX - margin).toFloat()
        val cellW = avail / list.size
        val i = ((x - matchesX) / cellW).toInt()
        if (i in list.indices) return list[i]
        return null
    }

    fun setEmojiCategory(index: Int) {
        emojiCategory = ((index % EmojiData.CATEGORY_COUNT) + EmojiData.CATEGORY_COUNT) % EmojiData.CATEGORY_COUNT
        emojiPage = 0
        layoutKeys()
        invalidate()
    }

    fun pageEmoji(direction: Int) {
        emojiPage += direction
        layoutKeys()
        invalidate()
    }

    fun setShowSuggestions(enabled: Boolean) {
        showSuggestions = enabled
        invalidate()
    }

    companion object {
        const val SEARCH_CLEAR = "\uFFF0"
        private const val SEARCH_PLACEHOLDER = "🔍  Search emoji (heart, dog, money…)"
        private val SPECIAL = setOf(
            KeyKind.SHIFT, KeyKind.BACKSPACE, KeyKind.ENTER, KeyKind.SPACE,
            KeyKind.MODE_LETTERS, KeyKind.MODE_SYMBOLS_1, KeyKind.MODE_SYMBOLS_2,
            KeyKind.MODE_EMOJI, KeyKind.MODE_CALC, KeyKind.SETTINGS, KeyKind.CLIPBOARD,
            KeyKind.TOOLS, KeyKind.HIDE, KeyKind.EMOJI_PREV, KeyKind.EMOJI_NEXT,
            KeyKind.EMOJI_CATEGORY, KeyKind.EMOJI_SEARCH, KeyKind.EMOJI_CLEAR,
            KeyKind.CALC_AC, KeyKind.CALC_BSP,
            KeyKind.CALC_EQUALS, KeyKind.CALC_OP,
            KeyKind.CURSOR_LEFT, KeyKind.CURSOR_RIGHT, KeyKind.CURSOR_HOME, KeyKind.CURSOR_END,
            KeyKind.COPY, KeyKind.CUT, KeyKind.PASTE, KeyKind.SELECT_ALL, KeyKind.UNDO, KeyKind.REDO,
            KeyKind.NEWLINE, KeyKind.CALC_HISTORY
        )
        private val EDIT_SHRINK = setOf(
            KeyKind.CURSOR_LEFT, KeyKind.CURSOR_RIGHT, KeyKind.CURSOR_HOME, KeyKind.CURSOR_END,
            KeyKind.COPY, KeyKind.CUT, KeyKind.PASTE, KeyKind.SELECT_ALL, KeyKind.UNDO, KeyKind.REDO,
            KeyKind.NEWLINE, KeyKind.CALC_HISTORY
        )
    }
}