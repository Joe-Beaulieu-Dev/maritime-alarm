package com.example.alarmscratch.alarm.data.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.WayDarkerBoatSails

class WeeklyRepeater(encodedRepeatingDays: Int = 0) {

    enum class Day(
        val mask: Int,
        val oneLetterShorthand: String,
        val threeLetterShorthand: String
    ) {
        SUNDAY(mask = 1, oneLetterShorthand = "S", threeLetterShorthand = "Sun"),
        MONDAY(mask = 2, oneLetterShorthand = "M", threeLetterShorthand = "Mon"),
        TUESDAY(mask = 4, oneLetterShorthand = "T", threeLetterShorthand = "Tue"),
        WEDNESDAY(mask = 8, oneLetterShorthand = "W", threeLetterShorthand = "Wed"),
        THURSDAY(mask = 16, oneLetterShorthand = "T", threeLetterShorthand = "Thu"),
        FRIDAY(mask = 32, oneLetterShorthand = "F", threeLetterShorthand = "Fri"),
        SATURDAY(mask = 64, oneLetterShorthand = "S", threeLetterShorthand = "Sat");

        fun dayNumber(): Int = this.ordinal + 1
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

    /*
     * Query
     */

    fun hasRepeatingDays(): Boolean =
        repeatingDayMap.containsValue(true)

    fun isRepeatingOn(day: Day): Boolean =
        repeatingDayMap[day] ?: false

    fun isRepeatingEveryDay(): Boolean =
        repeatingDayMap.filterValues { !it }.isEmpty()

    fun getRepeatingDays(): List<Day> =
        repeatingDayMap.filterValues { it }.keys.toList()

    /*
     * Manipulate
     */

    fun withDay(day: Day): WeeklyRepeater =
        if (repeatingDayMap[day] == false) {
            WeeklyRepeater(toEncodedRepeatingDays() + day.mask)
        } else {
            WeeklyRepeater(toEncodedRepeatingDays())
        }

    fun withoutDay(day: Day): WeeklyRepeater =
        if (repeatingDayMap[day] == true) {
            WeeklyRepeater(toEncodedRepeatingDays() - day.mask)
        } else {
            WeeklyRepeater(toEncodedRepeatingDays())
        }

    /*
     * Transform
     */

    fun toEncodedRepeatingDays(): Int =
        repeatingDayMap.filterValues { it }.keys.sumOf { it.mask }

    fun toAlarmCardDateAnnotatedString(enabled: Boolean): AnnotatedString =
        buildAnnotatedString {
            // Add all Days to the AnnotatedString with different
            // styles based on whether or not they're set to repeat
            repeatingDayMap.forEach { (day, isRepeating) ->
                if (isRepeating) {
                    // Day set to repeat, make it stand out
                    withStyle(style = SpanStyle(color = if (enabled) BoatSails else WayDarkerBoatSails)) {
                        append(day.oneLetterShorthand)
                    }
                } else {
                    // Day not set to repeat, make it appear less prominent
                    withStyle(style = SpanStyle(color = LightVolcanicRock)) {
                        append(day.oneLetterShorthand)
                    }
                }
                // Conditionally add spacing for style
                if (day != Day.SATURDAY) {
                    append(" ")
                }
            }
        }

    fun toAlarmCreationDateString(): String =
        repeatingDayMap
            .filter { it.value }
            .map { it.key.threeLetterShorthand }
            .joinToString(separator = ", ")

    /*
     * Compare
     */

    override fun equals(other: Any?): Boolean =
        if (this === other) {
            true
        } else if (other as? WeeklyRepeater != null) {
            this.toEncodedRepeatingDays() == other.toEncodedRepeatingDays()
        } else {
            false
        }

    /**
     * Returns toEncodedRepeatingDays().hashCode() + 1.
     * 1 is added to the result to prevent false positives when comparing the hash code
     * of a WeeklyRepeater to the hash code of null. This false positive will occur when
     * the WeeklyRepeater has no repeating days. This is because both 0.hashCode()
     * and null.hashCode() equal 0.
     *
     * @return the hash code of this WeeklyRepeater
     */
    override fun hashCode(): Int =
        // TODO: This is good enough for now when it comes to comparing 2 WeeklyRepeaters, or WeeklyRepeater and null.
        //  However, this function breaks the hash code contract in certain situations.
        //  Ex: WeeklyRepeater.equals(1) = false, however, WeeklyRepeater(0).hashCode() and 1.hashCode() both equal 1.
        //  Come up with a unique way of calculating this.
        this.toEncodedRepeatingDays().hashCode() + 1
}
