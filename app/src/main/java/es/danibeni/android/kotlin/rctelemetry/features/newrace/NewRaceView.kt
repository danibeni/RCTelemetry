package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator

data class NewRaceView (val id: Long, val driver: String, val vehicleName: String, val vehicleAddress: String): KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(::NewRaceView)
    }

    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeLong(id)
            writeString(driver)
            writeString(vehicleName)
            writeString(vehicleAddress)
        }
    }
}