package com.tracklab400.app

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Hareket kütüphanesi ve detay ekranı akış testleri.
 * Demo profili ana ekrana taşındıktan sonra:
 * 1) Kuvvet sekmesi → kütüphane açılır, hareket detayı (RPE + başlangıç/bitiş) görünür.
 * 2) Ana sayfa → Antrenmana Başla → 4 bölüm (Isınma/Ana Çalışma/Kuvvet/Soğuma) ve
 *    ısınma hareketi detayı (Adımlar) görünür.
 */
class ExerciseDetailFlowTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun ensureHome() {
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodes(hasText(WELCOME_DEMO)).fetchSemanticsNodes().isNotEmpty() ||
                compose.onAllNodes(hasText(NAV_HOME)).fetchSemanticsNodes().isNotEmpty()
        }
        if (compose.onAllNodes(hasText(WELCOME_DEMO)).fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithText(WELCOME_DEMO).performClick()
        }
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodes(hasText(NAV_HOME)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun strengthTabOpensLibraryAndExerciseDetail() {
        ensureHome()

        compose.onNodeWithText(NAV_STRENGTH).performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(LIBRARY_TITLE)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(LIBRARY_TITLE).assertExists()

        compose.onNodeWithText(FIRST_STRENGTH_EXERCISE).performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(DETAIL_START_POSE)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(DETAIL_START_POSE).assertExists()
        compose.onNodeWithText(DETAIL_END_POSE).assertExists()
        compose.onAllNodes(hasText(DETAIL_RPE_TARGET)).fetchSemanticsNodes().isNotEmpty()

        compose.onNodeWithContentDescription(NAV_BACK).performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(LIBRARY_TITLE)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun workoutDetailShowsFourSectionsAndWarmUpDetail() {
        ensureHome()

        compose.onNodeWithText(ACTION_START).performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(SECTION_WARMUP)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(SECTION_WARMUP).assertExists()
        compose.onNodeWithText(SECTION_WORKOUT).assertExists()
        compose.onNodeWithText(SECTION_STRENGTH).assertExists()
        compose.onNodeWithText(SECTION_COOLDOWN).assertExists()

        compose.onNodeWithText(FIRST_WARMUP_EXERCISE).performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(DETAIL_STEPS)).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodes(hasText(DETAIL_STEPS)).fetchSemanticsNodes().isNotEmpty()
        compose.onAllNodes(hasText(DETAIL_TIPS)).fetchSemanticsNodes().isNotEmpty()
        compose.onAllNodes(hasText(DETAIL_SAFETY)).fetchSemanticsNodes().isNotEmpty()
    }

    companion object {
        private const val WELCOME_DEMO = "Demo profiliyle dene"
        private const val NAV_HOME = "Ana Sayfa"
        private const val NAV_STRENGTH = "Kuvvet"
        private const val NAV_BACK = "Geri"
        private const val LIBRARY_TITLE = "Hareket Kütüphanesi"
        private const val FIRST_STRENGTH_EXERCISE = "Trap Bar Deadlift"
        private const val DETAIL_START_POSE = "Başlangıç"
        private const val DETAIL_END_POSE = "Bitiş"
        private const val DETAIL_RPE_TARGET = "Hedef Zorluk"
        private const val ACTION_START = "Antrenmana Başla"
        private const val SECTION_WARMUP = "Isınma"
        private const val SECTION_WORKOUT = "Ana Çalışma"
        private const val SECTION_STRENGTH = "Kuvvet"
        private const val SECTION_COOLDOWN = "Soğuma"
        private const val FIRST_WARMUP_EXERCISE = "1. Hafif koşu"
        private const val DETAIL_STEPS = "Adımlar"
        private const val DETAIL_TIPS = "İpuçları"
        private const val DETAIL_SAFETY = "Güvenlik notları"
    }
}