package ir.mohandesan.calc.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class CalculatorEngineTest {

    private val engine = CalculatorEngine(AngleMode.DEG)

    @Test
    fun `simple addition`() {
        val r = engine.evaluate("2 + 3")
        assertTrue(r is EvalResult.Success)
        assertEquals(5.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `order of operations`() {
        val r = engine.evaluate("2 + 3 × 4")
        assertTrue(r is EvalResult.Success)
        assertEquals(14.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `parentheses override precedence`() {
        val r = engine.evaluate("(2 + 3) × 4")
        assertTrue(r is EvalResult.Success)
        assertEquals(20.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `power is right associative`() {
        val r = engine.evaluate("2 ^ 3 ^ 2")
        assertTrue(r is EvalResult.Success)
        // 2^(3^2) = 2^9 = 512, NOT (2^3)^2 = 64
        assertEquals(512.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `implicit multiplication`() {
        val r = engine.evaluate("2π")
        assertTrue(r is EvalResult.Success)
        assertEquals(2 * PI, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `implicit multiplication with function`() {
        val r = engine.evaluate("2 sin(30)")
        assertTrue(r is EvalResult.Success)
        // sin(30°) = 0.5, so 2 * 0.5 = 1
        assertEquals(1.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `sin in degrees`() {
        val r = engine.evaluate("sin(30)")
        assertTrue(r is EvalResult.Success)
        assertEquals(0.5, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `sin in radians`() {
        engine.angleMode = AngleMode.RAD
        val r = engine.evaluate("sin(π/2)")
        assertTrue(r is EvalResult.Success)
        assertEquals(1.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `log base 10`() {
        val r = engine.evaluate("log(1000)")
        assertTrue(r is EvalResult.Success)
        assertEquals(3.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `natural log`() {
        val r = engine.evaluate("ln(e)")
        assertTrue(r is EvalResult.Success)
        assertEquals(1.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `factorial of 5`() {
        val r = engine.evaluate("5!")
        assertTrue(r is EvalResult.Success)
        assertEquals(120.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `square root`() {
        val r = engine.evaluate("sqrt(16)")
        assertTrue(r is EvalResult.Success)
        assertEquals(4.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `custom log base`() {
        val r = engine.evaluate("logbase(8, 2)")
        assertTrue(r is EvalResult.Success)
        assertEquals(3.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `division by zero returns error`() {
        val r = engine.evaluate("1 / 0")
        assertTrue(r is EvalResult.Error)
    }

    @Test
    fun `nested functions`() {
        val r = engine.evaluate("sin(cos(0))")
        assertTrue(r is EvalResult.Success)
        // cos(0) = 1, sin(1°) ≈ 0.017452
        assertEquals(0.017452406, (r as EvalResult.Success).value, 1e-6)
    }

    @Test
    fun `negative numbers`() {
        val r = engine.evaluate("-5 + 3")
        assertTrue(r is EvalResult.Success)
        assertEquals(-2.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `decimal numbers`() {
        val r = engine.evaluate("0.1 + 0.2")
        assertTrue(r is EvalResult.Success)
        assertEquals(0.3, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `scientific notation`() {
        val r = engine.evaluate("1e3 + 2e3")
        assertTrue(r is EvalResult.Success)
        assertEquals(3000.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `percentage operator`() {
        val r = engine.evaluate("100 % 30")
        assertTrue(r is EvalResult.Success)
        assertEquals(10.0, (r as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun `empty expression returns error`() {
        val r = engine.evaluate("")
        assertTrue(r is EvalResult.Error)
    }

    @Test
    fun `malformed expression returns error`() {
        val r = engine.evaluate("2 + ")
        assertTrue(r is EvalResult.Error)
    }

    @Test
    fun `format result integer`() {
        assertEquals("5", engine.formatResult(5.0))
    }

    @Test
    fun `format result decimal`() {
        assertEquals("3.14", engine.formatResult(3.14))
    }
}
