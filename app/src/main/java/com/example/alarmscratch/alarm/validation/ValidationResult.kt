package com.example.alarmscratch.alarm.validation

sealed interface ValidationResult<E : ValidationError> {
    class Success<E : ValidationError> : ValidationResult<E>
    data class Error<E : ValidationError>(val error: E) : ValidationResult<E>
}
