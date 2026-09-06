package com.tracklab400.app

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

/**
 * Smoke testleri — uygulamanın açılışı ve ana akış.
 *
 * Uygulama iki farklı durumda açılır: temiz kurulumda karşılama (onboarding)
 * ekranı, profili kurulmuşsa doğrudan ana ekran. Testler her iki durumu da
 * tolere edecek ve "Demo profiliyle dene" akışını ana ekrana taşıyacak şekilde
 * yazılmıştır.
 */
class MainActivitySmokeTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun waitForSettled() {
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodes(hasText(WELCOME_DEMO)).fetchSemanticsNodes().isNotEmpty() ||
                compose.onAllNodes(hasText(NAV_HOME)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun ensureHome() {
        waitForSettled()
        if (compose.onAllNodes(hasText(WELCOME_DEMO)).fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithText(WELCOME_DEMO).performClick()
        }
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodes(hasText(NAV_HOME)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun appLaunchesToWelcomeOrHome() {
        waitForSettled()
        if (compose.onAllNodes(hasText(WELCOME_DEMO)).fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithText(WELCOME_TITLE).assertExists()
            compose.onNodeWithText(WELCOME_CREATE_PROFILE).assertExists()
        } else {
            compose.onNodeWithText(HOME_SECTION_THIS_WEEK).assertExists()
            compose.onNodeWithText(NAV_PROGRESS).assertExists()
        }
    }

    @Test
    fun homeShowsBottomNavigationAndContent() {
        ensureHome()
        compose.onNodeWithText(NAV_HOME).assertExists()
        compose.onNodeWithText(NAV_PLAN).assertExists()
        compose.onNodeWithText(NAV_PROGRESS).assertExists()
        compose.onNodeWithText(NAV_STRENGTH).assertExists()
        compose.onNodeWithText(HOME_SECTION_THIS_WEEK).assertExists()
    }

    @Test
    fun homeNavigatesToPlanViaBottomNav() {
        ensureHome()
        compose.onNodeWithText(NAV_PLAN).performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText("Hafta 1")).fetchSemanticsNodes().isNotEmpty() &&
                compose.onAllNodes(hasText(NAV_PLAN)).fetchSemanticsNodes().size >= 2
        }
    }

    companion object {
        private const val WELCOME_TITLE = "400 metreyi bilimle çalış"
        private const val WELCOME_CREATE_PROFILE = "Kendi profilimi oluştur"
        private const val WELCOME_DEMO = "Demo profiliyle dene"
        private const val NAV_HOME = "Ana Sayfa"
        private const val NAV_PLAN = "Plan"
        private const val NAV_PROGRESS = "İlerleme"
        private const val NAV_STRENGTH = "Kuvvet"
        private const val HOME_SECTION_THIS_WEEK = "Bu Hafta"
    }
}