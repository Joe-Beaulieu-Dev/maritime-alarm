package com.example.alarmscratch.core

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.alarmscratch.core.ui.permission.Permission
import com.example.alarmscratch.core.util.PermissionUtil
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
    fun isPermissionGranted_ReturnsTrue_WhenPermissionIsGranted() {
        mockkStatic(ContextCompat::class) {
            every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_GRANTED
            assertTrue(PermissionUtil.isPermissionGranted(mockk<Context>(), Permission.PostNotifications))
        }
    }

    @Test
    fun isPermissionGranted_ReturnsFalse_WhenPermissionIsDenied() {
        mockkStatic(ContextCompat::class) {
            every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_DENIED
            assertFalse(PermissionUtil.isPermissionGranted(mockk<Context>(), Permission.PostNotifications))
        }
    }
}
