package com.joebsource.lavalarm.core.ringtone

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.Ringtone
import com.joebsource.lavalarm.core.data.repository.RingtoneRepository

class RingtonePlayer {

    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var ringtone: Ringtone? = null

    fun playRingtone(context: Context, ringtoneUri: String) {
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

        ringtone = getRingtone(context, ringtoneUri)

        // Getting around smart cast mutability warnings
        audioManager?.let { manager ->
            audioFocusRequest?.let { request ->
                ringtone?.let { ring ->
                    // Setting looping is only necessary when the RingtoneManager returns a default fallback Ringtone,
                    // which happens when it can't find a Ringtone for the given URI. This happens when the Alarm goes off
                    // after a device restart, when the User has a protected lock screen set up, BEFORE they unlock it for the first time.
                    // I'm guessing this is due to the RingtoneManager not having access to the necessary files while in this state.
                    // Note: In the above scenario, if the User unlocks their phone at least once, then re-locks it, the RingtoneManager
                    // will be able to find the Ringtone, and will not need to return the fallback.
                    //
                    // The non-fallback Ringtones used by this app, which are of type RingtoneManager.TYPE_ALARM,
                    // are already set to loop by default and therefore don't need to be set below.
                    try {
                        ring.isLooping = true
                    } catch (_: Exception) {
                        // Just don't crash, nothing else to do here
                        // When I've seen Ringtone.setLooping() used, it was called in a try/catch like this.
                        // Alarm execution is a critical part of the code so I'm not taking any chances.
                    }
                    manager.requestAudioFocus(request)

                    // Ringtone.setStreamType() is deprecated. However, it is the only way to get
                    // the Ringtone to play when the Device is set to Silent, which is standard
                    // behavior for an alarm app.
                    //
                    // Side Note: AudioAttributes.Builder().setLegacyStreamType() looks like it's
                    // supposed to do the same thing, but it doesn't work for this.
                    // AudioAttributes.Builder().setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED) also
                    // does not get the Ringtone to play while the Device is set to Silent.
                    ring.streamType = AudioManager.STREAM_ALARM
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
