package com.tracklab400.app.data.local

import com.tracklab400.app.data.model.Equipment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun equipmentSetRoundTripsThroughJson() {
        val original = setOf(Equipment.BARBELL, Equipment.BANT, Equipment.DUMBBELL)
        val json = converters.equipmentToJson(original)
        val decoded = converters.jsonToEquipment(json)
        assertEquals(original, decoded)
    }

    @Test
    fun emptyEquipmentSetRoundTrips() {
        val json = converters.equipmentToJson(emptySet())
        assertTrue(converters.jsonToEquipment(json).isEmpty())
    }

    @Test
    fun singleEquipmentRoundTrips() {
        val json = converters.equipmentToJson(setOf(Equipment.TRAP_BAR))
        assertEquals(setOf(Equipment.TRAP_BAR), converters.jsonToEquipment(json))
    }
}
