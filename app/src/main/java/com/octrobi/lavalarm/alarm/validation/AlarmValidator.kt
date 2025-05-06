package com.octrobi.lavalarm.alarm.validation

import android.content.Context
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import java.time.LocalDateTime

class AlarmValidator {

    companion object {
        // Name validation
        private val ILLEGAL_CHARACTER_PATTERN = Regex("[^A-Za-z0-9 ]")
        const val NAME_CHARACTER_LIMIT = 30
    }

    enum class NameError: ValidationError {
        ILLEGAL_CHARACTER,
        ONLY_WHITESPACE,
        CHARACTER_LIMIT;

        override fun toSnackbarString(context: Context): String =
            when (this) {
                ILLEGAL_CHARACTER, ONLY_WHITESPACE ->
                    context.getString(R.string.alarm_name_err_snackbar)
                CHARACTER_LIMIT ->
                    context.getString(R.string.alarm_name_too_long_err_snackbar)
            }

        fun toInlineString(context: Context): String =
            when (this) {
                ILLEGAL_CHARACTER ->
                    context.getString(R.string.alarm_name_illegal_character_err_inline)
                ONLY_WHITESPACE ->
                    context.getString(R.string.alarm_name_only_whitespace_err_inline)
                CHARACTER_LIMIT ->
                    // Character limit has no inline string
                    ""
            }
    }

    enum class DateTimeError: ValidationError {
        NOT_SET_IN_FUTURE;

        override fun toSnackbarString(context: Context): String =
            when (this) {
                NOT_SET_IN_FUTURE ->
                    context.getString(R.string.alarm_datetime_in_past_err_snackbar)
            }
    }

    fun validateNameContent(name: String): ValidationResult<NameError> =
        if (name.contains(ILLEGAL_CHARACTER_PATTERN)) {
            ValidationResult.Error(NameError.ILLEGAL_CHARACTER)
        } else if (name.isNotEmpty() && name.trim().isEmpty()) {
            ValidationResult.Error(NameError.ONLY_WHITESPACE)
        } else {
            ValidationResult.Success()
        }

    fun validateNameLength(name: String): ValidationResult<NameError> =
        if (name.length > NAME_CHARACTER_LIMIT) {
            ValidationResult.Error(NameError.CHARACTER_LIMIT)
        } else {
            ValidationResult.Success()
        }

    fun validateDateTime(dateTime: LocalDateTime): ValidationResult<DateTimeError> =
        if (!dateTime.isAfter(LocalDateTimeUtil.nowTruncated())) {
            ValidationResult.Error(DateTimeError.NOT_SET_IN_FUTURE)
        } else {
            ValidationResult.Success()
        }
}
