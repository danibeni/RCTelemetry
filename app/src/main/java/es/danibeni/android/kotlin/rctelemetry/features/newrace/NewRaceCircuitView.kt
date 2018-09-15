package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator

data class NewRaceCircuitView (val id: Long, val name: String, val city: String, val lenght: Float, val threshold: Float, val belowThresholdNum: Int, val aboveThresholdNum: Int, val count: Int) :
        KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(
                ::NewRaceCircuitView)
    }

    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readFloat(), parcel.readFloat(), parcel.readInt(), parcel.readInt(), parcel.readInt())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeLong(id)
            writeString(name)
            writeString(city)
            writeFloat(lenght)
            writeFloat(threshold)
            writeInt(belowThresholdNum)
            writeInt(aboveThresholdNum)
            writeInt(count)
        }
    }
}