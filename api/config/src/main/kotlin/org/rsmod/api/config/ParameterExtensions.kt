package org.rsmod.api.config

import kotlin.reflect.KProperty
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType

fun <T : Any> locParam(param: ParamType<T>): ParameterProperty<T> = ParameterProperty(param)

fun locXpParam(param: ParamType<Int>): ParameterXPProperty = ParameterXPProperty(param)

fun <T : Any> objParam(param: ParamType<T>): ParameterProperty<T> = ParameterProperty(param)

fun objXpParam(param: ParamType<Int>): ParameterXPProperty = ParameterXPProperty(param)

class ParameterProperty<T : Any>(private val param: ParamType<T>) {
    operator fun getValue(thisRef: UnpackedLocType, property: KProperty<*>): T =
        thisRef.param(param)

    operator fun getValue(thisRef: UnpackedObjType, property: KProperty<*>): T =
        thisRef.param(param)
}

class ParameterXPProperty(private val param: ParamType<Int>) {
    operator fun getValue(thisRef: UnpackedLocType, property: KProperty<*>): Double {
        val fineXp = thisRef.param(param)
        return fineXp / PlayerStatMap.XP_FINE_PRECISION.toDouble()
    }

    operator fun getValue(thisRef: UnpackedObjType, property: KProperty<*>): Double {
        val fineXp = thisRef.param(param)
        return fineXp / PlayerStatMap.XP_FINE_PRECISION.toDouble()
    }
}
