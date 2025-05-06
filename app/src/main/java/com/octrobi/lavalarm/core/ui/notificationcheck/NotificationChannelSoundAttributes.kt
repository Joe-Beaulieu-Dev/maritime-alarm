package com.octrobi.lavalarm.core.ui.notificationcheck

import android.media.AudioAttributes
import android.net.Uri

data class NotificationChannelSoundAttributes(
    val sound: Uri? = null,
    val audioAttributes: AudioAttributes? = null
)
