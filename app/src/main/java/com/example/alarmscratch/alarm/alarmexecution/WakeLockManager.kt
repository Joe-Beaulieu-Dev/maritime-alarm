package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.WakeLock

object WakeLockManager {

    // TODO: Modify Tag
    private const val PARTIAL_WAKE_LOCK_TAG = "alarmscratch:partial_wake_lock_tag"
    private var partialWakeLock: WakeLock? = null

    fun acquireWakeLock(context: Context) {
        if (partialWakeLock == null) {
            val powerManager = context.applicationContext.getSystemService(PowerManager::class.java)
            partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, PARTIAL_WAKE_LOCK_TAG)

            // Acquire WakeLock with 10min timeout
            partialWakeLock?.acquire(10 * 60 * 1000L)
        }
    }

    fun releaseWakeLock() {
        partialWakeLock?.release().also { partialWakeLock = null }
    }
}
