package com.smartkey.keyboard

enum class KeyKind { CHAR, SHIFT, BACKSPACE, SPACE, ENTER, MODE_LETTERS, MODE_SYMBOLS_1, MODE_SYMBOLS_2, MODE_EMOJI, MODE_CALC, EMOJI, SETTINGS, CLIPBOARD, TOOLS, HIDE, EMOJI_PREV, EMOJI_NEXT, EMOJI_CATEGORY, CALC_AC, CALC_BSP, CALC_EQUALS, CALC_OP, CURSOR_LEFT, CURSOR_RIGHT, CURSOR_HOME, CURSOR_END, COPY, CUT, PASTE, SELECT_ALL, UNDO, REDO, NEWLINE, CALC_HISTORY }

data class KeySpec(
    val kind: KeyKind,
    val text: String = "",
    val weight: Float = 1f
)

object KeyboardLayout {
    const val MODE_LETTERS = 0
    const val MODE_SYMBOLS_1 = 1
    const val MODE_SYMBOLS_2 = 2
    const val MODE_EMOJI = 3
    const val MODE_CALC = 4
    const val MODE_COUNT = 5

    private fun c(ch: Char) = KeySpec(KeyKind.CHAR, text = ch.toString())
    private fun cs(s: String) = KeySpec(KeyKind.CHAR, text = s)
    private fun k(kind: KeyKind, text: String = "", weight: Float = 1f) = KeySpec(kind, text, weight)

    val EDIT_ROW: List<KeySpec> = listOf(
        k(KeyKind.UNDO, "↶", weight = 1f),
        k(KeyKind.CUT, "✂", weight = 1f),
        k(KeyKind.COPY, "⧉", weight = 1f),
        k(KeyKind.PASTE, "📋", weight = 1f),
        k(KeyKind.CURSOR_LEFT, "◀", weight = 1f),
        k(KeyKind.CURSOR_RIGHT, "▶", weight = 1f),
        k(KeyKind.SELECT_ALL, "⌑", weight = 1f),
        k(KeyKind.REDO, "↷", weight = 1f)
    )

    val LETTERS: List<List<KeySpec>> = listOf(
        listOf(c('Q'), c('W'), c('E'), c('R'), c('T'), c('Y'), c('U'), c('I'), c('O'), c('P')),
        listOf(c('A'), c('S'), c('D'), c('F'), c('G'), c('H'), c('J'), c('K'), c('L')),
        listOf(k(KeyKind.SHIFT, weight = 1.5f), c('Z'), c('X'), c('C'), c('V'), c('B'), c('N'), c('M'), k(KeyKind.BACKSPACE, weight = 1.5f)),
        listOf(k(KeyKind.MODE_SYMBOLS_1, weight = 1.4f), cs(","), k(KeyKind.SPACE, weight = 5.4f), cs("."), k(KeyKind.ENTER, weight = 1.4f)),
        listOf(k(KeyKind.MODE_EMOJI, weight = 1.2f), k(KeyKind.MODE_CALC, weight = 1.2f), k(KeyKind.CLIPBOARD, weight = 1.2f), k(KeyKind.TOOLS, weight = 1.2f), k(KeyKind.SETTINGS, weight = 1.2f), k(KeyKind.HIDE, weight = 1.2f))
    )

    val SYMBOLS_1: List<List<KeySpec>> = listOf(
        listOf(c('1'), c('2'), c('3'), c('4'), c('5'), c('6'), c('7'), c('8'), c('9'), c('0')),
        listOf(c('@'), c('#'), c('$'), c('%'), c('&'), c('-'), c('+'), c('('), c(')')),
        listOf(k(KeyKind.MODE_SYMBOLS_2, weight = 1.5f), c('*'), c('"'), c('\''), c(':'), c(';'), c('!'), c('?'), k(KeyKind.BACKSPACE, weight = 1.5f)),
        listOf(k(KeyKind.MODE_LETTERS, weight = 1.4f), cs(","), k(KeyKind.SPACE, weight = 5.4f), cs("."), k(KeyKind.ENTER, weight = 1.4f)),
        listOf(k(KeyKind.MODE_EMOJI, weight = 1.2f), k(KeyKind.MODE_CALC, weight = 1.2f), k(KeyKind.CLIPBOARD, weight = 1.2f), k(KeyKind.TOOLS, weight = 1.2f), k(KeyKind.SETTINGS, weight = 1.2f), k(KeyKind.HIDE, weight = 1.2f))
    )

    val SYMBOLS_2: List<List<KeySpec>> = listOf(
        listOf(c('~'), c('`'), c('|'), c('•'), c('√'), c('π'), c('÷'), c('×'), c('¶'), c('∆')),
        listOf(c('£'), c('¢'), c('€'), c('¥'), c('^'), c('°'), c('='), c('{'), c('}')),
        listOf(k(KeyKind.MODE_SYMBOLS_1, weight = 1.5f), c('_'), c('\\'), c('<'), c('>'), c('/'), c('['), c(']'), k(KeyKind.BACKSPACE, weight = 1.5f)),
        listOf(k(KeyKind.MODE_LETTERS, weight = 1.4f), cs(","), k(KeyKind.SPACE, weight = 5.4f), cs("."), k(KeyKind.ENTER, weight = 1.4f)),
        listOf(k(KeyKind.MODE_EMOJI, weight = 1.2f), k(KeyKind.MODE_CALC, weight = 1.2f), k(KeyKind.CLIPBOARD, weight = 1.2f), k(KeyKind.TOOLS, weight = 1.2f), k(KeyKind.SETTINGS, weight = 1.2f), k(KeyKind.HIDE, weight = 1.2f))
    )

    val CALC: List<List<KeySpec>> = listOf(
        listOf(k(KeyKind.CALC_AC, text = "AC"), k(KeyKind.CALC_OP, text = "("), k(KeyKind.CALC_OP, text = ")"), k(KeyKind.CALC_BSP, text = "⌫")),
        listOf(c('7'), c('8'), c('9'), k(KeyKind.CALC_OP, text = "÷")),
        listOf(c('4'), c('5'), c('6'), k(KeyKind.CALC_OP, text = "×")),
        listOf(c('1'), c('2'), c('3'), k(KeyKind.CALC_OP, text = "−")),
        listOf(k(KeyKind.CALC_OP, text = "±"), c('0'), c('.'), k(KeyKind.CALC_OP, text = "+")),
        listOf(k(KeyKind.CALC_OP, text = "√"), k(KeyKind.CALC_OP, text = "%"), k(KeyKind.CALC_OP, text = "^"), k(KeyKind.CALC_HISTORY, text = "🕘")),
        listOf(k(KeyKind.HIDE, text = "H"), k(KeyKind.MODE_LETTERS, text = "abc"), k(KeyKind.CALC_EQUALS, text = "="))
    )

    val NUMBER_ROW: List<KeySpec> = listOf(
        c('1'), c('2'), c('3'), c('4'), c('5'), c('6'), c('7'), c('8'), c('9'), c('0')
    )

    fun rowsFor(mode: Int, numberRow: Boolean = false, editingRow: Boolean = true): List<List<KeySpec>> {
        if (mode == MODE_CALC) return CALC
        val base: List<List<KeySpec>> = when (mode) {
            MODE_SYMBOLS_1 -> SYMBOLS_1
            MODE_SYMBOLS_2 -> SYMBOLS_2
            else -> LETTERS
        }
        val rows = ArrayList<List<KeySpec>>()
        if (mode == MODE_LETTERS && numberRow) rows.add(NUMBER_ROW)
        rows.addAll(base)
        if (editingRow && (mode == MODE_LETTERS || mode == MODE_SYMBOLS_1 || mode == MODE_SYMBOLS_2)) {
            rows.add(EDIT_ROW)
        }
        return rows
    }
}

object EmojiData {
    data class Category(val name: String, val emoji: List<String>)

    private const val PER_PAGE = 40

    private val SMILEYS = Category(
        "Smileys", listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
            "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸",
            "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "😣",
            "😖", "😫", "🥺", "😢", "😭", "😤", "😠", "😡", "🤯", "😳",
            "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔", "🫡",
            "🤭", "🤫", "🤥", "😶", "😐", "😑", "😬", "🙄", "😯", "😴",
            "🤤", "😪", "😵", "🤐", "🥴", "🤢", "🤮", "🤧", "😷", "🤒"
        )
    )

    private val ANIMALS = Category(
        "Animals", listOf(
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨",
            "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒",
            "🐔", "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗",
            "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞", "🐢", "🐍",
            "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳",
            "🐋", "🦈", "🐊", "🦭", "🐅", "🦓", "🦍", "🐘", "🦏", "🐪",
            "🐫", "🦒", "🦘", "🦥", "🦦", "🦨", "🦩", "🦜", "🐿️", "🦔"
        )
    )

    private val FOOD = Category(
        "Food", listOf(
            "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
            "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑",
            "🥦", "🥬", "🥒", "🌶️", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠",
            "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞",
            "🧇", "🥓", "🥩", "🍗", "🍖", "🌭", "🍔", "🍟", "🍕", "🥪",
            "🥙", "🧆", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍛",
            "🍙", "🍚", "🍢", "🍣", "🍤", "🍥", "🥠", "🍦", "🍧", "🍨",
            "🍩", "🍪", "🎂", "🍰", "🧁", "🥧", "🍫", "🍬", "🍭", "🍮"
        )
    )

    private val ACTIVITY = Category(
        "Activity", listOf(
            "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
            "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🥅", "⛳", "🏹", "🎣",
            "🥊", "🥋", "🎽", "🛹", "🛼", "🏄", "🏊", "🤽", "🚣", "🧗",
            "🚴", "🏇", "🏂", "🎿", "⛷️", "🧘", "🤸", "🧎", "🏋️", "🤼",
            "🎮", "🕹️", "🎲", "♟️", "🎯", "🎳", "🎪", "🎤", "🎧", "🎼",
            "🎹", "🥁", "🎷", "🎺", "🎸", "🎻", "🪕", "🎬", "🎨", "🎭",
            "🎩", "🎪", "🏆", "🥇", "🥈", "🥉", "🎖️", "🏅", "🎗️", "🎫"
        )
    )

    private val TRAVEL = Category(
        "Travel", listOf(
            "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐",
            "🛻", "🚚", "🚛", "🚜", "🛵", "🏍️", "🛺", "🚲", "🛴", "🚁",
            "🚀", "🛸", "✈️", "🛫", "🛬", "🪂", "💺", "🚂", "🚆", "🚇",
            "🚈", "🚄", "🚅", "🚝", "🚃", "🚋", "🚊", "🚉", "🗺️", "🗿",
            "🏠", "🏡", "🏢", "🏣", "🏤", "🏥", "🏦", "🏨", "🏩", "🏪",
            "🏫", "🏬", "🏭", "🏯", "🏰", "💒", "🗼", "🏛️", "⛪", "🕌",
            "🛕", "🕍", "⛩️", "🏘️", "🌋", "🗻", "🏔️", "⛰️", "🏕️", "🏖️"
        )
    )

    private val OBJECTS = Category(
        "Objects", listOf(
            "⌚", "📱", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🖲️", "💾", "💿",
            "📀", "📼", "📷", "📸", "📹", "🎥", "📽️", "🎞️", "📞", "☎️",
            "📟", "📠", "📺", "📻", "🎙️", "🎚️", "🎛️", "🧭", "⏱️", "⏲️",
            "⏰", "🕰️", "⌛", "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯️",
            "🪔", "🧯", "🗑️", "🛢️", "💳", "💰", "💵", "💴", "💶", "💷",
            "🪙", "💎", "⚖️", "🪜", "🧰", "🪛", "🔧", "🔨", "⚒️", "🛠️",
            "⛏️", "🪚", "🔩", "📎", "🖇️", "📌", "📍", "📏", "🔒", "🔓"
        )
    )

    private val SYMBOL_EMOJI = Category(
        "Symbols", listOf(
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
            "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐",
            "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐",
            "♑", "♒", "♓", "🆔", "⚛️", "🉑", "☢️", "☣️", "📴", "📳",
            "🈶", "🈚", "🈸", "🈺", "🈷️", "✴️", "🆚", "💮", "🉐", "㊙️",
            "㊗️", "🧿", "🈴", "🈵", "🔞", "✳️", "❇️", "✴️", "💲", "⚜️"
        )
    )

    private val CATEGORIES = listOf(SMILEYS, ANIMALS, FOOD, ACTIVITY, TRAVEL, OBJECTS, SYMBOL_EMOJI)

    private val FLAT: MutableList<String> = mutableListOf()
    private val CATEGORY_START = HashMap<String, Int>()

    init {
        for (cat in CATEGORIES) {
            CATEGORY_START[cat.name] = FLAT.size
            FLAT.addAll(cat.emoji)
        }
    }

    val all: List<String> get() = FLAT

    fun flatIndex(categoryIndex: Int, pageOffset: Int): Int {
        val cat = CATEGORIES[categoryIndex]
        val start = CATEGORY_START[cat.name] ?: 0
        val end = start + cat.emoji.size
        val count = cat.emoji.size
        val pages = (count + PER_PAGE - 1) / PER_PAGE
        val bounded = ((pageOffset % pages) + pages) % pages
        val from = start + bounded * PER_PAGE
        val to = minOf(end, from + PER_PAGE)
        if (from >= to) return 0
        return from
    }

    fun pageEmoji(categoryIndex: Int, pageOffset: Int): List<String> {
        val cat = CATEGORIES[categoryIndex]
        val start = CATEGORY_START[cat.name] ?: 0
        val end = start + cat.emoji.size
        val count = cat.emoji.size
        val pages = (count + PER_PAGE - 1) / PER_PAGE
        val bounded = ((pageOffset % pages) + pages) % pages
        val from = start + bounded * PER_PAGE
        val to = minOf(end, from + PER_PAGE)
        return cat.emoji.subList(from - start, to - start)
    }

    const val CATEGORY_COUNT = 7
    const val PAGE_SIZE = PER_PAGE

    fun categoryIcon(index: Int): String = CATEGORIES[index].emoji[0]

    fun categoryIndexOf(icon: String): Int {
        for (i in CATEGORIES.indices) {
            if (CATEGORIES[i].emoji.firstOrNull() == icon) return i
        }
        return 0
    }
}