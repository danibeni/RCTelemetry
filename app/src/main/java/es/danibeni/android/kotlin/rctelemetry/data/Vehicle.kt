package es.danibeni.android.kotlin.rctelemetry.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName="vehicles")
data class Vehicle @JvmOverloads constructor(
        @ColumnInfo(name = "vehiclename") var name: String = "",
        @ColumnInfo(name = "vehicleaddressid") var addressId: String = "",
        @ColumnInfo(name = "vehiclemac") var mac: String = "",
        @ColumnInfo(name = "vehiclestatus") var status: String = "",
        @PrimaryKey @ColumnInfo(name = "vehicleid") var id: String = UUID.randomUUID().toString()
)