package com.joebsource.lavalarm.core.extension

import android.content.Intent
import android.os.Build
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getSerializableExtraSafe(name: String, clazz: Class<T>): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(name, clazz)
    } else {
        getSerializableExtra(name) as? T
    }
