package com.example.alarmscratch.testutil

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice

object PermissionUtil {

    private const val SYSTEM_DIALOG_ALLOW = "Allow"
    // Text uses smart apostrophe
    private const val SYSTEM_DIALOG_DENY = "Don\u2019t allow"

    /*
     * With System Permission Dialog
     */

    /**
     * Grants whatever permission is currently being requested via the System Permission Dialog
     */
    fun grantPermissionDialog() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .findObject(By.text(SYSTEM_DIALOG_ALLOW))
            .click()
    }

    /**
     * Denies whatever permission is currently being requested via the System Permission Dialog
     */
    fun denyPermissionDialog() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .findObject(By.text(SYSTEM_DIALOG_DENY))
            .click()
    }

    /*
     * Without System Permission Dialog
     */

    /**
     * Grants a specific permission without having to go through the System Permission Dialog
     *
     * @param permission permission string
     */
    fun grantPermissionAuto(permission: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.grantRuntimePermission(
            instrumentation.targetContext.packageName,
            permission
        )
    }
}
