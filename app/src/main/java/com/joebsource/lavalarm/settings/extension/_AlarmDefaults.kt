package com.joebsource.lavalarm.settings.extension

import android.content.Context
import android.media.Ringtone
import com.joebsource.lavalarm.core.data.repository.RingtoneRepository
import com.joebsource.lavalarm.settings.data.model.AlarmDefaults

// TODO: Do something better here. There's a similar method in _Alarm.kt as well.
fun AlarmDefaults.getRingtone(context: Context): Ringtone =
    RingtoneRepository(context).getRingtone(ringtoneUri)
