package com.example.alarmscratch.core.data.model

// TODO: Make actual Uri
class RingtoneData(val id: Int, val name: String, private val baseUri: String) {

    companion object {
        const val FULL_RINGTONE_URI = "full_ringtone_uri"
        const val NO_RINGTONE_URI = ""
    }

    fun getFullUriString(): String = "$baseUri/$id"
}
