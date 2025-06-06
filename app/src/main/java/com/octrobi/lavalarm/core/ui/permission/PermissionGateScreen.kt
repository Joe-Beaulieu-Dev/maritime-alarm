package com.octrobi.lavalarm.core.ui.permission

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.util.PermissionUtil

@Composable
fun PermissionGateScreen(
    permission: Permission,
    gatedScreen: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    permissionGateViewModel: PermissionGateViewModel = viewModel(factory = PermissionGateViewModel.Factory)
) {
    // State
    val attemptedToAskForPermission by permissionGateViewModel.attemptedToAskForPermission.collectAsState()
    val deniedPermissionList = permissionGateViewModel.deniedPermissionList

    // Permission logic
    val context = LocalContext.current
    // This always returns false for Special Permissions
    val shouldShowRequestPermissionRationale = (context as? Activity)
        ?.shouldShowRequestPermissionRationale(permission.permissionString)
        ?: false
    // The Permission Request System Dialog will never display for Special Permissions.
    // Always launch the System Settings for Special Permissions instead.
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGateViewModel.onPermissionResult(permission, isGranted)
    }
    val systemSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        permissionGateViewModel.onReturnFromSystemSettings(context, permission)
    }

    // Initial query. Must auto-call because of the nature of permissions on Android.
    LaunchedEffect(key1 = attemptedToAskForPermission) {
        if (!attemptedToAskForPermission) {
            if (permission.permissionType == Permission.PermissionType.SPECIAL) {
                // The Permission Request System Dialog will never display for Special Permissions,
                // therefore just query the permission status and invoke the ViewModel's onPermissionResult()
                // function to progress the permission check logic.
                permissionGateViewModel.onPermissionResult(
                    permission,
                    PermissionUtil.isPermissionGranted(context, permission)
                )
            } else {
                // If the permission is already granted, then "launching" the permissionRequestLauncher
                // will not cause the Permission Request System Dialog to show. Instead, the permissionRequestLauncher's
                // onResult() function will immediately return true, changing attemptedToAskForPermission to true
                // without ever showing the Dialog. To the User, it will look as if the app just launched normally.
                permissionRequestLauncher.launch(permission.permissionString)
            }
        }
    }

    // Don't show anything until we know we've asked the User for the required permissions at least one time
    if (attemptedToAskForPermission) {
        // Permission denied
        if (deniedPermissionList.contains(permission)) {
            // TODO: The below 2 properties are a work around for an Android Studio bug where fromHtml() does not work with previews.
            //  The bug mentions TextDefaults.fromHtml() instead of AnnotatedString.fromHtml(), but for reasons I'm not going to get
            //  into for brevity, I believe it's the same thing. This will hopefully go away after updating Android Studio to Koala
            //  or above. Since this only affects previews, just create the AnnotatedString up here and pass it to the previewed composable.
            //  https://issuetracker.google.com/issues/139326648#comment18
            //  https://issuetracker.google.com/issues/336161238
            val systemDialogBodyText = AnnotatedString.fromHtml(stringResource(id = permission.systemDialogBodyRes))
            val systemSettingsBodyText = AnnotatedString.fromHtml(stringResource(id = permission.systemSettingsBodyRes))

            if (permission.permissionType == Permission.PermissionType.SPECIAL) {
                // Special Permissions can only be manually granted via the System Settings
                PermissionGateScreenContent(
                    bodyText = systemSettingsBodyText,
                    requestButtonTextRes = R.string.permission_open_system_settings,
                    onRequest = {
                        systemSettingsLauncher.launch(
                            Intent(
                                permission.systemSettingsAction,
                                Uri.fromParts(UriScheme.PACKAGE, context.packageName, null)
                            )
                        )
                    },
                    modifier = modifier
                )
            } else {
                // The User denied the permission only once, therefore we can
                // still display the Permission Request System Dialog.
                if (shouldShowRequestPermissionRationale) {
                    PermissionGateScreenContent(
                        bodyText = systemDialogBodyText,
                        requestButtonTextRes = R.string.permission_request,
                        onRequest = { permissionRequestLauncher.launch(permission.permissionString) },
                        modifier = modifier
                    )
                } else {
                    // The User denied the permission twice. Therefore the System will never show
                    // the Permission Request System Dialog again. Therefore we must give the User an option
                    // to manually grant the permission, since at this point the System will never show the
                    // Permission Request System Dialog ever again. Explain why the permission is needed, inform
                    // the User that they can manually accept the permission in the System Settings, and display
                    // a Button that leads to the System Settings, which they can decide if they want to press.
                    PermissionGateScreenContent(
                        bodyText = systemSettingsBodyText,
                        requestButtonTextRes = R.string.permission_open_system_settings,
                        onRequest = {
                            systemSettingsLauncher.launch(
                                Intent(
                                    permission.systemSettingsAction,
                                    Uri.fromParts(UriScheme.PACKAGE, context.packageName, null)
                                )
                            )
                        },
                        modifier = modifier
                    )
                }
            }
        } else {
            // Permission granted, show gated screen
            gatedScreen()
        }
    }
}

@Composable
fun PermissionGateScreenContent(
    bodyText: AnnotatedString,
    @StringRes requestButtonTextRes: Int,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Icon and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp)
            ) {
                // Icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Title
                Text(
                    text = stringResource(id = R.string.permission_required),
                    fontSize = 24.sp
                )
            }

            // Body
            Text(
                text = bodyText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp)
            )

            // Request Button
            Button(
                onClick = onRequest,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp)
            ) {
                Text(text = stringResource(id = requestButtonTextRes))
            }
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun PermissionGateScreenNotificationSystemDialogPreview() {
    LavalarmTheme {
        PermissionGateScreenContent(
            bodyText = buildAnnotatedString {
                append(stringResource(id = R.string.permission_missing_notifications_system_dialog))
            },
            requestButtonTextRes = R.string.permission_request,
            onRequest = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun PermissionGateScreenNotificationSystemSettingsPreview() {
    LavalarmTheme {
        PermissionGateScreenContent(
            bodyText = buildAnnotatedString {
                append(stringResource(id = R.string.permission_missing_notifications_system_settings))
            },
            requestButtonTextRes = R.string.permission_open_system_settings,
            onRequest = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun PermissionGateScreenAlarmSystemSettingsPreview() {
    LavalarmTheme {
        PermissionGateScreenContent(
            bodyText = buildAnnotatedString {
                append(stringResource(id = R.string.permission_missing_alarm_system_settings))
            },
            requestButtonTextRes = R.string.permission_open_system_settings,
            onRequest = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
