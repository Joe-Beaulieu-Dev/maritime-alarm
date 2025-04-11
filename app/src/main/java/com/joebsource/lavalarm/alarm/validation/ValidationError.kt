package com.joebsource.lavalarm.alarm.validation

import android.content.Context

sealed interface ValidationError {
    fun toSnackbarString(context: Context): String
}
