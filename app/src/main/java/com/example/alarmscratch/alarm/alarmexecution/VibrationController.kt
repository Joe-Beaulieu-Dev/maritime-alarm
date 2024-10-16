package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi

object VibrationController {

    private val VIBRATION_PATTERN = longArrayOf(0, 500, 500)
    private const val VIBRATION_REPETITION_INDEX = 1

    fun startVibration(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrateTiramisu(context)
        } else {
            vibratePreTiramisu(context)
        }
    }

    fun stopVibration(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getVibratorS(context).cancel()
        } else {
            getVibratorPreS(context).cancel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun vibrateTiramisu(context: Context) {
        getVibratorS(context).vibrate(
            VibrationEffect.createWaveform(VIBRATION_PATTERN, VIBRATION_REPETITION_INDEX),
            VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM)
        )
    }

    private fun vibratePreTiramisu(context: Context) {
        getVibratorPreS(context).vibrate(
            VibrationEffect.createWaveform(VIBRATION_PATTERN, VIBRATION_REPETITION_INDEX),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getVibratorS(context: Context): Vibrator =
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator

    private fun getVibratorPreS(context: Context): Vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}
