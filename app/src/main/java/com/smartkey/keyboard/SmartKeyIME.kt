package com.smartkey.keyboard

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.SystemClock
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodSubtype

class SmartKeyIME : InputMethodService(), KeyboardListener {

    private lateinit var prefs: SmartPrefs
    private lateinit var suggestions: SuggestionsEngine
    private lateinit var shortcuts: TextShortcuts
    private lateinit var clipboard: SmartClipboard
    private lateinit var haptics: HapticManager
    private val sounds = SoundManager(this)
    private val calculator = CalculatorEngine()

    private var keyboardView: KeyboardView? = null
    private var clipboardPanel: ClipboardPanel? = null
    private var toolsPanel: ToolsPanel? = null

    private var currentMode = KeyboardLayout.MODE_LETTERS
    private var wordBuffer = StringBuilder()
    private var capitalizeNext = true
    private var shiftState = 0
    private var capsLock = false
    private var privateMode = false
    private var lastSpaceTime = 0L
    private var emojiSearchActive = false
    private var emojiQuery = ""
    private val emojiResults = ArrayList<String>()

    override fun onCreate() {
        super.onCreate()
        prefs = SmartPrefs(this)
        suggestions = SuggestionsEngine(prefs)
        shortcuts = TextShortcuts(prefs)
        clipboard = SmartClipboard(this, prefs)
        haptics = HapticManager(this)
        sounds.init()
    }

    override fun onCreateInputView(): View {
        val view = KeyboardView(this)
        view.listener = this
        view.theme = resolveTheme()
        keyboardView = view
        applyPrefsToView()
        applyModeToView(view)
        return view
    }

    override fun onStartInputView(editorInfo: EditorInfo, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        resetState()
        privateMode = isPrivateField(editorInfo)
        val suggestionEnabled = prefs.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true) && !privateMode
        keyboardView?.setShowSuggestions(suggestionEnabled)
        keyboardView?.let {
            it.theme = resolveTheme()
            it.typedPrefix = ""
            applyPrefsToView()
            applyModeToView(it)
            switchMode(defaultModeFor(editorInfo))
        }
        val before = currentInputConnection?.getTextBeforeCursor(300, 0)?.toString() ?: ""
        capitalizeNext = suggestions.shouldCapitalize(before)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    private fun resetState() {
        currentMode = KeyboardLayout.MODE_LETTERS
        wordBuffer = StringBuilder()
        shiftState = 0
        capsLock = false
        capitalizeNext = true
        privateMode = false
        lastSpaceTime = 0L
        emojiSearchActive = false
        emojiQuery = ""
        emojiResults.clear()
        keyboardView?.emojiSearchActive = false
        keyboardView?.emojiQuery = ""
        keyboardView?.emojiResults = emptyList()
        hidePanels()
    }

    private fun isSystemDark(): Boolean =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    private fun resolveTheme(): Theme {
        val name = prefs.getString(SmartPrefs.KEY_THEME) ?: ThemeConfig.KEY_LIGHT
        return ThemeConfig.byName(name, isSystemDark())
    }

    private fun isPrivateField(info: EditorInfo): Boolean {
        val type = info.inputType
        if (info.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING != 0) return true
        val variation = type and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    private fun applyModeToView(view: KeyboardView) {
        view.shiftState = if (capsLock) 1 else shiftState
        view.capsLock = capsLock
        view.invalidate()
    }

    private fun feedback() {
        if (prefs.getBoolean(SmartPrefs.KEY_HAPTICS, true)) {
            haptics.buzz()
        }
        if (prefs.getBoolean(SmartPrefs.KEY_SOUNDS, true)) {
            sounds.play()
        }
    }

    override fun onKey(spec: KeySpec) {
        feedback()
        if (!privateMode) clipboard.capture()
        when (spec.kind) {
            KeyKind.CHAR -> onCharKey(spec.text)
            KeyKind.SHIFT -> onShiftKey()
            KeyKind.BACKSPACE -> onBackspace()
            KeyKind.SPACE -> onSpace()
            KeyKind.ENTER -> onEnter()
            KeyKind.NEWLINE -> {
                currentInputConnection?.commitText("\n", 1)
                capitalizeNext = true
                updateViewShift()
                updateCandidates()
            }
            KeyKind.MODE_LETTERS -> switchMode(KeyboardLayout.MODE_LETTERS)
            KeyKind.MODE_SYMBOLS_1 -> switchMode(KeyboardLayout.MODE_SYMBOLS_1)
            KeyKind.MODE_SYMBOLS_2 -> switchMode(KeyboardLayout.MODE_SYMBOLS_2)
            KeyKind.MODE_EMOJI -> switchMode(KeyboardLayout.MODE_EMOJI)
            KeyKind.MODE_CALC -> switchMode(KeyboardLayout.MODE_CALC)
            KeyKind.EMOJI -> onEmoji(spec.text)
            KeyKind.EMOJI_PREV -> keyboardView?.pageEmoji(-1)
            KeyKind.EMOJI_NEXT -> keyboardView?.pageEmoji(1)
            KeyKind.EMOJI_CATEGORY -> keyboardView?.setEmojiCategory(EmojiData.categoryIndexOf(spec.text))
            KeyKind.EMOJI_SEARCH -> toggleEmojiSearch()
            KeyKind.EMOJI_CLEAR -> {
                emojiQuery = ""
                updateEmojiResults()
            }
            KeyKind.CLIPBOARD -> showClipboardPanel()
            KeyKind.TOOLS -> showToolsPanel()
            KeyKind.SETTINGS -> openSettings()
            KeyKind.HIDE -> requestHideSelf(0)
            KeyKind.CALC_AC -> calcClear()
            KeyKind.CALC_BSP -> calcBackspace()
            KeyKind.CALC_EQUALS -> calcEquals()
            KeyKind.CALC_OP -> calcOp(spec.text)
            KeyKind.CALC_HISTORY -> calcHistoryNext()
            KeyKind.CURSOR_LEFT -> sendKey(KeyEvent.KEYCODE_DPAD_LEFT)
            KeyKind.CURSOR_RIGHT -> sendKey(KeyEvent.KEYCODE_DPAD_RIGHT)
            KeyKind.CURSOR_HOME -> sendKey(KeyEvent.KEYCODE_MOVE_HOME)
            KeyKind.CURSOR_END -> sendKey(KeyEvent.KEYCODE_MOVE_END)
            KeyKind.COPY -> copySelected()
            KeyKind.CUT -> cutSelected()
            KeyKind.PASTE -> pasteFromSystemClipboard()
            KeyKind.SELECT_ALL -> selectAll()
            KeyKind.UNDO -> runContextAction(android.R.id.undo)
            KeyKind.REDO -> runContextAction(android.R.id.redo)
            else -> Unit
        }
    }

    private fun applyPrefsToView() {
        val view = keyboardView ?: return
        view.numberRowEnabled = prefs.getBoolean(SmartPrefs.KEY_NUMBER_ROW, false)
        view.oneHandedSide = prefs.getInt(SmartPrefs.KEY_ONE_HANDED, 0)
        view.keyHeightMult = when (prefs.getString(SmartPrefs.KEY_KEY_HEIGHT)) {
            "small" -> 0.8f
            "large" -> 1.15f
            else -> 1f
        }
    }

    /**
     * Pick the starting keyboard mode based on the editor's input type so the
     * keyboard adapts to number-only, phone, URL, email and datetime fields.
     */
    private fun defaultModeFor(info: EditorInfo): Int {
        val type = info.inputType
        if (privateMode) return KeyboardLayout.MODE_LETTERS
        val cls = type and InputType.TYPE_MASK_CLASS
        return when (cls) {
            InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_PHONE, InputType.TYPE_CLASS_DATETIME ->
                KeyboardLayout.MODE_SYMBOLS_1
            else -> KeyboardLayout.MODE_LETTERS
        }
    }

    private fun sendKey(code: Int) {
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
    }

    private fun runContextAction(id: Int) {
        currentInputConnection?.performContextMenuAction(id)
    }

    private fun selectAll() {
        val ic = currentInputConnection ?: return
        ic.performContextMenuAction(android.R.id.selectAll)
    }

    private fun copySelected() {
        val ic = currentInputConnection ?: return
        val sel = ic.getSelectedText(0)?.toString()
        if (!sel.isNullOrEmpty()) {
            clipboard.copy(sel)
            clipboard.push(sel)
        } else {
            val word = lastWordBeforeCursor()
            if (word != null) {
                clipboard.copy(word)
                clipboard.push(word)
            }
        }
    }

    private fun cutSelected() {
        val ic = currentInputConnection ?: return
        val sel = ic.getSelectedText(0)?.toString()
        if (sel.isNullOrEmpty()) {
            val word = lastWordBeforeCursor() ?: return
            ic.deleteSurroundingText(word.length, 0)
            clipboard.copy(word)
            clipboard.push(word)
            return
        }
        ic.commitText("", 1)
        clipboard.copy(sel)
        clipboard.push(sel)
    }

    private fun pasteFromSystemClipboard() {
        val ic = currentInputConnection ?: return
        val text = clipboard.systemText() ?: return
        ic.commitText(text, 1)
        clipboard.push(text)
    }

    private fun calcHistoryNext() {
        if (!calculator.hasHistory) return
        val entry = calculator.recallHistory(0)
        keyboardView?.invalidate()
    }

    private fun onCharKey(text: String) {
        if (text.isBlank()) return
        if (emojiSearchActive) {
            if (text.length == 1 && (text[0].isLetterOrDigit() || text[0] == ' ')) {
                appendToEmojiQuery(text[0].lowercaseChar())
            }
            return
        }
        if (currentMode == KeyboardLayout.MODE_CALC) {
            calculator.append(text)
            keyboardView?.invalidate()
            return
        }
        val ic = currentInputConnection ?: return
        val capAuto = capitalizeNext && prefs.getBoolean(SmartPrefs.KEY_AUTOCAP, true) && !privateMode
        var out = text
        if (text.length == 1 && text[0] in 'a'..'z') {
            val ch = text[0]
            val target = when {
                capsLock -> ch.uppercaseChar()
                shiftState == 1 -> ch.uppercaseChar()
                capAuto -> ch.uppercaseChar()
                else -> ch
            }
            out = target.toString()
            if (target == ch) {
                if (shiftState == 1) {
                    shiftState = 0
                    capitalizeNext = false
                }
            } else if (shiftState == 1) {
                shiftState = 0
            }
        }
        ic.commitText(out, 1)
        if (text.length == 1 && text[0] in 'a'..'z') {
            wordBuffer.append(text[0])
            if (shiftState == 1) shiftState = 0
        } else {
            if (text in listOf(".", "!", "?")) {
                if (!maybeExpandShortcut()) {
                    maybeAutocorrect()
                    commitWordToLearning()
                }
                capitalizeNext = true
            } else if (!(text.firstOrNull()?.isLetterOrDigit() ?: false)) {
                if (!maybeExpandShortcut()) {
                    maybeAutocorrect()
                    commitWordToLearning()
                }
            }
        }
        updateViewShift()
        updateCandidates()
    }

    private fun onShiftKey() {
        when {
            capsLock -> {
                capsLock = false
                shiftState = 0
            }
            shiftState == 1 -> {
                shiftState = 0
                capsLock = true
            }
            else -> shiftState = 1
        }
        updateViewShift()
    }

    private fun onBackspace() {
        wordBuffer = StringBuilder()
        if (emojiSearchActive) {
            if (emojiQuery.isNotEmpty()) {
                emojiQuery = emojiQuery.dropLast(1)
                updateEmojiResults()
            }
            return
        }
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(1, 0)?.toString() ?: ""
        ic.deleteSurroundingText(1, 0)
        if (before == "\n") capitalizeNext = true
        refreshSuggestions()
    }

    private fun onSpace() {
        if (emojiSearchActive) {
            if (emojiQuery.isNotBlank() && !emojiQuery.endsWith(" ")) {
                emojiQuery += " "
                updateEmojiResults()
            }
            return
        }
        if (!maybeExpandShortcut()) {
            maybeAutocorrect()
            commitWordToLearning()
        }
        val ic = currentInputConnection ?: return
        val now = SystemClock.elapsedRealtime()
        val doubleSpace = prefs.getBoolean(SmartPrefs.KEY_DOUBLE_SPACE, true) && !privateMode &&
            now - lastSpaceTime < 600
        val before = ic.getTextBeforeCursor(1, 0)?.toString() ?: ""
        lastSpaceTime = now
        if (doubleSpace && before == " ") {
            ic.beginBatchEdit()
            try {
                ic.deleteSurroundingText(1, 0)
                ic.commitText(". ", 1)
            } finally {
                ic.endBatchEdit()
            }
            capitalizeNext = true
            wordBuffer = StringBuilder()
            updateViewShift()
            updateCandidates()
            return
        }
        ic.commitText(" ", 1)
        val lastChar = before.lastOrNull()
        capitalizeNext = lastChar == '.' || lastChar == '!' || lastChar == '?' || lastChar == '\n'
        wordBuffer = StringBuilder()
        updateViewShift()
        updateCandidates()
    }

    private fun maybeExpandShortcut(): Boolean {
        if (privateMode) return false
        if (!prefs.getBoolean(SmartPrefs.KEY_SHORTCUTS_ENABLED, true)) return false
        val ic = currentInputConnection ?: return false
        val typed = wordBuffer.toString()
        if (typed.isBlank()) return false
        val replacement = shortcuts.expand(typed) ?: return false
        val before = ic.getTextBeforeCursor(typed.length, 0)?.toString()
        if (before != typed) return false
        ic.beginBatchEdit()
        try {
            ic.deleteSurroundingText(typed.length, 0)
            ic.commitText(replacement, 1)
        } finally {
            ic.endBatchEdit()
        }
        wordBuffer = StringBuilder()
        refreshSuggestions()
        return true
    }

    /**
     * Basic offline autocorrection: when the word just typed is not in the
     * dictionary but is within edit distance 2 of a known word, replace it
     * before the separator is committed. Only touches the buffer we typed, so
     * user-managed text is never rewritten.
     */
    private fun maybeAutocorrect() {
        if (privateMode) return
        if (!prefs.getBoolean(SmartPrefs.KEY_AUTOCORRECT, true)) return
        val ic = currentInputConnection ?: return
        val typed = wordBuffer.toString()
        if (typed.length < 3) return
        val fix = suggestions.correct(typed) ?: return
        val before = ic.getTextBeforeCursor(typed.length, 0)?.toString()
        if (before != typed) return
        ic.beginBatchEdit()
        try {
            ic.deleteSurroundingText(typed.length, 0)
            ic.commitText(fix, 1)
        } finally {
            ic.endBatchEdit()
        }
        wordBuffer = StringBuilder(fix)
    }

    private fun onEnter() {
        if (emojiSearchActive) {
            val first = emojiResults.firstOrNull()
            if (first != null) {
                commitEmoji(first)
            } else {
                exitEmojiSearch()
            }
            return
        }
        if (!maybeExpandShortcut()) {
            maybeAutocorrect()
            commitWordToLearning()
        }
        val ic = currentInputConnection ?: return
        val editor = currentInputEditorInfo
        val action = editor?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_UNSPECIFIED
        when (action) {
            EditorInfo.IME_ACTION_GO -> ic.performEditorAction(EditorInfo.IME_ACTION_GO)
            EditorInfo.IME_ACTION_SEARCH -> ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
            EditorInfo.IME_ACTION_SEND -> ic.performEditorAction(EditorInfo.IME_ACTION_SEND)
            EditorInfo.IME_ACTION_NEXT -> ic.performEditorAction(EditorInfo.IME_ACTION_NEXT)
            EditorInfo.IME_ACTION_DONE -> ic.performEditorAction(EditorInfo.IME_ACTION_DONE)
            else -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
        }
        wordBuffer = StringBuilder()
        capitalizeNext = true
        updateViewShift()
        updateCandidates()
    }

    private fun onEmoji(text: String) {
        if (text.isNotBlank()) {
            currentInputConnection?.commitText(text, 1)
        }
        if (emojiSearchActive) {
            exitEmojiSearch()
        }
    }

    private fun toggleEmojiSearch() {
        if (emojiSearchActive) {
            exitEmojiSearch()
            return
        }
        emojiSearchActive = true
        emojiQuery = ""
        emojiResults.clear()
        keyboardView?.emojiQuery = ""
        keyboardView?.emojiResults = emptyList()
        keyboardView?.emojiSearchActive = true
        switchMode(KeyboardLayout.MODE_LETTERS)
        keyboardView?.setShowSuggestions(false)
        updateEmojiResults()
    }

    private fun appendToEmojiQuery(ch: Char) {
        if (emojiQuery.length >= 24) return
        emojiQuery += ch
        updateEmojiResults()
    }

    private fun updateEmojiResults() {
        emojiResults.clear()
        val results = EmojiData.search(emojiQuery)
        emojiResults.addAll(results)
        keyboardView?.emojiQuery = emojiQuery
        keyboardView?.emojiResults = results
    }

    private fun commitEmoji(text: String) {
        currentInputConnection?.commitText(text, 1)
        exitEmojiSearch()
    }

    private fun exitEmojiSearch() {
        emojiSearchActive = false
        emojiQuery = ""
        emojiResults.clear()
        keyboardView?.emojiSearchActive = false
        keyboardView?.emojiQuery = ""
        keyboardView?.emojiResults = emptyList()
        switchMode(KeyboardLayout.MODE_EMOJI)
    }

    private fun switchMode(newMode: Int) {
        if (newMode != KeyboardLayout.MODE_LETTERS) {
            emojiSearchActive = false
            emojiQuery = ""
            emojiResults.clear()
            keyboardView?.emojiSearchActive = false
            keyboardView?.emojiQuery = ""
            keyboardView?.emojiResults = emptyList()
        }
        currentMode = newMode
        val view = keyboardView ?: return
        view.mode = newMode
        if (newMode == KeyboardLayout.MODE_EMOJI) {
            view.setEmojiCategory(0)
        }
        if (newMode == KeyboardLayout.MODE_LETTERS || newMode == KeyboardLayout.MODE_SYMBOLS_1 || newMode == KeyboardLayout.MODE_SYMBOLS_2) {
            view.setShowSuggestions(prefs.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true) && !privateMode)
        }
        view.requestLayout()
        view.invalidate()
    }

    private fun refreshSuggestions() {
        val view = keyboardView
        if (view == null) return
        if (privateMode || !prefs.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true)) {
            view.suggestions = emptyList()
            view.typedPrefix = wordBuffer.toString()
            return
        }
        view.suggestions = suggestions.suggest(wordBuffer.toString())
        view.typedPrefix = wordBuffer.toString()
    }

    private fun updateCandidates() {
        if (!privateMode && prefs.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true)) {
            refreshSuggestions()
        }
    }

    private fun commitWordToLearning() {
        val word = wordBuffer.toString()
        if (word.isNotEmpty()) {
            suggestions.learn(word)
        }
        wordBuffer = StringBuilder()
        refreshSuggestions()
    }

    override fun onSuggestion(text: String) {
        feedback()
        hidePanels()
        if (emojiSearchActive) {
            if (text == KeyboardView.SEARCH_CLEAR) {
                emojiQuery = ""
                updateEmojiResults()
            } else {
                commitEmoji(text)
            }
            return
        }
        val ic = currentInputConnection ?: return
        val prefixLen = wordBuffer.length
        if (prefixLen > 0) {
            ic.deleteSurroundingText(prefixLen, 0)
        }
        ic.commitText(text, 1)
        wordBuffer = StringBuilder(text)
        refreshSuggestions()
    }

    private fun updateViewShift() {
        keyboardView?.let {
            it.shiftState = if (capsLock) 1 else shiftState
            it.capsLock = capsLock
            it.invalidate()
        }
    }

    override fun getCalcDisplay(): String = calculator.formatDisplay()

    private fun calcClear() {
        calculator.clear()
        keyboardView?.invalidate()
    }

    private fun calcBackspace() {
        calculator.backspace()
        keyboardView?.invalidate()
    }

    private fun calcOp(op: String) {
        calculator.append(calcMap(op))
        keyboardView?.invalidate()
    }

    private fun calcMap(op: String): String = when (op) {
        "−" -> "-"
        "×" -> "*"
        "÷" -> "/"
        else -> op
    }

    private fun calcEquals() {
        if (calculator.expression.isBlank()) return
        if (!calculator.isValid()) return
        val result = calculator.evaluate()
        currentInputConnection?.commitText(result, 1)
        calculator.clear()
        keyboardView?.invalidate()
    }

    private fun showClipboardPanel() {
        hidePanels()
        val view = keyboardView ?: return
        val panel = ClipboardPanel(this, clipboard)
        clipboardPanel = panel
        panel.show(view, onSelect = { text ->
            hidePanels()
            currentInputConnection?.commitText(text, 1)
        })
    }

    private fun showToolsPanel() {
        hidePanels()
        val view = keyboardView ?: return
        val panel = ToolsPanel(this)
        toolsPanel = panel
        panel.show(view, callbacks = object : ToolsPanel.Callbacks {
            override fun insert(text: String) {
                hidePanels()
                currentInputConnection?.commitText(text, 1)
            }

            override fun transform(mode: Int) {
                hidePanels()
                transformText(mode)
            }

            override fun copySelection() {
                val ic = currentInputConnection ?: return
                val sel = ic.getSelectedText(0)?.toString()
                if (!sel.isNullOrEmpty()) {
                    clipboard.copy(sel)
                    clipboard.push(sel)
                } else {
                    val word = lastWordBeforeCursor()
                    if (word != null) {
                        clipboard.copy(word)
                        clipboard.push(word)
                    }
                }
            }
        })
    }

    private fun lastWordBeforeCursor(): String? {
        val ic = currentInputConnection ?: return null
        val before = ic.getTextBeforeCursor(200, 0)?.toString() ?: return null
        val words = before.split(Regex("[\\s\\p{Punct}]+"))
        return words.lastOrNull { it.isNotEmpty() }
    }

    private fun transformText(mode: Int) {
        val ic = currentInputConnection ?: return
        val sel = ic.getSelectedText(0)?.toString()
        val text: String
        val deleteLength: Int
        if (!sel.isNullOrEmpty()) {
            text = sel
            deleteLength = sel.length
        } else {
            text = lastWordBeforeCursor() ?: return
            deleteLength = text.length
        }
        val transformed = when (mode) {
            0 -> text.uppercase()
            1 -> text.lowercase()
            else -> text.split(" ").joinToString(" ") { w ->
                w.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
        ic.deleteSurroundingText(deleteLength, 0)
        ic.commitText(transformed, 1)
    }

    private fun openSettings() {
        hidePanels()
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun hidePanels() {
        clipboardPanel?.dismiss()
        clipboardPanel = null
        toolsPanel?.dismiss()
        toolsPanel = null
    }

    override fun onComputeInsets(outInsets: InputMethodService.Insets) {
        super.onComputeInsets(outInsets)
        outInsets.visibleTopInsets = 0
        outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_VISIBLE
    }

    override fun onWindowShown() {
        super.onWindowShown()
        keyboardView?.let {
            it.typedPrefix = ""
            it.suggestions = emptyList()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val themeName = prefs.getString(SmartPrefs.KEY_THEME) ?: ThemeConfig.KEY_LIGHT
        if (themeName.equals(ThemeConfig.KEY_SYSTEM, true)) {
            keyboardView?.theme = resolveTheme()
            keyboardView?.invalidate()
        }
    }

    override fun onDestroy() {
        sounds.release()
        super.onDestroy()
    }

    override fun onCurrentInputMethodSubtypeChanged(subtype: InputMethodSubtype) {
        keyboardView?.invalidate()
    }
}