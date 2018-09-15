package es.danibeni.android.kotlin.rctelemetry.data.local

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.danibeni.android.kotlin.rctelemetry.data.Lap
import es.danibeni.android.kotlin.rctelemetry.features.newrace.LapView
import java.util.*

class LapListConverter {


        var gson = Gson()
        @TypeConverter
        fun stringToLapList(data: String?): List<Lap> {
            if (data == null) {
                return Collections.emptyList()
            }

            val listType = object : TypeToken<List<Lap>>() {}.getType()

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun lapListToString(someObjects: List<LapView>): String {
            return gson.toJson(someObjects)
        }

}