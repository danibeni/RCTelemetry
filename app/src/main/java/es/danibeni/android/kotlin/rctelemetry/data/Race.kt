package es.danibeni.android.kotlin.rctelemetry.data

import android.arch.persistence.room.*
import android.support.annotation.NonNull
import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import java.util.*

@Entity(tableName = "races")
data class Race (
        @NonNull @PrimaryKey (autoGenerate = true)
        var id: Long = 0,
        var driver: String = String.empty(),
        var vehicleName: String = String.empty(),
        var vehicleAlias: String = String.empty(),
        var vehicleAddress: String = String.empty(),
        var circuitId: Long = -1,
        var laps: String = String.empty(),
        var raceDate: Date = Date()

){

}