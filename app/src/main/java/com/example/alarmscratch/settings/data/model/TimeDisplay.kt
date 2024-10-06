package com.example.alarmscratch.settings.data.model

enum class TimeDisplay(val value: String) {
    TwelveHour("12hr"),
    TwentyFourHour("24hr");

    companion object {

        fun fromString(timeDisplay: String?): TimeDisplay? =
            try {
                if (timeDisplay == null) {
                    null
                } else {
                    TimeDisplay.valueOf(timeDisplay)
                }
            } catch (e: Exception) {
                null
            }
    }
}
