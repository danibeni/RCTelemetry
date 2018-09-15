package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.os.Parcel
import es.danibeni.android.kotlin.rctelemetry.core.platform.KParcelable
import es.danibeni.android.kotlin.rctelemetry.core.platform.parcelableCreator
import es.danibeni.android.kotlin.rctelemetry.core.platform.readBoolean
import es.danibeni.android.kotlin.rctelemetry.core.platform.writeBoolean

data class LapView (val lapNumber: Int, val lapTime: Long, val lapDiff: Long, val lapVel: Float) :
        KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(
                ::LapView)
    }

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readLong(), parcel.readLong(), parcel.readFloat())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeInt(lapNumber)
            writeLong(lapTime)
            writeLong(lapDiff)
            writeFloat(lapVel)
        }
    }
}