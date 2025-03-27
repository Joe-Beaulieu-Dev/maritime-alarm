package com.example.alarmscratch.alarm.validation

import android.content.Context
import com.example.alarmscratch.R
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class AlarmValidatorTest {

    // Validator
    private val alarmValidator = AlarmValidator()

    /*
     ******************
     * AlarmValidator *
     ******************
     */

    /*
     * validateNameContent
     */

    @Test
    fun validateNameContent_ReturnsIllegalCharacterError_WhenNameContainsIllegalCharacter() {
        val validationResult = alarmValidator.validateNameContent("@")

        assertTrue(validationResult is ValidationResult.Error)
        assertEquals(
            AlarmValidator.NameError.ILLEGAL_CHARACTER,
            (validationResult as ValidationResult.Error).error
        )
    }

    @Test
    fun validateNameContent_ReturnsOnlyWhitespaceError_WhenNameIsOnlyWhitespace() {
        val validationResult = alarmValidator.validateNameContent(" ")

        assertTrue(validationResult is ValidationResult.Error)
        assertEquals(
            AlarmValidator.NameError.ONLY_WHITESPACE,
            (validationResult as ValidationResult.Error).error
        )
    }

    @Test
    fun validateNameContent_ReturnsSuccess_WhenNameIsValid() {
        val validationResult = alarmValidator.validateNameContent("alarm name")
        assertTrue(validationResult is ValidationResult.Success)
    }

    /*
     * validateNameLength
     */

    @Test
    fun validateNameLength_ReturnsCharacterLimitError_WhenCharacterLimitExceeded() {
        val longName = StringBuilder().apply {
            for (i in 1..AlarmValidator.NAME_CHARACTER_LIMIT + 1) {
                append("j")
            }
        }.toString()

        val validationResult = alarmValidator.validateNameLength(longName)

        assertTrue(validationResult is ValidationResult.Error)
        assertEquals(
            AlarmValidator.NameError.CHARACTER_LIMIT,
            (validationResult as ValidationResult.Error).error
        )
    }

    @Test
    fun validateNameLength_ReturnsSuccess_WhenCharacterLimitNotExceeded() {
        val name = StringBuilder().apply {
            for (i in 1..AlarmValidator.NAME_CHARACTER_LIMIT) {
                append("j")
            }
        }.toString()

        val validationResult = alarmValidator.validateNameLength(name)

        assertTrue(validationResult is ValidationResult.Success)
    }

    /*
     * validateDateTime
     */

    @Test
    fun validateDateTime_ReturnsNotSetInFutureError_WhenDateTimeIsInPast() {
        val dateTime = LocalDateTime.now()

        var validationResult: ValidationResult<ValidationError>? = null
        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.plusHours(1)
            validationResult = alarmValidator.validateDateTime(dateTime)
        }

        assertTrue(validationResult is ValidationResult.Error)
        assertEquals(
            AlarmValidator.DateTimeError.NOT_SET_IN_FUTURE,
            (validationResult as ValidationResult.Error).error
        )
    }

    @Test
    fun validateDateTime_ReturnsNotSetInFutureError_WhenDateTimeIsNow() {
        val dateTime = LocalDateTime.now()

        var validationResult: ValidationResult<ValidationError>? = null
        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime
            validationResult = alarmValidator.validateDateTime(dateTime)
        }

        assertTrue(validationResult is ValidationResult.Error)
        assertEquals(
            AlarmValidator.DateTimeError.NOT_SET_IN_FUTURE,
            (validationResult as ValidationResult.Error).error
        )
    }

    @Test
    fun validateDateTime_ReturnsSuccess_WhenDateTimeIsInFuture() {
        val dateTime = LocalDateTime.now()

        var validationResult: ValidationResult<ValidationError>? = null
        mockkObject(LocalDateTimeUtil) {
            every { LocalDateTimeUtil.nowTruncated() } returns dateTime.minusHours(1)
            validationResult = alarmValidator.validateDateTime(dateTime)
        }

        assertTrue(validationResult is ValidationResult.Success)
    }

    /*
     *************
     * NameError *
     *************
     */

    /*
     * toSnackbarString
     */

    @Test
    fun toSnackbarString_NameError_ReturnsProperString_ForIllegalCharacterError() {
        val error = AlarmValidator.NameError.ILLEGAL_CHARACTER
        val expectedString = "illegal character"
        val context = mockk<Context> {
            every { getString(R.string.alarm_name_err_snackbar) } returns expectedString
        }

        assertEquals(expectedString, error.toSnackbarString(context))
    }

    @Test
    fun toSnackbarString_NameError_ReturnsProperString_ForOnlyWhitespaceError() {
        val error = AlarmValidator.NameError.ONLY_WHITESPACE
        val expectedString = "only whitespace"
        val context = mockk<Context> {
            every { getString(R.string.alarm_name_err_snackbar) } returns expectedString
        }

        assertEquals(expectedString, error.toSnackbarString(context))
    }

    @Test
    fun toSnackbarString_NameError_ReturnsProperString_ForCharacterLimitError() {
        val error = AlarmValidator.NameError.CHARACTER_LIMIT
        val expectedString = "character limit"
        val context = mockk<Context> {
            every { getString(R.string.alarm_name_too_long_err_snackbar) } returns expectedString
        }

        assertEquals(expectedString, error.toSnackbarString(context))
    }

    /*
     * toInlineString
     */

    @Test
    fun toInlineString_NameError_ReturnsProperString_ForIllegalCharacterError() {
        val error = AlarmValidator.NameError.ILLEGAL_CHARACTER
        val expectedString = "illegal character"
        val context = mockk<Context> {
            every { getString(R.string.alarm_name_illegal_character_err_inline) } returns expectedString
        }

        assertEquals(expectedString, error.toInlineString(context))
    }

    @Test
    fun toInlineString_NameError_ReturnsProperString_ForOnlyWhitespaceError() {
        val error = AlarmValidator.NameError.ONLY_WHITESPACE
        val expectedString = "only whitespace"
        val context = mockk<Context> {
            every { getString(R.string.alarm_name_only_whitespace_err_inline) } returns expectedString
        }

        assertEquals(expectedString, error.toInlineString(context))
    }

    @Test
    fun toInlineString_NameError_ReturnsEmptyString_ForCharacterLimitError() {
        val error = AlarmValidator.NameError.CHARACTER_LIMIT
        val expectedString = ""

        assertEquals(expectedString, error.toInlineString(mockk<Context>()))
    }

    /*
     *****************
     * DateTimeError *
     *****************
     */

    /*
     * toSnackbarString
     */

    @Test
    fun toSnackbarString_DateTimeError_ReturnsProperString_ForNotSetInFutureError() {
        val error = AlarmValidator.DateTimeError.NOT_SET_IN_FUTURE
        val expectedString = "not set in future"
        val context = mockk<Context> {
            every { getString(R.string.alarm_datetime_in_past_err_snackbar) } returns expectedString
        }

        assertEquals(expectedString, error.toSnackbarString(context))
    }
}
