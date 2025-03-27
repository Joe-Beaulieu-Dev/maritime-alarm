package com.example.alarmscratch.alarm.validation

import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class AlarmValidatorTest {

    // Validator
    private val alarmValidator = AlarmValidator()

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
    fun validateNameLength_ReturnsNameCharacterLimitError_WhenCharacterLimitExceeded() {
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
}
