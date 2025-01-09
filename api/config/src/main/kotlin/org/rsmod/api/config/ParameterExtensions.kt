package org.rsmod.api.config

import kotlin.reflect.KProperty
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType

public fun <T : Any> locParam(param: ParamType<T>): ParameterProperty<T> = ParameterProperty(param)

public fun locXpParam(param: ParamType<Int>): ParameterXPProperty = ParameterXPProperty(param)

public fun <T : Any> objParam(param: ParamType<T>): ParameterProperty<T> = ParameterProperty(param)

public fun objXpParam(param: ParamType<Int>): ParameterXPProperty = ParameterXPProperty(param)

public class ParameterProperty<T : Any>(private val param: ParamType<T>) {
    public operator fun getValue(thisRef: UnpackedLocType, property: KProperty<*>): T =
        thisRef.param(param)

    public operator fun getValue(thisRef: UnpackedObjType, property: KProperty<*>): T =
        thisRef.param(param)
}

public class ParameterXPProperty(private val param: ParamType<Int>) {
    public operator fun getValue(thisRef: UnpackedLocType, property: KProperty<*>): Double {
        val fineXp = thisRef.param(param)
        return fineXp / PlayerStatMap.XP_FINE_PRECISION.toDouble()
    }

    public operator fun getValue(thisRef: UnpackedObjType, property: KProperty<*>): Double {
        val fineXp = thisRef.param(param)
        return fineXp / PlayerStatMap.XP_FINE_PRECISION.toDouble()
    }
}
