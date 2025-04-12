package com.joebsource.lavalarm.core.ui.notificationcheck

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NotificationChannelGateScreen(
    notificationPermission: NotificationPermission,
    gatedScreen: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    notificationChannelGateViewModel: NotificationChannelGateViewModel = viewModel(factory = NotificationChannelGateViewModel.Factory)
) {
    // State
    val disabledChannelList = notificationChannelGateViewModel.disabledChannelList

    // Notification check
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val checkNotificationChannelStatus: () -> Unit = {
        notificationChannelGateViewModel.checkNotificationChannelStatus(context, notificationPermission.appNotificationChannel)
    }

    LaunchedEffect(key1 = context, key2 = lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                checkNotificationChannelStatus()
            }
        }
    }

    if (disabledChannelList.isNotEmpty()) {
        // TODO: The below property is a work around for an Android Studio bug where fromHtml() does not work with previews.
        //  The bug mentions TextDefaults.fromHtml() instead of AnnotatedString.fromHtml(), but for reasons I'm not going to get
        //  into for brevity, I believe it's the same thing. This will hopefully go away after updating Android Studio to Koala
        //  or above. Since this only affects previews, just create the AnnotatedString up here and pass it to the previewed composable.
        //  https://issuetracker.google.com/issues/139326648#comment18
        //  https://issuetracker.google.com/issues/336161238
        val notificationDisabledBodyText = AnnotatedString.fromHtml(
            stringResource(id = notificationPermission.notificationDisabledBodyText)
        )

        NotificationChannelGateScreenContent(
            bodyText = notificationDisabledBodyText,
            openSettings = { notificationChannelGateViewModel.openNotificationSettings(context) },
            modifier = modifier
        )
    } else {
        gatedScreen()
    }
}

@Composable
fun NotificationChannelGateScreenContent(
    bodyText: AnnotatedString,
    openSettings: () -> Unit,
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
                    text = stringResource(id = R.string.notification_required),
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
                onClick = openSettings,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp)
            ) {
                Text(text = stringResource(id = R.string.notification_open_notification_settings))
            }
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun NotificationChannelGateScreenPreview() {
    LavalarmTheme {
        NotificationChannelGateScreenContent(
            bodyText = buildAnnotatedString {
                append(stringResource(id = R.string.notification_missing_alarm_system_settings))
            },
            openSettings = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
