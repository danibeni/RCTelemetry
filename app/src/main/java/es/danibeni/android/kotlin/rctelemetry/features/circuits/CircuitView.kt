package es.danibeni.android.kotlin.rctelemetry.features.circuits

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator

data class CircuitView (val id: Long, val name: String, val city: String, val lenght: Float) :
        KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(
                ::CircuitView)
    }

    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readFloat())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeLong(id)
            writeString(name)
            writeString(city)
            writeFloat(lenght)
        }
    }
}