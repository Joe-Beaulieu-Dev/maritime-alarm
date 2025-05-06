package com.octrobi.lavalarm.core.extension

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object LocalDateUtil {
    fun fromUtcMillis(utcTimeMillis: Long): LocalDate =
        Instant.ofEpochMilli(utcTimeMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
}

/*
 * Utility
 */

fun LocalDate.toUtcMillis(): Long =
    atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
