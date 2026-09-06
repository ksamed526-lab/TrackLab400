package com.tracklab400.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TrackAccess { PIST_VAR, PIST_YOK }

@Serializable
enum class GymStatus { SALON_VAR, SALON_YOK }

@Serializable
enum class TrainingExperience { BASLANGIC, ORTA, ILERI }

@Serializable
enum class StopwatchType { EL_KRONOMETRESI, ELEKTRONIK }

@Serializable
enum class Equipment { YOK, VUCUT_AGIRLIGI, DUMBBELL, BARBELL, TRAP_BAR, BANT }
