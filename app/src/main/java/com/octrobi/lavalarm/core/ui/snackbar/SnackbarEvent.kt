package com.octrobi.lavalarm.core.ui.snackbar

data class SnackbarEvent(val message: String) {
    companion object {
        const val KEY_SNACKBAR_EVENT_MESSAGE = "key_snackbar_event_message"
    }
}
