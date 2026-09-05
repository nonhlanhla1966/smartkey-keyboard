package com.smartkey.keyboard

enum class KeyKind { CHAR, SHIFT, BACKSPACE, SPACE, ENTER, MODE_LETTERS, MODE_SYMBOLS_1, MODE_SYMBOLS_2, MODE_EMOJI, MODE_CALC, EMOJI, SETTINGS, CLIPBOARD, TOOLS, HIDE, EMOJI_PREV, EMOJI_NEXT, EMOJI_CATEGORY, EMOJI_SEARCH, EMOJI_CLEAR, CALC_AC, CALC_BSP, CALC_EQUALS, CALC_OP, CURSOR_LEFT, CURSOR_RIGHT, CURSOR_HOME, CURSOR_END, COPY, CUT, PASTE, SELECT_ALL, UNDO, REDO, NEWLINE, CALC_HISTORY }

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

    private val KW_SMILEYS: Map<String, List<String>> = mapOf(
        "😀" to listOf("grin", "smile"), "😃" to listOf("smile", "happy"), "😄" to listOf("smile", "happy"),
        "😁" to listOf("grin", "teeth"), "😆" to listOf("laugh", "happy"), "😅" to listOf("sweat", "smile"),
        "😂" to listOf("laugh", "cry", "joy"), "🤣" to listOf("laugh", "rolling"), "😊" to listOf("smile", "blush"),
        "😇" to listOf("angel", "halo", "innocent"),
        "🙂" to listOf("smile"), "🙃" to listOf("upside", "upside down"), "😉" to listOf("wink"),
        "😌" to listOf("relaxed", "relieved"), "😍" to listOf("love", "heart", "eyes"), "🥰" to listOf("love", "smile", "heart"),
        "😘" to listOf("kiss", "blow"), "😗" to listOf("kiss"), "😙" to listOf("kiss", "whistle"), "😚" to listOf("kiss"),
        "😋" to listOf("yum", "tasty"), "😛" to listOf("tongue"), "😝" to listOf("tongue", "squint"),
        "😜" to listOf("tongue", "wink"), "🤪" to listOf("crazy", "zany"), "🤨" to listOf("eyebrow", "skeptic"),
        "🧐" to listOf("nerd", "glasses"), "🤓" to listOf("nerd", "glasses", "geek"), "😎" to listOf("cool", "sunglasses"),
        "🥸" to listOf("disguise", "glasses", "nose"),
        "🤩" to listOf("star", "amazed", "wow"), "🥳" to listOf("party", "celebrate"), "😏" to listOf("smirk"),
        "😒" to listOf("unamused"), "😞" to listOf("disappointed", "sad"), "😔" to listOf("sad", "pensive"),
        "😟" to listOf("worry", "sad"), "😕" to listOf("confused"), "🙁" to listOf("frown", "sad"), "😣" to listOf("struggle", "pain"),
        "😖" to listOf("anguish", "pain"), "😫" to listOf("tired", "exhausted"), "🥺" to listOf("pleading", "cute", "puppy"),
        "😢" to listOf("cry", "tear", "sad"), "😭" to listOf("cry", "sob", "tears"), "😤" to listOf("frustrated", "steam"),
        "😠" to listOf("angry"), "😡" to listOf("anger", "rage", "red"), "🤯" to listOf("mind", "blown", "explosion"),
        "😳" to listOf("flushed", "embarrassed"),
        "🥵" to listOf("hot", "sweating", "heat"), "🥶" to listOf("cold", "freezing", "ice"), "😱" to listOf("scream", "fear", "shock"),
        "😨" to listOf("fear", "scared"), "😰" to listOf("anxious", "sweat", "worried"), "😥" to listOf("relieved", "sad"),
        "😓" to listOf("sweat", "exhausted"), "🤗" to listOf("hug", "hands"), "🤔" to listOf("thinking", "hmm"),
        "🫡" to listOf("salute", "military"),
        "🤭" to listOf("hush", "covering", "giggle"), "🤫" to listOf("shh", "quiet", "silence"), "🤥" to listOf("liar", "nose", "pinocchio"),
        "😶" to listOf("silent", "mouth"), "😐" to listOf("neutral", "meh"), "😑" to listOf("expressionless", "blank"),
        "😬" to listOf("grimace", "awkward", "teeth"), "🙄" to listOf("eye", "roll", "annoyed"), "😯" to listOf("surprised", "hushed"),
        "😴" to listOf("sleeping", "sleep"),
        "🤤" to listOf("drool"), "😪" to listOf("sleepy"), "😵" to listOf("dizzy", "confusion", "x eyes"),
        "🤐" to listOf("zipper", "silence", "secret"), "🥴" to listOf("woozy", "drunk"), "🤢" to listOf("nauseated", "sick"),
        "🤮" to listOf("vomit", "barf", "sick"), "🤧" to listOf("sneeze", "sick"), "😷" to listOf("mask", "sick"),
        "🤒" to listOf("fever", "sick", "thermometer")
    )

    private val KW_ANIMALS: Map<String, List<String>> = mapOf(
        "🐶" to listOf("dog", "puppy"), "🐱" to listOf("cat", "kitten"), "🐭" to listOf("mouse"), "🐹" to listOf("hamster"),
        "🐰" to listOf("rabbit", "bunny"), "🦊" to listOf("fox"), "🐻" to listOf("bear"), "🐼" to listOf("panda"),
        "🐻‍❄️" to listOf("polar", "bear", "white"), "🐨" to listOf("koala"),
        "🐯" to listOf("tiger"), "🦁" to listOf("lion"), "🐮" to listOf("cow"), "🐷" to listOf("pig"), "🐸" to listOf("frog"),
        "🐵" to listOf("monkey"), "🙈" to listOf("see no evil", "monkey"), "🙉" to listOf("hear no evil", "monkey"),
        "🙊" to listOf("speak no evil", "monkey"), "🐒" to listOf("monkey"),
        "🐔" to listOf("chicken"), "🐧" to listOf("penguin"), "🐦" to listOf("bird"), "🐤" to listOf("chick", "baby bird"),
        "🦆" to listOf("duck"), "🦅" to listOf("eagle"), "🦉" to listOf("owl"), "🦇" to listOf("bat"), "🐺" to listOf("wolf"),
        "🐗" to listOf("boar", "hog"),
        "🐴" to listOf("horse"), "🦄" to listOf("unicorn"), "🐝" to listOf("bee", "honey"), "🪱" to listOf("worm"),
        "🐛" to listOf("bug", "caterpillar"), "🦋" to listOf("butterfly"), "🐌" to listOf("snail"), "🐞" to listOf("ladybug", "beetle"),
        "🐢" to listOf("turtle"), "🐍" to listOf("snake"),
        "🐙" to listOf("octopus"), "🦑" to listOf("squid", "calamari"), "🦐" to listOf("shrimp", "prawn"), "🦞" to listOf("lobster"),
        "🦀" to listOf("crab"), "🐡" to listOf("blowfish", "puffer", "fish"), "🐠" to listOf("tropical", "fish"), "🐟" to listOf("fish"),
        "🐬" to listOf("dolphin"), "🐳" to listOf("whale", "spouting"),
        "🐋" to listOf("whale"), "🦈" to listOf("shark"), "🐊" to listOf("crocodile", "alligator"), "🦭" to listOf("seal"),
        "🐅" to listOf("tiger"), "🦓" to listOf("zebra"), "🦍" to listOf("gorilla"), "🐘" to listOf("elephant"),
        "🦏" to listOf("rhino", "rhinoceros"), "🐪" to listOf("camel"),
        "🐫" to listOf("camel", "two humps"), "🦒" to listOf("giraffe"), "🦘" to listOf("kangaroo"), "🦥" to listOf("sloth"),
        "🦦" to listOf("otter"), "🦨" to listOf("skunk"), "🦩" to listOf("flamingo"), "🦜" to listOf("parrot"),
        "🐿️" to listOf("squirrel"), "🦔" to listOf("hedgehog")
    )

    private val KW_FOOD: Map<String, List<String>> = mapOf(
        "🍏" to listOf("apple", "green"), "🍎" to listOf("apple", "red"), "🍐" to listOf("pear"), "🍊" to listOf("orange", "tangerine"),
        "🍋" to listOf("lemon"), "🍌" to listOf("banana"), "🍉" to listOf("watermelon"), "🍇" to listOf("grapes"),
        "🍓" to listOf("strawberry"), "🫐" to listOf("blueberry"),
        "🍈" to listOf("melon"), "🍒" to listOf("cherries"), "🍑" to listOf("peach"), "🥭" to listOf("mango"),
        "🍍" to listOf("pineapple"), "🥥" to listOf("coconut"), "🥝" to listOf("kiwi"), "🍅" to listOf("tomato"),
        "🍆" to listOf("eggplant"), "🥑" to listOf("avocado"),
        "🥦" to listOf("broccoli"), "🥬" to listOf("lettuce", "green", "leafy"), "🥒" to listOf("cucumber"), "🌶️" to listOf("pepper", "chili", "hot"),
        "🌽" to listOf("corn"), "🥕" to listOf("carrot"), "🧄" to listOf("garlic"), "🧅" to listOf("onion"),
        "🥔" to listOf("potato"), "🍠" to listOf("sweet potato", "yam"),
        "🥐" to listOf("croissant"), "🥯" to listOf("bagel"), "🍞" to listOf("bread"), "🥖" to listOf("baguette"),
        "🥨" to listOf("pretzel"), "🧀" to listOf("cheese"), "🥚" to listOf("egg"), "🍳" to listOf("cooking", "fried egg", "frying pan"),
        "🧈" to listOf("butter"), "🥞" to listOf("pancakes"),
        "🧇" to listOf("waffle"), "🥓" to listOf("bacon"), "🥩" to listOf("meat", "steak"), "🍗" to listOf("chicken", "drumstick"),
        "🍖" to listOf("meat", "bone"), "🌭" to listOf("hotdog"), "🍔" to listOf("burger", "hamburger"), "🍟" to listOf("fries", "french fries"),
        "🍕" to listOf("pizza"), "🥪" to listOf("sandwich"),
        "🥙" to listOf("flatbread", "stuffed", "wrap"), "🧆" to listOf("falafel"), "🌮" to listOf("taco"), "🌯" to listOf("burrito"),
        "🥗" to listOf("salad"), "🥘" to listOf("stew", "pot", "paella"), "🍝" to listOf("pasta", "spaghetti"), "🍜" to listOf("noodles", "ramen"),
        "🍲" to listOf("soup", "pot"), "🍛" to listOf("curry", "rice"),
        "🍙" to listOf("rice ball"), "🍚" to listOf("rice", "cooked"), "🍢" to listOf("skewer", "oden"), "🍣" to listOf("sushi"),
        "🍤" to listOf("shrimp", "fried"), "🍥" to listOf("fish cake"), "🥠" to listOf("fortune cookie"), "🍦" to listOf("ice cream", "soft serve"),
        "🍧" to listOf("shaved ice", "sorbet"), "🍨" to listOf("ice cream", "sundae"),
        "🍩" to listOf("donut", "doughnut"), "🍪" to listOf("cookie"), "🎂" to listOf("birthday", "cake"), "🍰" to listOf("cake", "shortcake"),
        "🧁" to listOf("cupcake"), "🥧" to listOf("pie"), "🍫" to listOf("chocolate", "bar"), "🍬" to listOf("candy"),
        "🍭" to listOf("lollipop"), "🍮" to listOf("custard", "pudding")
    )

    private val KW_ACTIVITY: Map<String, List<String>> = mapOf(
        "⚽" to listOf("soccer", "football"), "🏀" to listOf("basketball"), "🏈" to listOf("football", "american"), "⚾" to listOf("baseball"),
        "🥎" to listOf("softball"), "🎾" to listOf("tennis"), "🏐" to listOf("volleyball"), "🏉" to listOf("rugby"),
        "🥏" to listOf("frisbee"), "🎱" to listOf("billiards", "eight ball", "pool"),
        "🏓" to listOf("table tennis", "ping pong"), "🏸" to listOf("badminton"), "🏒" to listOf("hockey", "ice hockey"), "🏑" to listOf("field hockey"),
        "🥍" to listOf("lacrosse"), "🏏" to listOf("cricket"), "🥅" to listOf("goal", "net"), "⛳" to listOf("golf"),
        "🏹" to listOf("archery", "bow"), "🎣" to listOf("fishing"),
        "🥊" to listOf("boxing", "glove"), "🥋" to listOf("martial arts", "karate"), "🎽" to listOf("running", "sash", "shirt"), "🛹" to listOf("skateboard"),
        "🛼" to listOf("roller skate", "skating"), "🏄" to listOf("surfing"), "🏊" to listOf("swimming", "pool"), "🤽" to listOf("water polo"),
        "🚣" to listOf("rowing", "boat"), "🧗" to listOf("climbing"),
        "🚴" to listOf("cycling", "bicycle"), "🏇" to listOf("horse racing"), "🏂" to listOf("snowboard"), "🎿" to listOf("skiing", "skis"),
        "⛷️" to listOf("skier", "skiing"), "🧘" to listOf("meditation", "yoga", "pose"), "🤸" to listOf("gymnastics", "cartwheel"), "🧎" to listOf("kneeling"),
        "🏋️" to listOf("weight lifting", "gym"), "🤼" to listOf("wrestling"),
        "🎮" to listOf("video game", "controller"), "🕹️" to listOf("joystick", "game"), "🎲" to listOf("dice"), "♟️" to listOf("chess"),
        "🎯" to listOf("darts", "target"), "🎳" to listOf("bowling"), "🎪" to listOf("circus", "tent"), "🎤" to listOf("microphone", "karaoke"),
        "🎧" to listOf("headphones", "earphones"), "🎼" to listOf("sheet music", "score"),
        "🎹" to listOf("piano", "keyboard"), "🥁" to listOf("drum"), "🎷" to listOf("saxophone"), "🎺" to listOf("trumpet"),
        "🎸" to listOf("guitar"), "🎻" to listOf("violin"), "🪕" to listOf("banjo"), "🎬" to listOf("clapper", "movie", "action"),
        "🎨" to listOf("art", "palette", "paint"), "🎭" to listOf("theater", "masks", "drama"),
        "🎩" to listOf("top hat", "magic"), "🎪" to listOf("circus", "tent"), "🏆" to listOf("trophy", "champion"), "🥇" to listOf("gold", "medal", "first"),
        "🥈" to listOf("silver", "medal", "second"), "🥉" to listOf("bronze", "medal", "third"), "🎖️" to listOf("medal", "award", "military"), "🏅" to listOf("medal", "award", "sports"),
        "🎗️" to listOf("ribbon", "awareness"), "🎫" to listOf("ticket")
    )

    private val KW_TRAVEL: Map<String, List<String>> = mapOf(
        "🚗" to listOf("car", "red car"), "🚕" to listOf("taxi", "cab"), "🚙" to listOf("suv", "car"), "🚌" to listOf("bus"),
        "🚎" to listOf("trolleybus", "bus"), "🏎️" to listOf("race car", "sports car"), "🚓" to listOf("police car"), "🚑" to listOf("ambulance"),
        "🚒" to listOf("fire engine", "fire truck"), "🚐" to listOf("van"),
        "🛻" to listOf("pickup truck"), "🚚" to listOf("truck", "delivery"), "🚛" to listOf("lorry", "truck"), "🚜" to listOf("tractor"),
        "🛵" to listOf("scooter", "motor scooter"), "🏍️" to listOf("motorcycle"), "🛺" to listOf("rickshaw", "tuk tuk"), "🚲" to listOf("bicycle", "bike"),
        "🛴" to listOf("scooter", "kick"), "🚁" to listOf("helicopter"),
        "🚀" to listOf("rocket"), "🛸" to listOf("ufo", "saucer"), "✈️" to listOf("airplane", "plane", "flight"), "🛫" to listOf("airplane", "departure", "takeoff"),
        "🛬" to listOf("airplane", "arrival", "landing"), "🪂" to listOf("parachute"), "💺" to listOf("seat"), "🚂" to listOf("locomotive", "train"),
        "🚆" to listOf("train"), "🚇" to listOf("metro", "subway"),
        "🚈" to listOf("light rail", "train"), "🚄" to listOf("bullet train", "high speed"), "🚅" to listOf("bullet train", "shinkansen"), "🚝" to listOf("monorail"),
        "🚃" to listOf("railway car", "train"), "🚋" to listOf("tram", "train"), "🚊" to listOf("tram"), "🚉" to listOf("station"),
        "🗺️" to listOf("map", "world map"), "🗿" to listOf("moai", "statue"),
        "🏠" to listOf("house", "home"), "🏡" to listOf("house", "garden"), "🏢" to listOf("office", "building"), "🏣" to listOf("post office", "japan"),
        "🏤" to listOf("post office", "europe"), "🏥" to listOf("hospital"), "🏦" to listOf("bank"), "🏨" to listOf("hotel"),
        "🏩" to listOf("love hotel"), "🏪" to listOf("convenience store"),
        "🏫" to listOf("school"), "🏬" to listOf("department store"), "🏭" to listOf("factory"), "🏯" to listOf("castle", "japanese"),
        "🏰" to listOf("castle"), "💒" to listOf("wedding", "chapel"), "🗼" to listOf("tower", "tokyo"), "🏛️" to listOf("building", "classical"),
        "⛪" to listOf("church"), "🕌" to listOf("mosque"),
        "🛕" to listOf("temple", "hindu"), "🕍" to listOf("synagogue"), "⛩️" to listOf("shrine", "torii"), "🏘️" to listOf("houses"),
        "🌋" to listOf("volcano"), "🗻" to listOf("mountain", "fuji"), "🏔️" to listOf("mountain", "snow"), "⛰️" to listOf("mountain"),
        "🏕️" to listOf("camping", "tent"), "🏖️" to listOf("beach", "umbrella")
    )

    private val KW_OBJECTS: Map<String, List<String>> = mapOf(
        "⌚" to listOf("watch"), "📱" to listOf("phone", "smartphone", "iphone"), "💻" to listOf("laptop", "computer"), "⌨️" to listOf("keyboard"),
        "🖥️" to listOf("desktop", "computer", "monitor"), "🖨️" to listOf("printer"), "🖱️" to listOf("mouse"), "🖲️" to listOf("trackball"),
        "💾" to listOf("floppy disk"), "💿" to listOf("cd", "optical disk"),
        "📀" to listOf("dvd"), "📼" to listOf("cassette", "videotape"), "📷" to listOf("camera"), "📸" to listOf("camera", "flash"),
        "📹" to listOf("video camera", "camcorder"), "🎥" to listOf("movie camera", "film"), "📽️" to listOf("projector", "film"), "🎞️" to listOf("film frames"),
        "📞" to listOf("telephone", "phone"), "☎️" to listOf("telephone", "phone"),
        "📟" to listOf("pager"), "📠" to listOf("fax"), "📺" to listOf("tv", "television"), "📻" to listOf("radio"),
        "🎙️" to listOf("microphone", "studio"), "🎚️" to listOf("slider", "level"), "🎛️" to listOf("controls", "knobs"), "🧭" to listOf("compass"),
        "⏱️" to listOf("stopwatch"), "⏲️" to listOf("timer"),
        "⏰" to listOf("alarm clock"), "🕰️" to listOf("clock", "mantelpiece"), "⌛" to listOf("hourglass"), "⏳" to listOf("hourglass", "sand"),
        "📡" to listOf("satellite", "antenna"), "🔋" to listOf("battery"), "🔌" to listOf("plug", "power"), "💡" to listOf("light bulb", "idea"),
        "🔦" to listOf("flashlight", "torch"), "🕯️" to listOf("candle"),
        "🪔" to listOf("diya", "lamp"), "🧯" to listOf("fire extinguisher"), "🗑️" to listOf("trash", "wastebasket", "bin"), "🛢️" to listOf("oil drum", "barrel"),
        "💳" to listOf("credit card", "card"), "💰" to listOf("money bag"), "💵" to listOf("dollar"), "💴" to listOf("yen"),
        "💶" to listOf("euro"), "💷" to listOf("pound"),
        "🪙" to listOf("coin"), "💎" to listOf("diamond", "gem"), "⚖️" to listOf("scale", "justice"), "🪜" to listOf("ladder"),
        "🧰" to listOf("toolbox", "tools"), "🪛" to listOf("screwdriver"), "🔧" to listOf("wrench"), "🔨" to listOf("hammer"),
        "⚒️" to listOf("hammer", "pick"), "🛠️" to listOf("hammer", "wrench"),
        "⛏️" to listOf("pick", "mining"), "🪚" to listOf("saw"), "🔩" to listOf("nut and bolt", "screw"), "📎" to listOf("paperclip"),
        "🖇️" to listOf("paperclips", "linked"), "📌" to listOf("pushpin", "pin"), "📍" to listOf("map pin", "round pushpin"), "📏" to listOf("ruler"),
        "🔒" to listOf("lock", "locked"), "🔓" to listOf("unlocked", "open lock")
    )

    private val KW_SYMBOLS: Map<String, List<String>> = mapOf(
        "❤️" to listOf("heart", "love", "red heart"), "🧡" to listOf("orange heart"), "💛" to listOf("yellow heart"), "💚" to listOf("green heart"),
        "💙" to listOf("blue heart"), "💜" to listOf("purple heart"), "🖤" to listOf("black heart"), "🤍" to listOf("white heart"),
        "🤎" to listOf("brown heart"), "💔" to listOf("broken heart", "heartbreak"),
        "❣️" to listOf("heart", "exclamation"), "💕" to listOf("two hearts"), "💞" to listOf("hearts", "revolving"), "💓" to listOf("beating heart", "heartbeat"),
        "💗" to listOf("growing heart"), "💖" to listOf("sparkling heart"), "💘" to listOf("heart arrow", "cupid"), "💝" to listOf("heart ribbon", "gift"),
        "💟" to listOf("heart", "decoration"), "☮️" to listOf("peace", "peace sign"),
        "✝️" to listOf("cross", "christian", "church"), "☪️" to listOf("star and crescent", "islam"), "🕉️" to listOf("om", "hindu"), "☸️" to listOf("wheel", "dharma"),
        "✡️" to listOf("star of david", "jewish"), "🔯" to listOf("six pointed star"), "🕎" to listOf("menorah"), "☯️" to listOf("yin yang"),
        "☦️" to listOf("orthodox cross"), "🛐" to listOf("worship", "prayer"),
        "⛎" to listOf("ophiuchus", "zodiac"), "♈" to listOf("aries", "zodiac"), "♉" to listOf("taurus", "zodiac"), "♊" to listOf("gemini", "zodiac"),
        "♋" to listOf("cancer", "zodiac"), "♌" to listOf("leo", "zodiac"), "♍" to listOf("virgo", "zodiac"), "♎" to listOf("libra", "zodiac"),
        "♏" to listOf("scorpio", "zodiac"), "♐" to listOf("sagittarius", "zodiac"),
        "♑" to listOf("capricorn", "zodiac"), "♒" to listOf("aquarius", "zodiac"), "♓" to listOf("pisces", "zodiac"), "🆔" to listOf("id", "identity"),
        "⚛️" to listOf("atom", "science"), "🉑" to listOf("accept", "ok"), "☢️" to listOf("radioactive", "radiation"), "☣️" to listOf("biohazard"),
        "📴" to listOf("phone off", "mobile off"), "📳" to listOf("vibration", "phone"),
        "🈶" to listOf("japanese", "has"), "🈚" to listOf("japanese", "nothing"), "🈸" to listOf("japanese", "application"), "🈺" to listOf("japanese", "business"),
        "🈷️" to listOf("japanese", "monthly"), "✴️" to listOf("star", "sparkle"), "🆚" to listOf("vs", "versus"), "💮" to listOf("flower", "seal", "white flower"),
        "🉐" to listOf("japanese", "bargain"), "㊙️" to listOf("secret", "japanese"),
        "㊗️" to listOf("congratulations", "japanese"), "🧿" to listOf("nazar", "amulet", "evil eye"), "🈴" to listOf("japanese", "passing"), "🈵" to listOf("japanese", "full"),
        "🔞" to listOf("no entry", "underage", "18"), "✳️" to listOf("eight pointed star"), "❇️" to listOf("sparkle", "sparkles"), "✴️" to listOf("star", "sparkle"),
        "💲" to listOf("dollar", "currency"), "⚜️" to listOf("fleur de lis")
    )

    private val KEYWORDS: Map<String, List<String>> =
        KW_SMILEYS + KW_ANIMALS + KW_FOOD + KW_ACTIVITY + KW_TRAVEL + KW_OBJECTS + KW_SYMBOLS

    fun search(queryRaw: String, limit: Int = 40): List<String> {
        val q = queryRaw.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        val terms = q.split(Regex("[\\s-]+")).filter { it.isNotEmpty() }
        if (terms.isEmpty()) return emptyList()
        val results = ArrayList<String>()
        for (e in FLAT) {
            val keywords = KEYWORDS[e]
            if (keywords == null) continue
            if (keywords.isEmpty()) continue
            val haystack = (listOf(e) + keywords).joinToString(" ")
            if (terms.all { haystack.contains(it) }) {
                results.add(e)
                if (results.size >= limit) break
            }
        }
        return results
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