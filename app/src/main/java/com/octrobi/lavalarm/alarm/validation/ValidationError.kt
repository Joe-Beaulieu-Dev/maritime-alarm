package com.octrobi.lavalarm.alarm.validation

import android.content.Context

sealed interface ValidationError {
    fun toSnackbarString(context: Context): String
}
