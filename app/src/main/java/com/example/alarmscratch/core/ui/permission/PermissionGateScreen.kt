package com.example.alarmscratch.core.ui.permission

import android.app.Activity
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

@Composable
fun PermissionGateScreen(
    permission: String,
    gatedScreen: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    permissionGateViewModel: PermissionGateViewModel = viewModel(factory = PermissionGateViewModel.Factory)
) {
    // State
    val attemptedToAskForPermission by permissionGateViewModel.attemptedToAskForPermission.collectAsState()
    val deniedPermissionList = permissionGateViewModel.deniedPermissionList

    // Permission logic
    val shouldShowRequestPermissionRationale = (LocalContext.current as? Activity)
        ?.shouldShowRequestPermissionRationale(permission)
        ?: false
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGateViewModel.onPermissionResult(permission, isGranted)
    }

    // Must auto-call because of the nature of permissions on Android
    LaunchedEffect(key1 = attemptedToAskForPermission) {
        if (!attemptedToAskForPermission) {
            permissionRequestLauncher.launch(permission)
        }
    }

    // Don't show anything until we know we've asked the User for the required permissions at least one time
    if (attemptedToAskForPermission) {
        // Permission Denied
        if (deniedPermissionList.contains(permission)) {
            // The User denied the permission only once, therefore we can still display
            // the Permission Request System Dialog
            if (shouldShowRequestPermissionRationale) {
                PermissionGateScreenContent(
                    bodyTextRes = R.string.permission_missing_system_dialog,
                    requestButtonTextRes = R.string.permission_request,
                    onRequest = { permissionRequestLauncher.launch(permission) },
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
                    bodyTextRes = R.string.permission_missing_system_settings,
                    requestButtonTextRes = R.string.permission_open_system_settings,
                    onRequest = {},
                    modifier = modifier
                )
            }
        } else {
            // Permission granted, show gated screen
            gatedScreen()
        }
    }
}

@Composable
fun PermissionGateScreenContent(
    @StringRes bodyTextRes: Int,
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
                text = stringResource(id = bodyTextRes),
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
private fun PermissionGateScreenContentSystemDialogPreview() {
    AlarmScratchTheme {
        PermissionGateScreenContent(
            bodyTextRes = R.string.permission_missing_system_dialog,
            requestButtonTextRes = R.string.permission_request,
            onRequest = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun PermissionGateScreenContentSystemSettingsPreview() {
    AlarmScratchTheme {
        PermissionGateScreenContent(
            bodyTextRes = R.string.permission_missing_system_settings,
            requestButtonTextRes = R.string.permission_open_system_settings,
            onRequest = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
