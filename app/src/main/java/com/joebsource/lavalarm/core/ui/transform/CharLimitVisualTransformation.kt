package com.joebsource.lavalarm.core.ui.transform

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

/**
 * Applies a highlight of [errorHighlightColor] to any character in the AnnotatedString
 * that exceeds the given [charLimit]. Since this class's filter function does not modify
 * character count, a TransformedText with OffsetMapping.Identity is returned.
 *
 * @param charLimit amount of characters to allow before applying a highlight to the remaining characters
 * @param errorHighlightColor highlight color applied to any character that exceeds the [charLimit]
 */
class CharLimitVisualTransformation(
    private val charLimit: Int,
    private val errorHighlightColor: Color
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val plainString = text.text

        // Add highlight to characters that exceed the character limit
        val transformedString = buildAnnotatedString {
            if (plainString.length <= charLimit) {
                append(plainString)
            } else {
                // Non-highlighted substring
                append(plainString.substring(IntRange(0, charLimit - 1)))

                // Highlighted substring
                withStyle(style = SpanStyle(background = errorHighlightColor)) {
                    append(plainString.substring(charLimit))
                }
            }
        }

        // Return with OffsetMapping.Identity since this
        // VisualTransformation does not add or remove characters
        return TransformedText(transformedString, OffsetMapping.Identity)
    }
}
