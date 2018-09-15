package es.danibeni.android.kotlin.rctelemetry.features.races

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator

data class RaceView (val id: Long, val circuit: String, val driver: String, val raceTime: String, val raceDate: String) :
        KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(
                ::RaceView)
    }

    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeLong(id)
            writeString(circuit)
            writeString(driver)
            writeString(raceDate)
            writeString(raceTime)
        }
    }
}