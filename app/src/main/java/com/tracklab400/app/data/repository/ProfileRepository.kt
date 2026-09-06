package com.tracklab400.app.data.repository

import com.tracklab400.app.data.local.dao.ProfileDao
import com.tracklab400.app.data.local.db.entity.toDomain
import com.tracklab400.app.data.local.db.entity.toEntity
import com.tracklab400.app.data.model.DemoProfile
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.prefs.UserPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val profileDao: ProfileDao,
    private val prefs: UserPreferencesManager,
) {

    val profile: Flow<Profile?> = profileDao.observeProfile().map { it?.toDomain() }

    val onboardingCompleted: Flow<Boolean?> = prefs.onboardingCompleted

    val isDemoMode: Flow<Boolean> = prefs.isDemoMode

    val notificationsEnabled: Flow<Boolean> = prefs.notificationsEnabled

    suspend fun saveProfile(profile: Profile) {
        val existing = profileDao.getProfile()
        val createdAt = existing?.createdAt ?: profile.createdAt
        profileDao.upsertProfile(profile.toEntity(createdAt = createdAt))
    }

    suspend fun applyDemoProfile() {
        val now = System.currentTimeMillis()
        val existing = profileDao.getProfile()
        val createdAt = existing?.createdAt ?: now
        profileDao.upsertProfile(DemoProfile.build(now).toEntity(createdAt = createdAt))
        prefs.setOnboardingCompleted(true)
        prefs.setDemoMode(true)
    }

    suspend fun clearProfile() {
        profileDao.deleteAll()
        prefs.setOnboardingCompleted(false)
        prefs.setDemoMode(false)
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        prefs.setNotificationsEnabled(enabled)
    }
}
