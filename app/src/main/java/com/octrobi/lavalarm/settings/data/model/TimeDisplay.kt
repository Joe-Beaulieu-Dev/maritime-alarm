package com.octrobi.lavalarm.settings.data.model

enum class TimeDisplay(val value: String) {
    TwelveHour("12hr"),
    TwentyFourHour("24hr");

    companion object {

        fun fromString(timeDisplay: String?): TimeDisplay? =
            TimeDisplay.entries.firstOrNull { it.value == timeDisplay }
    }
}
