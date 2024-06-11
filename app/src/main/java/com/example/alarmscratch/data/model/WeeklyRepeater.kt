package com.example.alarmscratch.data.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.alarmscratch.ui.theme.BoatSails
import com.example.alarmscratch.ui.theme.LightVolcanicRock
import com.example.alarmscratch.ui.theme.WayDarkerBoatSails

class WeeklyRepeater(private var encodedRepeatingDays: Int = 0) {

    enum class Day(val mask: Int, val shorthand: String) {
        SUNDAY(mask = 1, shorthand = "S"),
        MONDAY(mask = 2, shorthand = "M"),
        TUESDAY(mask = 4, shorthand = "T"),
        WEDNESDAY(mask = 8, shorthand = "W"),
        THURSDAY(mask = 16, shorthand = "T"),
        FRIDAY(mask = 32, shorthand = "F"),
        SATURDAY(mask = 64, shorthand = "S")
    }

    private val repeatingDayMap: MutableMap<Day, Boolean> =
        mutableMapOf<Day, Boolean>()
            .also { map ->
                // Add each Day to Map, and assign true/false based on
                // whether or not the Day is included in encodedDays
                Day.entries.forEach { day ->
                    map[day] = encodedRepeatingDays.and(day.mask) == day.mask
                }
            }

    fun hasRepeatingDays(): Boolean = encodedRepeatingDays != 0

    fun isRepeatingOn(day: Day): Boolean = repeatingDayMap[day] ?: false

    fun addDay(day: Day) {
        // encodedDays does not contain given day, so add it and update map
        if (encodedRepeatingDays.and(day.mask) != day.mask) {
            encodedRepeatingDays += day.mask
            repeatingDayMap[day] = true
        }
    }

    fun removeDay(day: Day) {
        // encodedDays contains given day, so remove it and update map
        if (encodedRepeatingDays.and(day.mask) == day.mask) {
            encodedRepeatingDays -= day.mask
            repeatingDayMap[day] = false
        }
    }

    fun getEncodedRepeatingDays(): Int = encodedRepeatingDays

    fun toAnnotatedDateString(enabled: Boolean): AnnotatedString =
        buildAnnotatedString {
            // Add all Days to the AnnotatedString with different
            // styles based on whether or not they're set to repeat
            repeatingDayMap.forEach { (day, isRepeating) ->
                if (isRepeating) {
                    // Day set to repeat, make it stand out
                    withStyle(style = SpanStyle(color = if (enabled) BoatSails else WayDarkerBoatSails)) {
                        append(day.shorthand)
                    }
                } else {
                    // Day not set to repeat, make it appear less prominent
                    withStyle(style = SpanStyle(color = LightVolcanicRock)) {
                        append(day.shorthand)
                    }
                }
                // Conditionally add spacing for style
                if (day != Day.SATURDAY) {
                    append(" ")
                }
            }
        }
}
