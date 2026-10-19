package ir.mohandesan.calc.model

import ir.mohandesan.calc.data.AngleMode

/**
 * Defines the layout of the calculator keypad.
 *
 * The keypad is organized as a 5-column grid (Portrait) with two "shift" layers
 * controlled by the [2nd] key. Each row is a list of [KeyDef].
 *
 * Layout:
 *  Row 0: angle / 2nd / constants
 *  Row 1: function row (sin, cos, tan, ln, log)
 *  Row 2: function row 2 (π, e, ^, √, !)
 *  Row 3: digits row (7,8,9,÷,DEL)
 *  Row 4: digits row (4,5,6,×,AC)
 *  Row 5: digits row (1,2,3,−,())
 *  Row 6: digits row (0,.,%,+,=)
 */
object KeypadLayout {

    fun primaryRows(): List<List<KeyDef>> = listOf(
        // Row 0 - scientific
        listOf(
            KeyDef("2nd",   type = KeyType.CONTROL,    action = KeyAction.TOGGLE_2ND),
            KeyDef("DEG",   type = KeyType.CONTROL,    action = KeyAction.TOGGLE_ANGLE),
            KeyDef("π",     insert = "π",  type = KeyType.SCIENTIFIC),
            KeyDef("e",     insert = "e",  type = KeyType.SCIENTIFIC),
            KeyDef("φ",     insert = "phi",type = KeyType.SCIENTIFIC)
        ),
        // Row 1 - trig + log
        listOf(
            KeyDef("sin",  insert = "sin(",   type = KeyType.FUNCTION, altLabel = "asin",  altInsert = "asin("),
            KeyDef("cos",  insert = "cos(",   type = KeyType.FUNCTION, altLabel = "acos",  altInsert = "acos("),
            KeyDef("tan",  insert = "tan(",   type = KeyType.FUNCTION, altLabel = "atan",  altInsert = "atan("),
            KeyDef("ln",   insert = "ln(",   type = KeyType.FUNCTION, altLabel = "eˣ",    altInsert = "exp("),
            KeyDef("log",  insert = "log(",   type = KeyType.FUNCTION, altLabel = "10ˣ",   altInsert = "10^")
        ),
        // Row 2 - power, root, factorial
        listOf(
            KeyDef("x²",   insert = "^2",     type = KeyType.SCIENTIFIC, altLabel = "x³",   altInsert = "^3"),
            KeyDef("xʸ",   insert = "^",      type = KeyType.SCIENTIFIC),
            KeyDef("√",    insert = "sqrt(",   type = KeyType.SCIENTIFIC, altLabel = "∛",   altInsert = "cbrt("),
            KeyDef("x⁻¹",  insert = "^-1",    type = KeyType.SCIENTIFIC, altLabel = "|x|",  altInsert = "abs("),
            KeyDef("n!",   insert = "!",      type = KeyType.SCIENTIFIC, altLabel = "nCr",  altInsert = "comb(,)")
        ),
        // Row 3 - 7 8 9 ÷ DEL
        listOf(
            KeyDef("7", insert = "7", type = KeyType.NUMBER),
            KeyDef("8", insert = "8", type = KeyType.NUMBER),
            KeyDef("9", insert = "9", type = KeyType.NUMBER),
            KeyDef("÷", insert = "÷", type = KeyType.OPERATOR),
            KeyDef("DEL", type = KeyType.CONTROL, action = KeyAction.DELETE)
        ),
        // Row 4 - 4 5 6 × AC
        listOf(
            KeyDef("4", insert = "4", type = KeyType.NUMBER),
            KeyDef("5", insert = "5", type = KeyType.NUMBER),
            KeyDef("6", insert = "6", type = KeyType.NUMBER),
            KeyDef("×", insert = "×", type = KeyType.OPERATOR),
            KeyDef("AC", type = KeyType.CONTROL, action = KeyAction.CLEAR)
        ),
        // Row 5 - 1 2 3 − ( )
        listOf(
            KeyDef("1", insert = "1", type = KeyType.NUMBER),
            KeyDef("2", insert = "2", type = KeyType.NUMBER),
            KeyDef("3", insert = "3", type = KeyType.NUMBER),
            KeyDef("−", insert = "−", type = KeyType.OPERATOR),
            KeyDef("( )", insert = "(", type = KeyType.OPERATOR)
        ),
        // Row 6 - 0 . % + =
        listOf(
            KeyDef("0",   insert = "0",  type = KeyType.NUMBER),
            KeyDef(".",    insert = ".",  type = KeyType.NUMBER),
            KeyDef("%",    insert = "%",  type = KeyType.OPERATOR),
            KeyDef("+",    insert = "+",  type = KeyType.OPERATOR),
            KeyDef("=",     type = KeyType.ACCENT, action = KeyAction.EVALUATE)
        )
    )

    fun labelForKey(key: KeyDef, secondActive: Boolean): String =
        if (secondActive && key.altLabel != null) key.altLabel!! else key.label

    fun insertForKey(key: KeyDef, secondActive: Boolean): String? =
        if (secondActive && key.altInsert != null) key.altInsert else key.insert

    /** Map [AngleMode] to the keypad label. */
    fun angleLabel(mode: AngleMode): String = when (mode) {
        AngleMode.DEG -> "DEG"
        AngleMode.RAD -> "RAD"
        AngleMode.GRAD -> "GRAD"
    }
}
