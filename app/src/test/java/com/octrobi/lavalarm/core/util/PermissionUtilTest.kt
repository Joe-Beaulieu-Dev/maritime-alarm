package com.octrobi.lavalarm.core.util

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.octrobi.lavalarm.core.ui.permission.Permission
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionUtilTest {

    /*
     * isPermissionGranted
     */

    @Test
    fun isPermissionGranted_ReturnsTrue_WhenStandardRuntimePermission_IsGranted() {
        mockkStatic(ContextCompat::class) {
            every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_GRANTED
            assertTrue(PermissionUtil.isPermissionGranted(mockk<Context>(), Permission.PostNotifications))
        }
    }

    @Test
    fun isPermissionGranted_ReturnsFalse_WhenStandardRuntimePermission_IsDenied() {
        mockkStatic(ContextCompat::class) {
            every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_DENIED
            assertFalse(PermissionUtil.isPermissionGranted(mockk<Context>(), Permission.PostNotifications))
        }
    }

    @Test
    fun isPermissionGranted_ReturnsTrue_WhenSpecialPermission_ScheduleExactAlarm_IsGranted() {
        val alarmManager = mockk<AlarmManager> {
            every { canScheduleExactAlarms() } returns true
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
        }

        assertTrue(PermissionUtil.isPermissionGranted(context, Permission.ScheduleExactAlarm))
    }

    @Test
    fun isPermissionGranted_ReturnsFalse_WhenSpecialPermission_ScheduleExactAlarm_IsDenied() {
        val alarmManager = mockk<AlarmManager> {
            every { canScheduleExactAlarms() } returns false
        }
        val context = mockk<Context> {
            every { getSystemService(AlarmManager::class.java) } returns alarmManager
        }

        assertFalse(PermissionUtil.isPermissionGranted(context, Permission.ScheduleExactAlarm))
    }
}
