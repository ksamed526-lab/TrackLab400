package com.tracklab400.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tracklab400.app.data.local.Converters
import com.tracklab400.app.data.local.db.entity.PainReportEntity
import com.tracklab400.app.data.local.db.entity.ProfileEntity
import com.tracklab400.app.data.local.db.entity.RepRecordEntity
import com.tracklab400.app.data.local.db.entity.SessionRecordEntity
import com.tracklab400.app.data.local.db.entity.SessionStatusEntity
import com.tracklab400.app.data.local.dao.PainReportDao
import com.tracklab400.app.data.local.dao.ProfileDao
import com.tracklab400.app.data.local.dao.RepRecordDao
import com.tracklab400.app.data.local.dao.SessionRecordDao
import com.tracklab400.app.data.local.dao.SessionStatusDao

@Database(
    entities = [ProfileEntity::class, SessionStatusEntity::class, SessionRecordEntity::class, RepRecordEntity::class, PainReportEntity::class],
    version = 3,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class TrackLabDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    abstract fun sessionStatusDao(): SessionStatusDao

    abstract fun sessionRecordDao(): SessionRecordDao

    abstract fun repRecordDao(): RepRecordDao

    abstract fun painReportDao(): PainReportDao

    companion object {
        @Volatile
        private var instance: TrackLabDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS session_status (" +
                        "weekNumber INTEGER NOT NULL, " +
                        "dayIndex INTEGER NOT NULL, " +
                        "status TEXT NOT NULL, " +
                        "completedAt INTEGER, " +
                        "updatedAt INTEGER NOT NULL, " +
                        "PRIMARY KEY(weekNumber, dayIndex))",
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS session_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "weekNumber INTEGER NOT NULL, " +
                        "dayOfWeek INTEGER NOT NULL, " +
                        "sessionKind TEXT NOT NULL, " +
                        "completedAt INTEGER NOT NULL, " +
                        "status TEXT NOT NULL, " +
                        "rpe INTEGER, " +
                        "sleepHours REAL, " +
                        "energyLevel INTEGER, " +
                        "legFeeling INTEGER, " +
                        "painReported INTEGER NOT NULL, " +
                        "painLocation TEXT, " +
                        "painSeverity INTEGER, " +
                        "notes TEXT, " +
                        "totalDistanceM INTEGER NOT NULL, " +
                        "completedReps INTEGER NOT NULL, " +
                        "expectedReps INTEGER NOT NULL, " +
                        "totalTimeMs INTEGER NOT NULL, " +
                        "createdAt INTEGER NOT NULL, " +
                        "updatedAt INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS rep_record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "sessionRecordId INTEGER NOT NULL, " +
                        "repIndex INTEGER NOT NULL, " +
                        "distanceM INTEGER NOT NULL, " +
                        "targetMinMs INTEGER, " +
                        "targetMaxMs INTEGER, " +
                        "actualMs INTEGER NOT NULL, " +
                        "isManual INTEGER NOT NULL, " +
                        "recordedAt INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS pain_report (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "sessionRecordId INTEGER NOT NULL, " +
                        "location TEXT NOT NULL, " +
                        "severity INTEGER NOT NULL, " +
                        "description TEXT, " +
                        "reportedAt INTEGER NOT NULL)"
                )
            }
        }

        fun getInstance(context: Context): TrackLabDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrackLabDatabase::class.java,
                    "tracklab.db",
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { instance = it }
            }
    }
}
