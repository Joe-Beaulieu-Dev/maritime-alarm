package com.octrobi.lavalarm.core.ringtone

import android.content.Context

object RingtonePlayerManager {

    private var ringtonePlayer = RingtonePlayer()

    fun startAlarmSound(context: Context, ringtoneUri: String) {
        ringtonePlayer.playRingtone(context.applicationContext, ringtoneUri)
    }

    fun stopAlarmSound() {
        ringtonePlayer.stopRingtone()
    }
}
