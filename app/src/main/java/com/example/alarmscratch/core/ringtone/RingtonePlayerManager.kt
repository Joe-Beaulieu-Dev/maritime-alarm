package com.example.alarmscratch.core.ringtone

import android.content.Context

object RingtonePlayerManager {

    private var ringtonePlayer = RingtonePlayer()

    fun startAlarmSound(context: Context, ringtoneUriString: String) {
        ringtonePlayer.playRingtone(context.applicationContext, ringtoneUriString)
    }

    fun stopAlarmSound() {
        ringtonePlayer.stopRingtone()
    }
}
