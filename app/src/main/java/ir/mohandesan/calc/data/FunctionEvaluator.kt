package ir.mohandesan.calc.data

import kotlin.math.abs
import kotlin.math.PI

/**
 * Evaluates a function y = f(x) over a range of x values, for plotting.
 * Internally reuses [CalculatorEngine] by substituting the variable token "x"
 * with the current x value and re-evaluating the expression.
 *
 * Supports all functions/constants/operators of [CalculatorEngine].
 */
class FunctionEvaluator(private val angleMode: AngleMode = AngleMode.RAD) {

    private val engine = CalculatorEngine(angleMode)

    /**
     * Evaluate [expression] at a single point [x].
     * Returns null on error (so the plotter can skip the point).
     */
    fun evalAt(expression: String, x: Double): Double? {
        val xStr = formatNumber(x)
        val substituted = substituteVar(expression, xStr)
        return when (val r = engine.evaluate(substituted)) {
            is EvalResult.Success -> r.value
            is EvalResult.Error -> null
        }
    }

    /**
     * Sample [samples] points of y=f(x) on [xMin, xMax] and adaptively determine
     * y bounds to return a list of (x, y) pairs (null y where undefined).
     */
    fun sample(
        expression: String,
        xMin: Double,
        xMax: Double,
        samples: Int = 400
    ): List<Pair<Double, Double?>> {
        if (xMax <= xMin || samples < 2) return emptyList()
        val step = (xMax - xMin) / (samples - 1)
        val pts = ArrayList<Pair<Double, Double?>>(samples)
        for (i in 0 until samples) {
            val x = xMin + i * step
            pts.add(x to evalAt(expression, x))
        }
        return pts
    }

    /**
     * Auto-compute a sensible y range from sampled points.
     * Returns null if no valid points exist.
     */
    fun autoYRange(points: List<Pair<Double, Double?>>): Pair<Double, Double>? {
        val valid = points.mapNotNull { it.second }
        if (valid.isEmpty()) return null
        var yMin = valid.min()
        var yMax = valid.max()
        if (yMin == yMax) {
            // Avoid a degenerate flat range
            val pad = abs(yMin) * 0.1 + 1.0
            yMin -= pad
            yMax += pad
        }
        // Add 10% padding
        val pad = (yMax - yMin) * 0.1
        return (yMin - pad) to (yMax + pad)
    }

    /**
     * Substitutes variable x in expression with a numeric value.
     * Replaces whole-word 'x' (case-insensitive), but NOT inside identifiers like "exp" or "max".
     */
    private fun substituteVar(expression: String, value: String): String {
        val sb = StringBuilder()
        var i = 0
        val s = expression
        while (i < s.length) {
            val c = s[i]
            if (c == 'x' || c == 'X') {
                // Check whether this x is part of a longer identifier
                val prev = if (i > 0) s[i - 1] else ' '
                val next = if (i + 1 < s.length) s[i + 1] else ' '
                if (prev.isLetterOrDigit() || next.isLetterOrDigit() || prev == '_' || next == '_') {
                    sb.append(c); i++
                } else {
                    sb.append("($value)"); i++
                }
            } else {
                sb.append(c); i++
            }
        }
        return sb.toString()
    }

    private fun formatNumber(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "0"
        // Use enough precision so step granularity is preserved
        return "%.12g".format(v)
    }

    /** Suggested default range for x. */
    fun defaultXRange(): Pair<Double, Double> = -2 * PI to 2 * PI
}
