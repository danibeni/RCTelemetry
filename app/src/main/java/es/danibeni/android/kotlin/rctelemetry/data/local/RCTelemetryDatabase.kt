package es.danibeni.android.kotlin.rctelemetry.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import es.danibeni.android.kotlin.rctelemetry.core.platform.DateConverter

import es.danibeni.android.kotlin.rctelemetry.data.Race


/**
 * The Room Database that contains the Races table.
 */
@Database(entities = arrayOf(Race::class), version = 1)
@TypeConverters(DateConverter::class)
abstract class RCTelemetryDatabase : RoomDatabase() {

    abstract fun racesDao(): RacesDao

    companion object {

        private var INSTANCE: RCTelemetryDatabase? = null

        private val sLock = Any()

        fun getInstance(context: Context): RCTelemetryDatabase {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            RCTelemetryDatabase::class.java, "RCTelemetry.db")
                            .build()
                }
                return INSTANCE!!
            }
        }
    }

}