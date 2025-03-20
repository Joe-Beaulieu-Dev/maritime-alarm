package com.example.alarmscratch.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.alarmscratch.core.ui.notificationcheck.AppNotificationChannel
import com.example.alarmscratch.core.util.NotificationChannelUtil
import io.mockk.EqMatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationChannelUtilTest {

    /*
     * createNotificationChannel
     */

    @Test
    fun createNotificationChannel_CreatesChannel_WithProperAttributes() {
        val notificationManager = mockk<NotificationManager> {
            every { createNotificationChannel(any()) } returns Unit
        }
        val appNotificationChannel = AppNotificationChannel.Alarm
        val channelName = "channelName"
        val channelDescription = "channelDescription"
        val context = mockk<Context> {
            every { getString(appNotificationChannel.name) } returns channelName
            every { getString(appNotificationChannel.description) } returns channelDescription
            every { getSystemService(NotificationManager::class.java) } returns notificationManager
        }

        mockkConstructor(NotificationChannel::class) {
            every {
                constructedWith<NotificationChannel>(
                    EqMatcher(appNotificationChannel.id), EqMatcher(channelName), EqMatcher(appNotificationChannel.importance)
                ).description = any()
            } returns Unit
            every {
                constructedWith<NotificationChannel>(
                    EqMatcher(appNotificationChannel.id), EqMatcher(channelName), EqMatcher(appNotificationChannel.importance)
                ).setSound(any(), any())
            } returns Unit

            NotificationChannelUtil.createNotificationChannel(context, appNotificationChannel)

            verify {
                constructedWith<NotificationChannel>(
                    EqMatcher(appNotificationChannel.id), EqMatcher(channelName), EqMatcher(appNotificationChannel.importance)
                ).description = channelDescription

                constructedWith<NotificationChannel>(
                    EqMatcher(appNotificationChannel.id), EqMatcher(channelName), EqMatcher(appNotificationChannel.importance)
                ).setSound(
                    AppNotificationChannel.Alarm.soundAttributes!!.sound,
                    AppNotificationChannel.Alarm.soundAttributes!!.audioAttributes
                )

                notificationManager.createNotificationChannel(any())
            }
        }
    }

    /*
     * isNotificationChannelEnabled
     */

    @Test
    fun isNotificationChannelEnabled_ReturnsTrue_WhenNotificationsEnabled_AndChannelImportanceGreaterThanNone() {
        val appNotificationChannel = AppNotificationChannel.Alarm
        val notificationChannel = mockk<NotificationChannel> {
            every { importance } returns NotificationManager.IMPORTANCE_HIGH
        }
        val notificationManager = mockk<NotificationManager> {
            every { getNotificationChannel(appNotificationChannel.id) } returns notificationChannel
            every { areNotificationsEnabled() } returns true
        }
        val context = mockk<Context> {
            every { getSystemService(NotificationManager::class.java) } returns notificationManager
        }

        // NotificationManager.IMPORTANCE_HIGH
        assertTrue(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))

        // NotificationManager.IMPORTANCE_DEFAULT
        every { notificationChannel.importance } returns NotificationManager.IMPORTANCE_DEFAULT
        assertTrue(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))

        // NotificationManager.IMPORTANCE_LOW
        every { notificationChannel.importance } returns NotificationManager.IMPORTANCE_LOW
        assertTrue(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))

        // NotificationManager.IMPORTANCE_MIN
        every { notificationChannel.importance } returns NotificationManager.IMPORTANCE_MIN
        assertTrue(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))
    }

    @Test
    fun isNotificationChannelEnabled_ReturnsFalse_WhenNotificationsEnabled_AndChannelImportanceIsNone() {
        val appNotificationChannel = AppNotificationChannel.Alarm
        val notificationChannel = mockk<NotificationChannel> {
            every { importance } returns NotificationManager.IMPORTANCE_NONE
        }
        val notificationManager = mockk<NotificationManager> {
            every { getNotificationChannel(appNotificationChannel.id) } returns notificationChannel
            every { areNotificationsEnabled() } returns true
        }
        val context = mockk<Context> {
            every { getSystemService(NotificationManager::class.java) } returns notificationManager
        }

        assertFalse(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))
    }

    @Test
    fun isNotificationChannelEnabled_ReturnsFalse_WhenNotificationsDisabled_AndChannelImportanceGreaterThanNone() {
        val appNotificationChannel = AppNotificationChannel.Alarm
        val notificationChannel = mockk<NotificationChannel> {
            every { importance } returns NotificationManager.IMPORTANCE_HIGH
        }
        val notificationManager = mockk<NotificationManager> {
            every { getNotificationChannel(appNotificationChannel.id) } returns notificationChannel
            every { areNotificationsEnabled() } returns false
        }
        val context = mockk<Context> {
            every { getSystemService(NotificationManager::class.java) } returns notificationManager
        }

        // NotificationManager.IMPORTANCE_HIGH
        assertFalse(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))

        // NotificationManager.IMPORTANCE_DEFAULT
        every { notificationChannel.importance } returns NotificationManager.IMPORTANCE_DEFAULT
        assertFalse(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))

        // NotificationManager.IMPORTANCE_LOW
        every { notificationChannel.importance } returns NotificationManager.IMPORTANCE_LOW
        assertFalse(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))

        // NotificationManager.IMPORTANCE_MIN
        every { notificationChannel.importance } returns NotificationManager.IMPORTANCE_MIN
        assertFalse(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))
    }

    @Test
    fun isNotificationChannelEnabled_ReturnsFalse_WhenNotificationsDisabled_AndChannelImportanceIsNone() {
        val appNotificationChannel = AppNotificationChannel.Alarm
        val notificationChannel = mockk<NotificationChannel> {
            every { importance } returns NotificationManager.IMPORTANCE_NONE
        }
        val notificationManager = mockk<NotificationManager> {
            every { getNotificationChannel(appNotificationChannel.id) } returns notificationChannel
            every { areNotificationsEnabled() } returns false
        }
        val context = mockk<Context> {
            every { getSystemService(NotificationManager::class.java) } returns notificationManager
        }

        assertFalse(NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel))
    }
}
