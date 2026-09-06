package com.tracklab400.app.data.local

import androidx.room.TypeConverter
import com.tracklab400.app.data.model.Equipment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun equipmentToJson(value: Set<Equipment>): String = json.encodeToString(value.toList())

    @TypeConverter
    fun jsonToEquipment(value: String): Set<Equipment> =
        json.decodeFromString<List<Equipment>>(value).toSet()
}
