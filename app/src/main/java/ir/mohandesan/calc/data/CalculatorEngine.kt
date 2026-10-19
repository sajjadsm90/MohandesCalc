package ir.mohandesan.calc.data

import kotlin.math.*

/**
 * Trigonometric angle mode for trig function evaluation.
 */
enum class AngleMode { DEG, RAD, GRAD }

/**
 * Result wrapper for evaluation outcomes.
 */
sealed class EvalResult {
    data class Success(val value: Double, val displayString: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
}

/**
 * Mathematical expression evaluator with shunting-yard parsing.
 *
 * Supported:
 *  - Operators: + - × ÷ ^ % (and unary minus)
 *  - Postfix: ! (factorial)
 *  - Functions: sin, cos, tan, asin, acos, atan,
 *               sinh, cosh, tanh, asinh, acosh, atanh,
 *               log (base 10), ln (natural log),
 *               sqrt, cbrt, abs, exp, floor, ceil, round,
 *               logbase(x, b) custom base log
 *  - Constants: pi (π), e, phi (golden ratio)
 *  - Implicit multiplication: 2π, 3sin(x), (2+3)(4+5)
 *
 * Thread-safe. Each instance carries its own [angleMode].
 */
class CalculatorEngine(var angleMode: AngleMode = AngleMode.DEG) {

    // ---------- Public API ----------

    fun evaluate(expression: String): EvalResult {
        val cleaned = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace("√", "sqrt")
            .replace("∞", "Infinity")
            .trim()
        if (cleaned.isEmpty()) return EvalResult.Error("Empty expression")

        return try {
            val tokens = tokenize(cleaned)
            val rpn = toRpn(tokens)
            val value = evalRpn(rpn)
            when {
                value.isNaN() -> EvalResult.Error("Math error: NaN")
                value.isInfinite() -> EvalResult.Error("Math error: overflow")
                else -> EvalResult.Success(value, formatResult(value))
            }
        } catch (e: ArithmeticException) {
            EvalResult.Error(e.message ?: "Math error")
        } catch (e: IllegalStateException) {
            EvalResult.Error(e.message ?: "Syntax error")
        } catch (e: Exception) {
            EvalResult.Error("Error: ${e.message ?: "unknown"}")
        }
    }

    // ---------- Tokenizer ----------

    private enum class TokType { NUMBER, OP, LPAREN, RPAREN, COMMA, FUNC, CONST, IDENT }

    private data class Token(val type: TokType, val value: String)

    private val functions = setOf(
        "sin", "cos", "tan", "asin", "acos", "atan",
        "sinh", "cosh", "tanh", "asinh", "acosh", "atanh",
        "log", "ln", "sqrt", "cbrt", "abs", "exp",
        "floor", "ceil", "round", "sign",
        "logbase", "root", "mod", "gcd", "lcm",
        "perm", "comb", "gamma"
    )

    private val constants = mapOf(
        "pi" to PI,
        "e" to E,
        "phi" to 1.6180339887498948482, // golden ratio
        "tau" to 2 * PI
    )

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val s = input

        fun isIdentStart(c: Char) = c.isLetter() || c == '_'
        fun isIdentPart(c: Char) = c.isLetterOrDigit() || c == '_'

        while (i < s.length) {
            val c = s[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val start = i
                    var hasDot = false
                    var hasE = false
                    while (i < s.length) {
                        val ch = s[i]
                        if (ch.isDigit()) i++
                        else if (ch == '.' && !hasDot && !hasE) { hasDot = true; i++ }
                        else if ((ch == 'e' || ch == 'E') && !hasE && i + 1 < s.length &&
                            (s[i+1].isDigit() || s[i+1] == '+' || s[i+1] == '-')) {
                            hasE = true; i++
                            if (i < s.length && (s[i] == '+' || s[i] == '-')) i++
                        } else break
                    }
                    tokens.add(Token(TokType.NUMBER, s.substring(start, i)))
                }
                c == '(' -> { tokens.add(Token(TokType.LPAREN, "(")); i++ }
                c == ')' -> { tokens.add(Token(TokType.RPAREN, ")")); i++ }
                c == ',' -> { tokens.add(Token(TokType.COMMA, ",")); i++ }
                c in "+-*/^%" -> {
                    // Distinguish unary minus/plus from binary
                    val prev = tokens.lastOrNull()
                    val isUnary = (prev == null) ||
                        prev!!.type == TokType.OP ||
                        prev.type == TokType.LPAREN ||
                        prev.type == TokType.COMMA
                    if ((c == '-' || c == '+') && isUnary) {
                        // Represent unary as 0 - x / 0 + x by emitting a literal "0" first
                        if (c == '-') tokens.add(Token(TokType.NUMBER, "0"))
                        // For unary + we just skip it
                        if (c == '+') { i++; continue }
                        if (c == '-') { tokens.add(Token(TokType.OP, "-")); i++; continue }
                    }
                    tokens.add(Token(TokType.OP, c.toString()))
                    i++
                }
                c == '!' -> {
                    tokens.add(Token(TokType.OP, "!"))
                    i++
                }
                isIdentStart(c) -> {
                    val start = i
                    while (i < s.length && isIdentPart(s[i])) i++
                    val name = s.substring(start, i).lowercase()
                    when {
                        functions.contains(name) -> tokens.add(Token(TokType.FUNC, name))
                        constants.containsKey(name) -> tokens.add(Token(TokType.CONST, name))
                        else -> throw IllegalStateException("Unknown identifier: $name")
                    }
                }
                else -> throw IllegalStateException("Unexpected character: $c")
            }
        }
        return tokens
    }

    // ---------- Shunting-Yard to RPN ----------

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 2
        "*", "/", "%" -> 3
        "^" -> 4
        "!" -> 5 // postfix, highest
        else -> 0
    }

    private fun isRightAssoc(op: String): Boolean = op == "^"

    private fun toRpn(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()
        var prev: Token? = null

        for (tok in tokens) {
            when (tok.type) {
                TokType.NUMBER, TokType.CONST -> {
                    // Implicit multiplication: if previous was number/const/rparen,
                    // and current is number/const, insert multiplication.
                    if (prev != null && (prev.type == TokType.NUMBER ||
                            prev.type == TokType.CONST ||
                            prev.type == TokType.RPAREN)) {
                        output.add(Token(TokType.OP, "*"))
                    }
                    output.add(tok)
                }
                TokType.FUNC -> {
                    // Implicit multiplication if previous was number/const/rparen
                    if (prev != null && (prev.type == TokType.NUMBER ||
                            prev.type == TokType.CONST ||
                            prev.type == TokType.RPAREN)) {
                        output.add(Token(TokType.OP, "*"))
                    }
                    stack.addLast(tok)
                }
                TokType.COMMA -> {
                    while (stack.isNotEmpty() && stack.last().type != TokType.LPAREN) {
                        output.add(stack.removeLast())
                    }
                    if (stack.isEmpty()) throw IllegalStateException("Misplaced comma")
                }
                TokType.OP -> {
                    if (tok.value == "!") {
                        // postfix, push directly to output (no precedence stack interaction)
                        output.add(tok)
                    } else {
                        while (stack.isNotEmpty()) {
                            val top = stack.last()
                            if (top.type == TokType.FUNC ||
                                (top.type == TokType.OP &&
                                    (precedence(top.value) > precedence(tok.value) ||
                                        (precedence(top.value) == precedence(tok.value) && !isRightAssoc(tok.value))))) {
                                output.add(stack.removeLast())
                            } else break
                        }
                        stack.addLast(tok)
                    }
                }
                TokType.LPAREN -> {
                    if (prev != null && (prev.type == TokType.NUMBER ||
                            prev.type == TokType.CONST ||
                            prev.type == TokType.RPAREN)) {
                        // Implicit multiplication: 2(3+4) = 2*(3+4)
                        output.add(Token(TokType.OP, "*"))
                    }
                    stack.addLast(tok)
                }
                TokType.RPAREN -> {
                    while (stack.isNotEmpty() && stack.last().type != TokType.LPAREN) {
                        output.add(stack.removeLast())
                    }
                    if (stack.isEmpty()) throw IllegalStateException("Mismatched parentheses")
                    stack.removeLast() // pop LPAREN
                    // If function on top of stack, move it to output
                    if (stack.isNotEmpty() && stack.last().type == TokType.FUNC) {
                        output.add(stack.removeLast())
                    }
                }
                else -> {}
            }
            prev = tok
        }
        while (stack.isNotEmpty()) {
            val top = stack.removeLast()
            if (top.type == TokType.LPAREN) throw IllegalStateException("Mismatched parentheses")
            output.add(top)
        }
        return output
    }

    // ---------- RPN evaluation ----------

    private fun evalRpn(rpn: List<Token>): Double {
        val stack = ArrayDeque<Double>()
        for (tok in rpn) {
            when (tok.type) {
                TokType.NUMBER -> stack.addLast(tok.value.toDouble())
                TokType.CONST -> stack.addLast(constants[tok.value] ?: error("Unknown constant"))
                TokType.OP -> {
                    when (tok.value) {
                        "!" -> {
                            val a = stack.removeLast()
                            stack.addLast(factorial(a))
                        }
                        else -> {
                            val b = stack.removeLast()
                            val a = stack.removeLast()
                            stack.addLast(when (tok.value) {
                                "+" -> a + b
                                "-" -> a - b
                                "*" -> a * b
                                "/" -> if (b == 0.0) throw ArithmeticException("Division by zero") else a / b
                                "%" -> a % b
                                "^" -> a.pow(b)
                                else -> error("Unknown operator: ${tok.value}")
                            })
                        }
                    }
                }
                TokType.FUNC -> {
                    applyFunction(tok.value, stack)
                }
                else -> {}
            }
        }
        if (stack.size != 1) throw IllegalStateException("Malformed expression")
        return stack.last()
    }

    private fun applyFunction(name: String, stack: ArrayDeque<Double>) {
        when (name) {
            // Single-arg
            "sin" -> { val a = stack.removeLast(); stack.addLast(trig(sin, a)) }
            "cos" -> { val a = stack.removeLast(); stack.addLast(trig(cos, a)) }
            "tan" -> { val a = stack.removeLast(); stack.addLast(trig(tan, a)) }
            "asin" -> { val a = stack.removeLast(); stack.addLast(invTrig(asin, a)) }
            "acos" -> { val a = stack.removeLast(); stack.addLast(invTrig(acos, a)) }
            "atan" -> { val a = stack.removeLast(); stack.addLast(invTrig(atan, a)) }
            "sinh" -> { val a = stack.removeLast(); stack.addLast(sinh(a)) }
            "cosh" -> { val a = stack.removeLast(); stack.addLast(cosh(a)) }
            "tanh" -> { val a = stack.removeLast(); stack.addLast(tanh(a)) }
            "asinh" -> { val a = stack.removeLast(); stack.addLast(asinh(a)) }
            "acosh" -> { val a = stack.removeLast(); stack.addLast(acosh(a)) }
            "atanh" -> { val a = stack.removeLast(); stack.addLast(atanh(a)) }
            "log" -> { val a = stack.removeLast(); stack.addLast(log10(a)) }
            "ln" -> { val a = stack.removeLast(); stack.addLast(ln(a)) }
            "sqrt" -> { val a = stack.removeLast(); stack.addLast(kotlin.math.sqrt(a)) }
            "cbrt" -> { val a = stack.removeLast(); stack.addLast(cbrt(a)) }
            "abs" -> { val a = stack.removeLast(); stack.addLast(abs(a)) }
            "exp" -> { val a = stack.removeLast(); stack.addLast(exp(a)) }
            "floor" -> { val a = stack.removeLast(); stack.addLast(floor(a)) }
            "ceil" -> { val a = stack.removeLast(); stack.addLast(ceil(a)) }
            "round" -> { val a = stack.removeLast(); stack.addLast(round(a).toDouble()) }
            "sign" -> { val a = stack.removeLast(); stack.addLast(sign(a).toDouble()) }
            "gamma" -> { val a = stack.removeLast(); stack.addLast(gamma(a)) }
            // Two-arg
            "logbase" -> {
                val base = stack.removeLast()
                val x = stack.removeLast()
                stack.addLast(log10(x) / log10(base))
            }
            "root" -> {
                val n = stack.removeLast()
                val x = stack.removeLast()
                stack.addLast(x.pow(1.0 / n))
            }
            "mod" -> {
                val b = stack.removeLast()
                val a = stack.removeLast()
                stack.addLast(a % b)
            }
            "gcd" -> {
                val b = stack.removeLast().toLong()
                val a = stack.removeLast().toLong()
                stack.addLast(gcd(a, b).toDouble())
            }
            "lcm" -> {
                val b = stack.removeLast().toLong()
                val a = stack.removeLast().toLong()
                stack.addLast(lcm(a, b).toDouble())
            }
            "perm" -> { // P(n,k) = n!/(n-k)!
                val k = stack.removeLast()
                val n = stack.removeLast()
                stack.addLast(factorial(n) / factorial(n - k))
            }
            "comb" -> { // C(n,k) = n!/(k!(n-k)!)
                val k = stack.removeLast()
                val n = stack.removeLast()
                stack.addLast(factorial(n) / (factorial(k) * factorial(n - k)))
            }
            else -> error("Unknown function: $name")
        }
    }

    /** Wrap a trig function so the argument is interpreted according to the angle mode. */
    private fun trig(fn: (Double) -> Double, x: Double): Double = when (angleMode) {
        AngleMode.DEG -> fn(toRadians(x))
        AngleMode.RAD -> fn(x)
        AngleMode.GRAD -> fn(x * PI / 200.0)
    }

    /** Wrap an inverse trig function so the result is given in the current angle mode. */
    private fun invTrig(fn: (Double) -> Double, x: Double): Double = when (angleMode) {
        AngleMode.DEG -> toDegrees(fn(x))
        AngleMode.RAD -> fn(x)
        AngleMode.GRAD -> fn(x) * 200.0 / PI
    }

    private fun factorial(n: Double): Double {
        if (n < 0) throw ArithmeticException("Factorial of negative number")
        if (n > 170) throw ArithmeticException("Factorial overflow")
        val k = n.toLong()
        if (k.toDouble() != n) {
            // Use gamma function for non-integer arguments
            return gamma(n + 1)
        }
        var result = 1.0
        for (i in 2..k) result *= i
        return result
    }

    /** Lanczos approximation of the gamma function. */
    private fun gamma(z: Double): Double {
        if (z < 0.5) return PI / (sin(PI * z) * gamma(1 - z))
        val g = 7.0
        val c = doubleArrayOf(
            0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        )
        var x = c[0]
        var zz = z - 1.0
        for (i in 1 until c.size) x += c[i] / (zz + i)
        val t = zz + g + 0.5
        return sqrt(2 * PI) * pow(t, zz + 0.5) * exp(-t) * x
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)
    private fun lcm(a: Long, b: Long): Long = if (a == 0L || b == 0L) 0 else abs(a * b) / gcd(a, b)

    /** Format the result for display: integers without decimals, others with up to 10 significant digits. */
    fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "−∞"
        if (value == 0.0) return "0"
        val absV = abs(value)
        // Scientific notation for very large/small numbers
        if (absV >= 1e15 || (absV < 1e-9 && absV > 0)) {
            return "%.6E".format(value).replace("E", "×10^").replace("+", "")
        }
        // Integer values
        if (value == value.toLong().toDouble() && absV < 1e15) {
            return value.toLong().toString()
        }
        // Otherwise: up to 10 significant digits, trimmed
        var s = "%.10f".format(value).trimEnd('0').trimEnd('.')
        // Negative zero guard
        if (s == "-0") s = "0"
        return s
    }
}
