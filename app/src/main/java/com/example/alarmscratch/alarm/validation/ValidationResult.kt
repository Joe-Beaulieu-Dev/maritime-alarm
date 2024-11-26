package com.example.alarmscratch.alarm.validation

typealias ValidationError = Error

sealed interface ValidationResult<out D, out E : ValidationError> {
    data class Success<out D, out E : ValidationError>(val data: D) : ValidationResult<D, E>
    data class Error<out D, out E : ValidationError>(val error: E) : ValidationResult<D, E>
}
