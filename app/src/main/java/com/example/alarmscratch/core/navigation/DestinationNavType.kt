package com.example.alarmscratch.core.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DestinationNavType {

    val CoreNavComponent2Type = object : NavType<CoreNavComponent2?>(isNullableAllowed = true) {
        override fun put(bundle: Bundle, key: String, value: CoreNavComponent2?) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun get(bundle: Bundle, key: String): CoreNavComponent2? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun serializeAsValue(value: CoreNavComponent2?): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun parseValue(value: String): CoreNavComponent2 {
            return Json.decodeFromString<CoreNavComponent2>(value)
        }
    }
}
