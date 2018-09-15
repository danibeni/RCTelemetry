package es.danibeni.android.kotlin.rctelemetry.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import es.danibeni.android.kotlin.rctelemetry.core.platform.Converters
import es.danibeni.android.kotlin.rctelemetry.data.Circuit

import es.danibeni.android.kotlin.rctelemetry.data.Race
import es.danibeni.android.kotlin.rctelemetry.data.Vehicle


/**
 * The Room Database that contains the Races table.
 */
@Database(entities = arrayOf(Race::class, Circuit::class), version = 1)
@TypeConverters(Converters::class)
abstract class RCTelemetryDatabase : RoomDatabase() {

    abstract fun racesDao(): RacesDao
    abstract fun circuitsDao(): CircuitsDao

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