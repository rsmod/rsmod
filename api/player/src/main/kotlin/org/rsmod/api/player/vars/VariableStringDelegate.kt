package org.rsmod.api.player.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varp.VarpType

public fun varpStringDelegate(varp: VarpType): VariableStringDelegate = VariableStringDelegate(varp)

public fun <T> varpStringTypedDelegate(
    varp: VarpType,
    toType: (String?) -> T,
    fromType: (T) -> String,
): VariableTypeStringDelegate<T> = VariableTypeStringDelegate(varp, toType, fromType)

public class VariableStringDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): String? {
        return thisRef.varsString[varp]
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: String?) {
        thisRef.syncVarpStr(varp, value)
    }
}

public class VariableTypeStringDelegate<T>(
    private val varp: VarpType,
    public val toType: (String?) -> T,
    public val fromType: (T) -> String,
) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): T {
        val varValue = thisRef.varsString[varp]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.syncVarpStr(varp, value)
        } else {
            val varValue = fromType(value)
            thisRef.syncVarpStr(varp, varValue)
        }
    }
}
