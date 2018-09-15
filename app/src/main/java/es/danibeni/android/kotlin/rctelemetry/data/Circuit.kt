package es.danibeni.android.kotlin.rctelemetry.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "circuits")
data class Circuit @JvmOverloads constructor(
        var name: String = "",
        var city: String = "",
        var lenght: Float? = 0F,
        var threshold: Float = 0F,
        var belowThresholdNum: Int = 2,
        var aboveThresholdNum: Int = 2,
        var count: Int = 0,
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0
)