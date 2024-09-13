package org.rsmod.api.player.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.time.InlineLocalDateTime

public fun varpDateDelegate(varp: VarpType): VariableLocalDateTimeDelegate =
    VariableLocalDateTimeDelegate(varp)

public class VariableLocalDateTimeDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): InlineLocalDateTime {
        val packed = thisRef.vars[varp] ?: return InlineLocalDateTime.NULL
        return InlineLocalDateTime(packed)
    }

    public operator fun setValue(
        thisRef: Player,
        property: KProperty<*>,
        value: InlineLocalDateTime,
    ) {
        if (value == InlineLocalDateTime.NULL) {
            thisRef.syncVarp(varp, 0)
        } else {
            thisRef.syncVarp(varp, value.packed)
        }
    }
}
