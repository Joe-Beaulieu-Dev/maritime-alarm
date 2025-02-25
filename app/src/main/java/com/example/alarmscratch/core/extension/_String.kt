package com.example.alarmscratch.core.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Converts a String into an AnnotatedString which will have a highlight of [errorHighlightColor]
 * applied to any character that exceeds the given [charLimit].
 *
 * @param charLimit amount of characters to allow before applying a highlight to the remaining characters
 * @param errorHighlightColor highlight color applied to any character that exceeds the [charLimit]
 *
 * @return AnnotatedString where any character exceeding the [charLimit] has a highlight of [errorHighlightColor]
 */
fun String.charLimitExceededStyle(charLimit: Int, errorHighlightColor: Color): AnnotatedString =
    buildAnnotatedString {
        val string = this@charLimitExceededStyle

        if (string.length <= charLimit) {
            append(string)
        } else {
            // Non-highlighted substring
            append(string.substring(IntRange(0, charLimit - 1)))

            // Highlighted substring
            withStyle(style = SpanStyle(background = errorHighlightColor)) {
                append(string.substring(charLimit))
            }
        }
    }
