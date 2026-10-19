package ir.mohandesan.calc.model

/**
 * Represents a single calculator key in the keypad grid.
 *
 * @param label    Text shown on the key (e.g. "sin", "+", "7")
 * @param insert   Token inserted into the expression when tapped.
 *                 If null, the key is treated as a control (AC, DEL, =).
 * @param type      Visual category of the key.
 * @param action    Optional special action (CLEAR, DELETE, EVALUATE, TOGGLE_2ND, TOGGLE_ANGLE).
 * @param altLabel  Alternative label shown when 2nd is active (optional).
 * @param altInsert Alternative insert when 2nd is active (optional).
 */
data class KeyDef(
    val label: String,
    val insert: String? = null,
    val type: KeyType = KeyType.NUMBER,
    val action: KeyAction? = null,
    val altLabel: String? = null,
    val altInsert: String? = null
)

enum class KeyType { NUMBER, OPERATOR, FUNCTION, SCIENTIFIC, CONTROL, ACCENT }

enum class KeyAction { CLEAR, DELETE, EVALUATE, TOGGLE_2ND, TOGGLE_ANGLE }

/**
 * One item in calculation history.
 */
data class HistoryItem(
    val id: Long,
    val expression: String,
    val result: String,
    val timestamp: Long,
    val angleModeLabel: String
)
