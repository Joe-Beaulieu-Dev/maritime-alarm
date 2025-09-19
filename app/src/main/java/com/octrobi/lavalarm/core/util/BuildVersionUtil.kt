package com.octrobi.lavalarm.core.util

import android.os.Build

object BuildVersionUtil {

    fun getCurrentSkdInt() =
        Build.VERSION.SDK_INT
}
