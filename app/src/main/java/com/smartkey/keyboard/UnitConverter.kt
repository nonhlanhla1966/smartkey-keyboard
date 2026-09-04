package com.smartkey.keyboard

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Offline unit converter supporting common unit categories.
 * Each category defines a set of units with a conversion factor to a base unit.
 * Pure JVM logic so it can be unit tested without an Android device.
 */
class UnitConverter {

    enum class Category(val label: String) {
        LENGTH("Length"),
        WEIGHT("Weight"),
        TEMPERATURE("Temperature"),
        AREA("Area"),
        VOLUME("Volume"),
        SPEED("Speed"),
        TIME("Time")
    }

    class Unit(val symbol: String, val name: String, val toBase: Double, val offset: Double = 0.0)

    companion object {
        private val MC = MathContext(15)

        val LENGTH = listOf(
            Unit("mm", "Millimeter", 0.001),
            Unit("cm", "Centimeter", 0.01),
            Unit("m", "Meter", 1.0),
            Unit("km", "Kilometer", 1000.0),
            Unit("in", "Inch", 0.0254),
            Unit("ft", "Foot", 0.3048),
            Unit("yd", "Yard", 0.9144),
            Unit("mi", "Mile", 1609.344)
        )

        val WEIGHT = listOf(
            Unit("mg", "Milligram", 0.000001),
            Unit("g", "Gram", 0.001),
            Unit("kg", "Kilogram", 1.0),
            Unit("t", "Tonne", 1000.0),
            Unit("oz", "Ounce", 0.0283495),
            Unit("lb", "Pound", 0.45359237)
        )

        // Celsius base; Kelvin offset +273.15 after converting to base Celsius first
        val TEMPERATURE = listOf(
            Unit("°C", "Celsius", 1.0, 0.0),
            Unit("°F", "Fahrenheit", 5.0 / 9.0, -32.0),
            Unit("K", "Kelvin", 1.0, -273.15)
        )

        val AREA = listOf(
            Unit("mm²", "Square mm", 0.000001),
            Unit("cm²", "Square cm", 0.0001),
            Unit("m²", "Square meter", 1.0),
            Unit("ha", "Hectare", 10000.0),
            Unit("km²", "Square km", 1000000.0),
            Unit("ft²", "Square foot", 0.092903),
            Unit("ac", "Acre", 4046.86)
        )

        val VOLUME = listOf(
            Unit("mL", "Milliliter", 0.001),
            Unit("L", "Liter", 1.0),
            Unit("m³", "Cubic meter", 1000.0),
            Unit("tsp", "Teaspoon", 0.00492892),
            Unit("tbsp", "Tablespoon", 0.0147868),
            Unit("fl oz", "Fluid ounce", 0.0295735),
            Unit("cup", "Cup", 0.236588),
            Unit("gal", "Gallon", 3.78541)
        )

        val SPEED = listOf(
            Unit("m/s", "Meter/sec", 3.6),
            Unit("km/h", "Km/hour", 1.0),
            Unit("mph", "Miles/hour", 1.60934),
            Unit("kt", "Knot", 1.852),
            Unit("fps", "Foot/sec", 1.09728)
        )

        val TIME = listOf(
            Unit("ms", "Millisecond", 0.000001),
            Unit("s", "Second", 1.0),
            Unit("min", "Minute", 60.0),
            Unit("h", "Hour", 3600.0),
            Unit("day", "Day", 86400.0),
            Unit("week", "Week", 604800.0),
            Unit("yr", "Year", 31536000.0)
        )

        fun unitsFor(category: Category): List<Unit> = when (category) {
            Category.LENGTH -> LENGTH
            Category.WEIGHT -> WEIGHT
            Category.TEMPERATURE -> TEMPERATURE
            Category.AREA -> AREA
            Category.VOLUME -> VOLUME
            Category.SPEED -> SPEED
            Category.TIME -> TIME
        }
    }

    /**
     * Convert [value] from unit with symbol [from] to unit with symbol [to] within [category].
     * Returns null if units are invalid for the category.
     */
    fun convert(category: Category, value: Double, from: String, to: String): Double? {
        val units = unitsFor(category)
        val fu = units.firstOrNull { it.symbol == from } ?: return null
        val tu = units.firstOrNull { it.symbol == to } ?: return null
        // Convert value -> base (with offset for temperature), then base -> target
        val inBase = (value + fu.offset) * fu.toBase
        val out = inBase / tu.toBase - tu.offset
        return BigDecimal(out).round(MC).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toDouble()
    }
}