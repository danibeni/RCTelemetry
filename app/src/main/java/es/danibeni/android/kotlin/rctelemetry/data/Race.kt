package es.danibeni.android.kotlin.rctelemetry.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import java.util.*

@Entity(tableName = "races")
data class Race @JvmOverloads constructor(
        @ColumnInfo(name = "circuit") var circuit: String = "",
        @ColumnInfo(name = "driver") var driver: String = "",
        @ColumnInfo(name = "vehicle") var vehicle: String = "",
        @PrimaryKey @ColumnInfo(name = "raceid") var id: String = UUID.randomUUID().toString()
) {

}