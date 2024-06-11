package com.example.alarmscratch.extension

private val ordinalNumberMap = mapOf(
    '0' to "th",
    '1' to "st",
    '2' to "nd",
    '3' to "rd",
    '4' to "th",
    '5' to "th",
    '6' to "th",
    '7' to "th",
    '8' to "th",
    '9' to "th"
)

fun Int.toOrdinal(): String {
    // can never be too careful
    val lastDigitChar = toString().lastOrNull()

    return if (lastDigitChar != null) {
        "$this${ordinalNumberMap.getOrDefault(key = lastDigitChar, defaultValue = "")}"
    } else {
        ""
    }
}
