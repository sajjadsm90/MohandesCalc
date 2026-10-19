package ir.mohandesan.calc.data

import kotlin.math.*

/**
 * Immutable complex number representation: a + b*i
 * Supports +, −, ×, ÷, conjugate, modulus, argument, power, sqrt, exp, ln.
 */
data class ComplexNumber(val re: Double, val im: Double) {

    operator fun plus(other: ComplexNumber) = ComplexNumber(re + other.re, im + other.im)
    operator fun minus(other: ComplexNumber) = ComplexNumber(re - other.re, im - other.im)
    operator fun times(other: ComplexNumber): ComplexNumber {
        val r = re * other.re - im * other.im
        val i = re * other.im + im * other.re
        return ComplexNumber(r, i)
    }
    operator fun div(other: ComplexNumber): ComplexNumber {
        val denom = other.re * other.re + other.im * other.im
        if (denom == 0.0) throw ArithmeticException("Division by zero (complex)")
        val r = (re * other.re + im * other.im) / denom
        val i = (im * other.re - re * other.im) / denom
        return ComplexNumber(r, i)
    }

    fun conjugate() = ComplexNumber(re, -im)
    fun abs() = sqrt(re * re + im * im)
    fun arg() = atan2(im, re)

    /** Raises to a real power using polar form. */
    fun pow(exponent: Double): ComplexNumber {
        val r = abs().pow(exponent)
        val theta = arg() * exponent
        return ComplexNumber(r * cos(theta), r * sin(theta))
    }

    /** Principal square root. */
    fun sqrt(): ComplexNumber {
        if (im == 0.0 && re >= 0) return ComplexNumber(kotlin.math.sqrt(re), 0.0)
        val r = abs()
        val theta = arg()
        val rootR = kotlin.math.sqrt(r)
        return ComplexNumber(rootR * cos(theta / 2), rootR * sin(theta / 2))
    }

    /** e^z */
    fun exp(): ComplexNumber {
        val e = exp(re)
        return ComplexNumber(e * cos(im), e * sin(im))
    }

    /** Natural log (principal branch) */
    fun ln(): ComplexNumber = ComplexNumber(ln(abs()), arg())

    override fun toString(): String {
        val r = format(re)
        val i = format(im)
        return when {
            im == 0.0 -> r
            re == 0.0 -> if (im == 1.0) "i" else if (im == -1.0) "-i" else "$i·i"
            im > 0 -> "$r + $i·i"
            else -> "$r − ${format(-im)}·i"
        }
    }

    private fun format(v: Double): String {
        if (v.isNaN()) return "NaN"
        if (v.isInfinite()) return if (v > 0) "∞" else "−∞"
        return if (v == v.toLong().toDouble()) v.toLong().toString()
        else "%.6f".format(v).trimEnd('0').trimEnd('.')
    }
}

object ComplexMath {
    fun sin(z: ComplexNumber): ComplexNumber {
        // sin(a+bi) = sin(a)cosh(b) + i cos(a)sinh(b)
        return ComplexNumber(sin(z.re) * cosh(z.im), cos(z.re) * sinh(z.im))
    }
    fun cos(z: ComplexNumber): ComplexNumber {
        return ComplexNumber(cos(z.re) * cosh(z.im), -sin(z.re) * sinh(z.im))
    }
    fun tan(z: ComplexNumber): ComplexNumber = sin(z) / cos(z)
    fun exp(z: ComplexNumber) = z.exp()
    fun ln(z: ComplexNumber) = z.ln()
    fun sqrt(z: ComplexNumber) = z.sqrt()
    fun pow(base: ComplexNumber, exp: ComplexNumber): ComplexNumber {
        // base^exp = exp(exp * ln(base))
        return (exp * base.ln()).exp()
    }
}
