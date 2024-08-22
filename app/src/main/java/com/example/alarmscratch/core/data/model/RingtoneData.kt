package com.example.alarmscratch.core.data.model

// TODO: Make actual Uri
class RingtoneData(
    id: Int,
    val name: String,
    baseUri: String
) {

    val fullUriString = "$baseUri/$id"

    companion object {
        const val KEY_FULL_RINGTONE_URI_STRING = "key_full_ringtone_uri_string"
        const val NO_RINGTONE_URI = ""
    }
}
