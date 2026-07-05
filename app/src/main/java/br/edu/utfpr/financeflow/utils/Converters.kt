package br.edu.utfpr.financeflow.utils

import androidx.room.TypeConverter
import br.edu.utfpr.financeflow.model.EntryType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromEntryType(value: EntryType) = value.name

    @TypeConverter
    fun toEntryType(value: String) = EntryType.valueOf(value)
}