package com.example.alarmscratch.core.data.repository

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.alarmscratch.core.data.model.RingtoneData

class RingtoneRepository(private val context: Context) {

    companion object {
        private const val SYSTEM_DEFAULT_RINGTONE_TITLE_PREFIX = "Default ("
        private const val SYSTEM_DEFAULT_RINGTONE_TITLE_SUFFIX = ")"
    }

    fun getRingtone(uriString: String): Ringtone {
        val ringtoneUri = Uri.parse(uriString)
        var ringtone = RingtoneManager.getRingtone(context.applicationContext, ringtoneUri)

        if (ringtone == null) {
            // TODO: This can return null, just check it out
            val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context.applicationContext, defaultRingtoneUri)
        }

        // TODO: This can technically still be null since getRingtone() can return null.
        //  It really shouldn't since we're already grabbing the system default as a fallback,
        //  but theoretically it can be. Include a tone with the app to give just in case.
        return ringtone
    }

    fun getAllRingtoneData(): List<RingtoneData> {
        val ringtoneManager = RingtoneManager(context.applicationContext).apply { setType(RingtoneManager.TYPE_ALARM) }
        val ringtoneCursor = ringtoneManager.cursor
        val ringtoneList: MutableList<RingtoneData> = mutableListOf()

        if (ringtoneCursor.moveToFirst()) {
            do {
                try {
                    val id = ringtoneCursor.getIntOrNull(RingtoneManager.ID_COLUMN_INDEX)
                    val name = ringtoneCursor.getStringOrNull(RingtoneManager.TITLE_COLUMN_INDEX)
                    val uri = ringtoneCursor.getStringOrNull(RingtoneManager.URI_COLUMN_INDEX)

                    if (id != null && name != null && uri != null) {
                        ringtoneList.add(RingtoneData(id = id, name = name, baseUri = uri))
                    }
                } catch (e: Exception) {
                    // Pulling from cursors can result in Exceptions.
                    // Nothing to do here besides just not crash, and
                    // move to the next item if there is one.
                }
            } while (ringtoneCursor.moveToNext())
        }
        // RingtoneManager documentation says not to close() the Cursor since it returns the same Cursor
        // every time you call getCursor() on the same instance, and instead suggests calling deactivate().
        // However, deactivate() is deprecated and has no suggested alternative. There's more to be said on this,
        // but to keep things brief, cursor.close() is being called since getAllRingtones() uses a new RingtoneManager
        // instance every time it's called.
        ringtoneCursor.close()

        return ringtoneList
    }

    fun tryGetNonGenericSystemDefaultUri(): String {
        val genericSystemDefaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val genericSystemDefaultRingtone: Ringtone? = RingtoneManager.getRingtone(context, genericSystemDefaultUri)

        return if (genericSystemDefaultRingtone != null) {
            // Remove the System Default Ringtone prefix and suffix and try to find a match on the cleaned name.
            // This could be improved with a Regex check, but for now this just needs to get done.
            val ringtoneList = getAllRingtoneData()
            val cleanRingtoneName = genericSystemDefaultRingtone
                .getTitle(context)
                .removePrefix(SYSTEM_DEFAULT_RINGTONE_TITLE_PREFIX)
                .removeSuffix(SYSTEM_DEFAULT_RINGTONE_TITLE_SUFFIX)
            val match: RingtoneData? = ringtoneList.firstOrNull { it.name == cleanRingtoneName }

            match?.fullUriString ?: genericSystemDefaultUri.toString()
        } else {
            genericSystemDefaultUri.toString()
        }
    }
}
