package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator

data class VehicleView (val name: String, val addressId: String, var status: String): KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(::VehicleView)
    }

    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeString(name)
            writeString(addressId)
            writeString(status)
        }
    }
}