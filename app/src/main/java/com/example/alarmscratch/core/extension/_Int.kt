package com.example.alarmscratch.core.extension

private const val DEFAULT_ORDINAL = ""

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
    val intString = toString()
    val lastDigitChar = intString.lastOrNull()

    return if (lastDigitChar != null) {
        // If the Int has more than 1 digit, and the second to last digit is a 1, then always append "th"
        if (intString.length > 1 && intString[intString.lastIndex - 1] == '1') {
            "${this}th"
        } else {
            "$this${ordinalNumberMap.getOrDefault(key = lastDigitChar, defaultValue = DEFAULT_ORDINAL)}"
        }
    } else {
        DEFAULT_ORDINAL
    }
}
