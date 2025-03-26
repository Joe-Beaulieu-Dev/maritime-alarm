package com.example.alarmscratch.testutil

import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinFunction

inline fun <reified T : Any> T.callPrivateFunction(name: String, vararg args: Any): Any? {
    val function = T::class.declaredMemberFunctions.firstOrNull { it.name == name }
    function?.isAccessible = true
    val ret = function?.call(this, *args)
    function?.isAccessible = false

    return ret
}

object ReflectionUtil {

    fun callPrivateTopLevelFunction(className: String, functionName: String, vararg args: Any): Any? {
        val clazz = Class.forName(className)
        val paramClasses = args.asList().map { it::class.java }.toTypedArray()
        val function = clazz.getDeclaredMethod(functionName, *paramClasses).kotlinFunction
        function?.isAccessible = true
        val ret = function?.call(*args)
        function?.isAccessible = false

        return ret
    }
}
