package com.example.alarmscratch.core.data.repository

import android.content.Context
import android.media.RingtoneManager
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.alarmscratch.core.data.model.RingtoneData

class RingtoneRepository(private val context: Context) {

    fun getAllRingtones(): List<RingtoneData> {
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
}
