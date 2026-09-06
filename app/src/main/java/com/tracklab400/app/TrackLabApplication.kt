package com.tracklab400.app

import android.app.Application
import com.tracklab400.app.data.notifications.NotificationChannels
import com.tracklab400.app.data.notifications.NotificationScheduler
import com.tracklab400.app.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TrackLabApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationChannels.create(this)
        startNotificationRescheduling()
    }

    private fun startNotificationRescheduling() {
        val scheduler = NotificationScheduler(this)
        applicationScope.launch {
            combine(
                container.profileRepository.profile,
                container.planRepository.plan,
                container.preferences.notificationSettings,
            ) { profile, plan, settings ->
                NotificationScheduleInput(profile?.startDateMillis, plan, settings)
            }
                .distinctUntilChanged()
                .collect { input ->
                    scheduler.reschedule(
                        settings = input.settings,
                        plan = input.plan,
                        startDateMillis = input.startDateMillis,
                    )
                }
        }
    }
}

private data class NotificationScheduleInput(
    val startDateMillis: Long?,
    val plan: com.tracklab400.app.data.model.TrainingPlan?,
    val settings: com.tracklab400.app.data.model.NotificationSettings,
)
