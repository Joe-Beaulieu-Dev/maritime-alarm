package com.joebsource.lavalarm.core.extension

import android.content.BroadcastReceiver
import android.content.BroadcastReceiver.PendingResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("ClassName")
class _BroadcastReceiverTest {

    /*
     * doAsync
     */

    // OptIn for advanceUntilIdle()
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doAsync_CallsBlock_AndFinishesPendingResult() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        var blockCalled = false
        val block: () -> Unit = { blockCalled = true }
        val pendingResult = mockk<PendingResult> {
            every { finish() } returns Unit
        }
        val receiver = mockk<BroadcastReceiver> {
            every { goAsync() } returns pendingResult
        }

        receiver.doAsync(this, dispatcher, block)
        advanceUntilIdle()

        assertTrue(blockCalled)
        verify {
            pendingResult.finish()
        }
    }
}
