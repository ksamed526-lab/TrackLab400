package com.tracklab400.app.data.exercises

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExerciseLibraryTest {

    private val fixtureJson = """
        [
          {
            "name": "Barbell Squat",
            "force": "push",
            "level": "beginner",
            "mechanic": "compound",
            "equipment": "barbell",
            "primaryMuscles": "quadriceps",
            "secondaryMuscles": "glutes, hamstrings",
            "instructions": "Set the bar on the back of your shoulders and squat down.",
            "category": "strength",
            "image": "Barbell_Squat/0.jpg",
            "key": "barbell-squat"
          },
          {
            "name": "Trap Bar Deadlift",
            "force": "pull",
            "level": "beginner",
            "mechanic": "compound",
            "equipment": "other",
            "primaryMuscles": "quadriceps",
            "secondaryMuscles": "glutes, hamstrings",
            "instructions": "Stand inside the trap bar and lift.",
            "category": "strength",
            "image": "Trap_Bar_Deadlift/0.jpg",
            "key": "trap-bar-deadlift"
          },
          {
            "name": "Running, Treadmill",
            "force": "push",
            "level": "beginner",
            "mechanic": "compound",
            "equipment": "machine",
            "primaryMuscles": "quadriceps",
            "secondaryMuscles": "hamstrings",
            "instructions": "",
            "category": "cardio",
            "image": "Running_Treadmill/0.jpg",
            "key": "running-treadmill"
          }
        ]
    """.trimIndent()

    @Test
    fun `decode populates fields`() {
        val entries = ExerciseLibraryJson.decode(fixtureJson)
        assertEquals(3, entries.size)
        assertEquals("barbell-squat", entries[0].key)
        assertEquals("barbell", entries[0].equipment)
        assertEquals("cardio", entries[2].category)
        assertTrue(entries[2].instructions.isBlank())
    }

    @Test
    fun `decode roundtrip keeps key and image`() {
        val library = ExerciseLibraryJson.decodeToLibrary(fixtureJson)
        assertEquals(3, library.size)
        assertEquals("barbell-squat", library.byKey("barbell-squat")?.key)
        assertEquals("Barbell Squat", library.byName("barbell squat")?.name)
    }

    @Test
    fun `library sorts by key and is indexable by key and name`() {
        val library = ExerciseLibraryJson.decodeToLibrary(fixtureJson)
        assertEquals(library.all.map { it.key }, library.all.map { it.key }.sorted())
        assertNotNull(library.byKey("TRAP-BAR-DEADLIFT"))
        assertNotNull(library.byKey("barbell-squat"))
        assertNotNull(library.byName("Running, Treadmill"))
        assertEquals("Running, Treadmill", library.byName("running, treadmill")?.name)
    }

    @Test
    fun `blank key falls back to image path`() {
        val json = fixtureJson.replace("\"key\": \"barbell-squat\"", "\"key\": \"\"")
        val library = ExerciseLibraryJson.decodeToLibrary(json)
        assertEquals("barbell-squat", library.byName("barbell squat")?.key)
    }

    @Test
    fun `plan aliases resolve to existing entries`() {
        val library = ExerciseLibraryJson.decodeToLibrary(fixtureJson)
        val matches = library.find("Squat / Trap-bar deadlift")
        assertEquals(listOf("barbell-squat", "trap-bar-deadlift"), matches.map { it.key })
        assertEquals(listOf("barbell-squat"), library.find("Barbell Squat").map { it.key })
        assertTrue(library.find("Bilinmeyen hareket").isEmpty())
    }

    @Test
    fun `categories and byCategory`() {
        val library = ExerciseLibraryJson.decodeToLibrary(fixtureJson)
        assertEquals(listOf("cardio", "strength"), library.categories())
        assertEquals(2, library.byCategory("strength").size)
        assertEquals(1, library.byCategory("CARDIO").size)
    }

    @Test
    fun `real bundled asset parses and all plan expressions resolve`() {
        val moduleDir = File(System.getProperty("user.dir") ?: ".")
        val assetFile = File(moduleDir, "src/main/assets/exercise_library.json")
        assertTrue("bundled asset missing at $assetFile", assetFile.isFile)

        val library = ExerciseLibraryJson.decodeToLibrary(assetFile.readText())
        assertTrue("library should contain the curated set", library.size >= 78)

        val duplicateKeys = library.all.groupBy { it.key }.filterValues { it.size > 1 }.keys
        assertTrue("duplicate keys: $duplicateKeys", duplicateKeys.isEmpty())

        // Her plan hareketi kütüphanede en az bir eşleşmeye sahip olmalı.
        val planExpressions = listOf(
            "Squat / Trap-bar deadlift",
            "Bulgar split squat",
            "Romanian deadlift",
            "Hip thrust",
            "Front squat / Step-up",
            "Nordic hamstring",
            "Baldır yükseltme",
            "Broad jump veya box jump",
            "Pogo / ayak bileği sıçraması",
            "Kısa plank / dead bug",
        )
        planExpressions.forEach { expr ->
            val matches = library.find(expr)
            assertTrue("no library match for plan exercise: $expr", matches.isNotEmpty())
            matches.forEach { m -> assertNotNull("unresolvable key ${m.key}", library.byKey(m.key)) }
        }

        // Küratörlü 50 kuvvet hareketinin anahtarları çözülebilmeli.
        val strengthKeys = library.all.filter { it.category != "cardio" }.map { it.key }
        assertTrue("no strength entries", strengthKeys.isNotEmpty())
        strengthKeys.forEach { k -> assertNotNull("missing image asset reference for $k", library.byKey(k)) }
    }

    @Test
    fun `normalize produces camera-safe keys`() {
        assertEquals("box-jump-multiple-response", ExerciseKey.normalize("Box_Jump_Multiple_Response"))
        assertEquals("pushups", ExerciseKey.normalize("Pushups"))
        assertEquals("standing-two-arm-overhead-throw", ExerciseKey.normalize("Standing Two-Arm Overhead Throw"))
        assertEquals("running-treadmill", ExerciseKey.keyFromImage("Running_Treadmill/0.jpg"))
    }
}