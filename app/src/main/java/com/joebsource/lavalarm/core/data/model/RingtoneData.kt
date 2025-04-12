package com.joebsource.lavalarm.core.data.model

class RingtoneData(
    id: Int,
    val name: String,
    baseUri: String
) {

    val fullUri = "$baseUri/$id"

    companion object {
        const val KEY_FULL_RINGTONE_URI = "key_full_ringtone_uri"
        const val NO_RINGTONE_URI = ""
    }
}
