package com.octrobi.lavalarm.core.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.octrobi.lavalarm.alarm.data.model.AlarmExecutionData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

object AlarmExecutionDataNavType : NavType<AlarmExecutionData>(isNullableAllowed = false) {

    val typeMap = mapOf(typeOf<AlarmExecutionData>() to AlarmExecutionDataNavType)

    override fun put(bundle: SavedState, key: String, value: AlarmExecutionData) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: SavedState, key: String): AlarmExecutionData? {
        return Json.decodeFromString(bundle.getString(key) ?: return null)
    }

    override fun serializeAsValue(value: AlarmExecutionData): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): AlarmExecutionData {
        return Json.decodeFromString(Uri.decode(value))
    }
}
