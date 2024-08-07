package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context

object RingtonePlayerManager {

    private var ringtonePlayer: RingtonePlayer? = null

    fun startAlarmSound(context: Context) {
        getRingtonePlayer()?.playRingtone(context.applicationContext)
    }

    fun stopAlarmSound() {
        getRingtonePlayer()?.stopRingtone()
    }

    private fun getRingtonePlayer(): RingtonePlayer? {
        if (ringtonePlayer == null) {
            ringtonePlayer = RingtonePlayer()
        }

        return ringtonePlayer
    }
}
