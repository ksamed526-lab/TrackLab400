package com.tracklab400.app.di

import android.content.Context
import com.tracklab400.app.data.local.db.TrackLabDatabase
import com.tracklab400.app.data.prefs.UserPreferencesManager
import com.tracklab400.app.data.repository.PlanRepository
import com.tracklab400.app.data.repository.ProfileRepository
import com.tracklab400.app.data.repository.RecordRepository

class AppContainer(context: Context) {

    private val database = TrackLabDatabase.getInstance(context)

    val preferences = UserPreferencesManager(context)

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(
            profileDao = database.profileDao(),
            prefs = preferences,
        )
    }

    val planRepository: PlanRepository by lazy {
        PlanRepository(
            profileDao = database.profileDao(),
            sessionStatusDao = database.sessionStatusDao(),
        )
    }

    val recordRepository: RecordRepository by lazy {
        RecordRepository(
            sessionRecordDao = database.sessionRecordDao(),
            repRecordDao = database.repRecordDao(),
            painReportDao = database.painReportDao(),
        )
    }
}
