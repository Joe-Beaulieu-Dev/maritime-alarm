package com.example.alarmscratch.core.data.model

// TODO: Make actual Uri
data class RingtoneData(
    private val id: Int,
    val name: String,
    private val baseUri: String
) {

    val fullUriString = "$baseUri/$id"

    companion object {
        const val KEY_FULL_RINGTONE_URI_STRING = "key_full_ringtone_uri_string"
        const val NO_RINGTONE_URI = ""
    }
}
