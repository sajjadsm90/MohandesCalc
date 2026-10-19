package ir.mohandesan.calc.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitConverterTest {

    @Test
    fun `meters to kilometers`() {
        assertEquals(0.5, UnitConverter.convert(500.0, UnitConverter.Category.LENGTH, "m", "km"), 1e-9)
    }

    @Test
    fun `kilometers to miles`() {
        // 1 km = 0.621371 miles
        assertEquals(0.621371, UnitConverter.convert(1.0, UnitConverter.Category.LENGTH, "km", "mi"), 1e-5)
    }

    @Test
    fun `celsius to fahrenheit`() {
        assertEquals(32.0, UnitConverter.convert(0.0, UnitConverter.Category.TEMPERATURE, "°C", "°F"), 1e-9)
        assertEquals(212.0, UnitConverter.convert(100.0, UnitConverter.Category.TEMPERATURE, "°C", "°F"), 1e-9)
    }

    @Test
    fun `fahrenheit to kelvin`() {
        // 32°F = 273.15 K
        assertEquals(273.15, UnitConverter.convert(32.0, UnitConverter.Category.TEMPERATURE, "°F", "K"), 1e-5)
    }

    @Test
    fun `kilograms to pounds`() {
        // 1 kg = 2.20462 lb
        assertEquals(2.20462, UnitConverter.convert(1.0, UnitConverter.Category.MASS, "kg", "lb"), 1e-4)
    }

    @Test
    fun `same unit returns input`() {
        assertEquals(42.0, UnitConverter.convert(42.0, UnitConverter.Category.LENGTH, "m", "m"), 1e-9)
    }

    @Test
    fun `meters per second to km per hour`() {
        // 1 m/s = 3.6 km/h
        assertEquals(3.6, UnitConverter.convert(1.0, UnitConverter.Category.SPEED, "m/s", "km/h"), 1e-9)
    }

    @Test
    fun `joules to kilocalories`() {
        // 1 kcal = 4184 J → 1 J ≈ 0.000239006 kcal
        assertEquals(0.000239, UnitConverter.convert(1.0, UnitConverter.Category.ENERGY, "J", "kcal"), 1e-6)
    }
}
