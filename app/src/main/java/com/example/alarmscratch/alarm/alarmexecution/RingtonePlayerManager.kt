package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context

object RingtonePlayerManager {

    private var ringtonePlayer = RingtonePlayer()

    fun startAlarmSound(context: Context) {
        ringtonePlayer.playRingtone(context.applicationContext)
    }

    fun stopAlarmSound() {
        ringtonePlayer.stopRingtone()
    }
}
