package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager

class RingtonePlayer {

    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var ringtone: Ringtone? = null

    fun playRingtone(context: Context) {
        if (audioManager == null) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(audioAttributes)
            .build()

        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, defaultRingtoneUri)


        // Getting around smart cast mutability warnings
        audioManager?.let { manager ->
            audioFocusRequest?.let { request ->
                ringtone?.let { ring ->
                    manager.requestAudioFocus(request)
                    ring.play()
                }
            }
        }
    }

    fun stopRingtone() {
        ringtone?.stop()
        ringtone = null
        audioFocusRequest?.let { request ->
            audioManager?.abandonAudioFocusRequest(request)
        }
    }
}