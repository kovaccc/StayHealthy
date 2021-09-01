package com.example.stayhealthy.util

import java.text.SimpleDateFormat
import java.util.*

object TimeHelper {

    fun getTimeOfCurrentDay(hour: Int, minute: Int, second: Int, millisecond: Int): Long {
        val calendar = GregorianCalendar()
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hour)
        calendar.set(GregorianCalendar.MINUTE, minute)
        calendar.set(GregorianCalendar.SECOND, second)
        calendar.set(GregorianCalendar.MILLISECOND, millisecond)
        return calendar.timeInMillis

    }

    fun getStartTimeOfDate(date: Long): Long {

        val calendar = GregorianCalendar()

        calendar.timeInMillis = date
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0)
        calendar.set(GregorianCalendar.MINUTE, 0)
        calendar.set(GregorianCalendar.SECOND, 0)
        calendar.set(GregorianCalendar.MILLISECOND, 0)
        return calendar.timeInMillis

    }


    fun getEndTimeOfDate(date: Long): Long {

        val calendar = GregorianCalendar()

        calendar.timeInMillis = date
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 23)
        calendar.set(GregorianCalendar.MINUTE, 59)
        calendar.set(GregorianCalendar.SECOND, 59)
        calendar.set(
            GregorianCalendar.MILLISECOND,
            59
        ) // needed to put milliseconds because storing objects in milliseconds

        return calendar.timeInMillis


    }

    fun getTimeOfCurrentDayFormatted(hour: Int, minute: Int, second: Int, millisecond: Int, format: String? = null): String {
        val dateFormat = format ?: "EEEE, dd/MM/yy, hh:mm:ss"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        return sdf.format(getTimeOfCurrentDay(hour, minute, second, millisecond))
    }

}