package com.example.alarmscratch.alarm.validation

import android.content.Context
import com.example.alarmscratch.R

class AlarmValidator {

    companion object {
        // Regex Patterns
        private val ILLEGAL_CHARACTER_PATTERN = Regex("[^A-Za-z0-9 ]")
    }

    enum class AlarmValidationError: ValidationError {
        ILLEGAL_CHARACTER,
        ONLY_WHITESPACE;

        fun toErrorString(context: Context): String =
            when (this) {
                ILLEGAL_CHARACTER ->
                    context.getString(R.string.alarm_name_illegal_character_err)
                ONLY_WHITESPACE ->
                    context.getString(R.string.alarm_name_only_whitespace_err)
            }
    }

    fun validateName(name: String): ValidationResult<AlarmValidationError> =
        if (name.contains(ILLEGAL_CHARACTER_PATTERN)) {
            ValidationResult.Error(AlarmValidationError.ILLEGAL_CHARACTER)
        } else if (name.isNotEmpty() && name.trim().isEmpty()) {
            ValidationResult.Error(AlarmValidationError.ONLY_WHITESPACE)
        } else {
            ValidationResult.Success()
        }
}
