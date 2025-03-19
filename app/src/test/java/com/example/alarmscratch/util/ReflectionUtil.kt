package com.example.alarmscratch.util

import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

inline fun <reified T : Any> T.callPrivateFunction(name: String, vararg args: Any): Any? {
    val function = T::class.declaredMemberFunctions.firstOrNull { it.name == name }
    function?.isAccessible = true
    val ret = function?.call(this, *args)
    function?.isAccessible = false

    return ret
}
