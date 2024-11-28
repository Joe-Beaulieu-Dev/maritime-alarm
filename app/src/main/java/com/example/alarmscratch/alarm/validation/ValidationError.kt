package com.example.alarmscratch.alarm.validation

import android.content.Context

sealed interface ValidationError {
    fun toSnackbarString(context: Context): String
}
