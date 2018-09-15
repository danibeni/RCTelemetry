package es.danibeni.android.kotlin.rctelemetry.core.platform

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.danibeni.android.kotlin.rctelemetry.data.Lap
import java.util.*

object Converters {

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToLaps(value: String): List<Lap>? {
        val listType = object : TypeToken<List<Lap>>() {

        }.type
        return Gson().fromJson<List<Lap>>(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromLapsToString(list: List<Lap>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}