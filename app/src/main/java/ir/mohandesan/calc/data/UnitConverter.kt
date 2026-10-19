package ir.mohandesan.calc.data

import kotlin.math.round

/**
 * Unit conversion engine. Each category defines its base unit and factor-to-base for every unit.
 * Temperature is special-cased because its conversions are affine, not multiplicative.
 */
object UnitConverter {

    enum class Category(val key: String, val titleRes: Int) {
        LENGTH("length", ir.mohandesan.calc.R.string.converter_length),
        MASS("mass", ir.mohandesan.calc.R.string.converter_mass),
        TEMPERATURE("temp", ir.mohandesan.calc.R.string.converter_temp),
        SPEED("speed", ir.mohandesan.calc.R.string.converter_speed),
        AREA("area", ir.mohandesan.calc.R.string.converter_area),
        VOLUME("volume", ir.mohandesan.calc.R.string.converter_volume),
        ENERGY("energy", ir.mohandesan.calc.R.string.converter_energy),
        PRESSURE("pressure", ir.mohandesan.calc.R.string.converter_pressure),
        TIME("time", ir.mohandesan.calc.R.string.converter_time);

        companion object {
            fun from(key: String): Category = entries.first { it.key == key }
        }
    }

    data class UnitDef(val symbol: String, val name: String, val toBase: Double)

    /** All units with their conversion factor to the base unit of their category. */
    val units: Map<Category, List<UnitDef>> = mapOf(
        Category.LENGTH to listOf(
            UnitDef("m", "Meter", 1.0),
            UnitDef("km", "Kilometer", 1e3),
            UnitDef("cm", "Centimeter", 1e-2),
            UnitDef("mm", "Millimeter", 1e-3),
            UnitDef("μm", "Micrometer", 1e-6),
            UnitDef("nm", "Nanometer", 1e-9),
            UnitDef("mi", "Mile", 1609.344),
            UnitDef("yd", "Yard", 0.9144),
            UnitDef("ft", "Foot", 0.3048),
            UnitDef("in", "Inch", 0.0254),
            UnitDef("nmi", "Nautical Mile", 1852.0),
            UnitDef("ly", "Light Year", 9.4607304725808e15)
        ),
        Category.MASS to listOf(
            UnitDef("kg", "Kilogram", 1.0),
            UnitDef("g", "Gram", 1e-3),
            UnitDef("mg", "Milligram", 1e-6),
            UnitDef("μg", "Microgram", 1e-9),
            UnitDef("t", "Tonne", 1e3),
            UnitDef("lb", "Pound", 0.45359237),
            UnitDef("oz", "Ounce", 0.028349523125),
            UnitDef("st", "Stone", 6.35029318),
            UnitDef("ton_us", "US Ton", 907.18474),
            UnitDef("u", "Atomic Mass Unit", 1.66053906660e-27)
        ),
        Category.TEMPERATURE to listOf(
            UnitDef("K", "Kelvin", 1.0),
            UnitDef("°C", "Celsius", 1.0),
            UnitDef("°F", "Fahrenheit", 1.0),
            UnitDef("°R", "Rankine", 1.0)
        ),
        Category.SPEED to listOf(
            UnitDef("m/s", "Meter/sec", 1.0),
            UnitDef("km/h", "Km/hour", 1.0 / 3.6),
            UnitDef("mph", "Mile/hour", 0.44704),
            UnitDef("ft/s", "Foot/sec", 0.3048),
            UnitDef("kn", "Knot", 0.5144444444),
            UnitDef("Mach", "Mach (sea level)", 340.29)
        ),
        Category.AREA to listOf(
            UnitDef("m²", "Square Meter", 1.0),
            UnitDef("km²", "Square Kilometer", 1e6),
            UnitDef("cm²", "Square Centimeter", 1e-4),
            UnitDef("ha", "Hectare", 1e4),
            UnitDef("ac", "Acre", 4046.8564224),
            UnitDef("mi²", "Square Mile", 2.589988110336e6),
            UnitDef("ft²", "Square Foot", 0.09290304),
            UnitDef("in²", "Square Inch", 6.4516e-4)
        ),
        Category.VOLUME to listOf(
            UnitDef("L", "Liter", 1e-3),
            UnitDef("mL", "Milliliter", 1e-6),
            UnitDef("m³", "Cubic Meter", 1.0),
            UnitDef("cm³", "Cubic Centimeter", 1e-6),
            UnitDef("gal_us", "Gallon (US)", 3.785411784e-3),
            UnitDef("gal_uk", "Gallon (UK)", 4.54609e-3),
            UnitDef("qt", "Quart (US)", 9.46352946e-4),
            UnitDef("pt", "Pint (US)", 4.73176473e-4),
            UnitDef("fl_oz", "Fluid Ounce (US)", 2.95735295625e-5),
            UnitDef("ft³", "Cubic Foot", 0.028316846592)
        ),
        Category.ENERGY to listOf(
            UnitDef("J", "Joule", 1.0),
            UnitDef("kJ", "Kilojoule", 1e3),
            UnitDef("cal", "Calorie", 4.184),
            UnitDef("kcal", "Kilocalorie", 4184.0),
            UnitDef("Wh", "Watt-hour", 3600.0),
            UnitDef("kWh", "Kilowatt-hour", 3.6e6),
            UnitDef("eV", "Electronvolt", 1.602176634e-19),
            UnitDef("BTU", "British Thermal Unit", 1055.05585262),
            UnitDef("ft·lb", "Foot-pound", 1.3558179483314),
            UnitDef("erg", "Erg", 1e-7)
        ),
        Category.PRESSURE to listOf(
            UnitDef("Pa", "Pascal", 1.0),
            UnitDef("kPa", "Kilopascal", 1e3),
            UnitDef("MPa", "Megapascal", 1e6),
            UnitDef("bar", "Bar", 1e5),
            UnitDef("atm", "Atmosphere", 101325.0),
            UnitDef("mmHg", "Millimeter of Mercury", 133.322387415),
            UnitDef("psi", "Pound/sq Inch", 6894.757293168),
            UnitDef("torr", "Torr", 133.32236842105263)
        ),
        Category.TIME to listOf(
            UnitDef("s", "Second", 1.0),
            UnitDef("ms", "Millisecond", 1e-3),
            UnitDef("μs", "Microsecond", 1e-6),
            UnitDef("ns", "Nanosecond", 1e-9),
            UnitDef("min", "Minute", 60.0),
            UnitDef("h", "Hour", 3600.0),
            UnitDef("d", "Day", 86400.0),
            UnitDef("wk", "Week", 604800.0),
            UnitDef("mo", "Month (30d)", 2.592e6),
            UnitDef("yr", "Year (365d)", 3.1536e7),
            UnitDef("dec", "Decade", 3.1536e8),
            UnitDef("cent", "Century", 3.1536e9)
        )
    )

    /** Converts [value] from [fromSymbol] to [toSymbol] within [category]. */
    fun convert(value: Double, category: Category, fromSymbol: String, toSymbol: String): Double {
        if (fromSymbol == toSymbol) return value
        if (category == Category.TEMPERATURE) return convertTemperature(value, fromSymbol, toSymbol)
        val list = units[category] ?: error("Unknown category: $category")
        val from = list.first { it.symbol == fromSymbol }
        val to = list.first { it.symbol == toSymbol }
        val baseValue = value * from.toBase
        return baseValue / to.toBase
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        // First, to Kelvin
        val kelvin = when (from) {
            "K" -> value
            "°C" -> value + 273.15
            "°F" -> (value - 32) * 5.0 / 9.0 + 273.15
            "°R" -> value * 5.0 / 9.0
            else -> error("Unknown temperature unit: $from")
        }
        // Then from Kelvin to target
        return when (to) {
            "K" -> kelvin
            "°C" -> kelvin - 273.15
            "°F" -> (kelvin - 273.15) * 9.0 / 5.0 + 32
            "°R" -> kelvin * 9.0 / 5.0
            else -> error("Unknown temperature unit: $to")
        }
    }

    /** Rounds to a sensible precision to avoid floating point noise in display. */
    fun roundTo(value: Double, decimals: Int = 8): Double {
        if (value.isNaN() || value.isInfinite()) return value
        val factor = Math.pow(10.0, decimals.toDouble())
        return round(value * factor) / factor
    }
}
