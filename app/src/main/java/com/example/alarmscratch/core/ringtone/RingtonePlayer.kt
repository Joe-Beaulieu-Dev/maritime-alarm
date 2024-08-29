package com.example.alarmscratch.core.ringtone

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.Ringtone
import com.example.alarmscratch.core.data.repository.RingtoneRepository

class RingtonePlayer {

    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var ringtone: Ringtone? = null

    fun playRingtone(context: Context, ringtoneUriString: String) {
        // Stop currently playing Ringtone if there is one
        stopRingtone()

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

        ringtone = getRingtone(context, ringtoneUriString)

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

    private fun getRingtone(context: Context, alarmRingtoneUri: String): Ringtone {
        val ringtoneRepository = RingtoneRepository(context)
        return ringtoneRepository.getRingtone(alarmRingtoneUri)
    }
}
