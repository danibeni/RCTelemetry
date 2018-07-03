package es.danibeni.android.kotlin.rctelemetry.features.races

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator

data class RaceView (val id: String, val circuit: String) :
        KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(
                ::RaceView)
    }

    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeString(id)
            writeString(circuit)
        }
    }
}