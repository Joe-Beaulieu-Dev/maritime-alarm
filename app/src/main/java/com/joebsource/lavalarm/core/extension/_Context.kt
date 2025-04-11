package com.joebsource.lavalarm.core.extension

import android.content.Context
import com.joebsource.lavalarm.core.AlarmApplication

/**
 * Returns the Application Context cast as AlarmApplication.
 *
 * Just makes the code a bit smaller.
 *
 * @return the AlarmApplication
 */
val Context.alarmApplication: AlarmApplication
    get() = applicationContext as AlarmApplication
