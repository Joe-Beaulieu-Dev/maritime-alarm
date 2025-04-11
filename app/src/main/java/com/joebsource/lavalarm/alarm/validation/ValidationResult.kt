package com.joebsource.lavalarm.alarm.validation

sealed interface ValidationResult<out E : ValidationError> {
    class Success<out E : ValidationError> : ValidationResult<E>
    data class Error<out E : ValidationError>(val error: E) : ValidationResult<E>
}
