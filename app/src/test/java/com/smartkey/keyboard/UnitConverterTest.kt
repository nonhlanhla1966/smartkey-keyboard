package com.smartkey.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UnitConverterTest {

    private val converter = UnitConverter()

    @Test
    fun lengthMetersToKilometers() {
        val r = converter.convert(UnitConverter.Category.LENGTH, 1500.0, "m", "km")
        assertEquals(1.5, r ?: -1.0, 1e-6)
    }

    @Test
    fun lengthMilesToFeet() {
        val r = converter.convert(UnitConverter.Category.LENGTH, 1.0, "mi", "ft")
        assertEquals(5280.0, r ?: -1.0, 1.0)
    }

    @Test
    fun weightKilogramsToPounds() {
        val r = converter.convert(UnitConverter.Category.WEIGHT, 1.0, "kg", "lb")
        assertEquals(2.20462, r ?: -1.0, 0.001)
    }

    @Test
    fun temperatureCelsiusToFahrenheit() {
        val r = converter.convert(UnitConverter.Category.TEMPERATURE, 100.0, "°C", "°F")
        assertEquals(212.0, r ?: -1.0, 1e-6)
    }

    @Test
    fun temperatureFahrenheitToCelsius() {
        val r = converter.convert(UnitConverter.Category.TEMPERATURE, 32.0, "°F", "°C")
        assertEquals(0.0, r ?: -1.0, 1e-6)
    }

    @Test
    fun temperatureKelvinToCelsius() {
        val r = converter.convert(UnitConverter.Category.TEMPERATURE, 273.15, "K", "°C")
        assertEquals(0.0, r ?: -1.0, 1e-6)
    }

    @Test
    fun areaAcresToSquareMeters() {
        val r = converter.convert(UnitConverter.Category.AREA, 1.0, "ac", "m²")
        assertEquals(4046.86, r ?: -1.0, 1.0)
    }

    @Test
    fun volumeGallonsToLiters() {
        val r = converter.convert(UnitConverter.Category.VOLUME, 1.0, "gal", "L")
        assertEquals(3.78541, r ?: -1.0, 0.001)
    }

    @Test
    fun speedKmhToMph() {
        val r = converter.convert(UnitConverter.Category.SPEED, 100.0, "km/h", "mph")
        assertEquals(62.137, r ?: -1.0, 0.01)
    }

    @Test
    fun timeHoursToMinutes() {
        val r = converter.convert(UnitConverter.Category.TIME, 2.0, "h", "min")
        assertEquals(120.0, r ?: -1.0, 1e-6)
    }

    @Test
    fun invalidUnitReturnsNull() {
        assertNull(converter.convert(UnitConverter.Category.LENGTH, 1.0, "m", "kg"))
    }

    @Test
    fun allCategoriesHaveUnits() {
        for (cat in UnitConverter.Category.entries) {
            assertEquals(true, UnitConverter.unitsFor(cat).isNotEmpty())
        }
    }
}