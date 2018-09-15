package es.danibeni.android.kotlin.rctelemetry.data

import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import java.util.*

data class RaceAndCircuit constructor(
        var id: Long = 0,
        var driver: String = String.empty(),
        var vehicleName: String = String.empty(),
        var vehicleAlias: String = String.empty(),
        var vehicleAddress: String = String.empty(),
        var raceDate: Date,
        var laps: String = String.empty(),
        var lapList: List<Lap>? = listOf(),
        var circuitId: Long = 0,
        var circuitName: String = String.empty(),
        var circuitLenght: Float? = 0F
)

