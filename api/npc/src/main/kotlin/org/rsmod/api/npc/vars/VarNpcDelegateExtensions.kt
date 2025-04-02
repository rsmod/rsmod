package org.rsmod.api.npc.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.type.varn.VarnType
import org.rsmod.game.type.varnbit.VarnBitType
import org.rsmod.map.CoordGrid

/* Varnpc delegates */
public fun intVarn(varn: VarnType): NpcVariableIntDelegate = NpcVariableIntDelegate(varn)

public fun boolVarn(varn: VarnType): NpcVariableTypeIntDelegate<Boolean> =
    typeIntVarn(varn, ::boolFromInt, ::boolToInt)

public fun typeCoordVarn(varn: VarnType): NpcVariableTypeIntDelegate<CoordGrid?> {
    val fromType: (CoordGrid?) -> Int = { typed -> typed?.packed ?: CoordGrid.NULL.packed }
    return typeIntVarn(varn, ::CoordGrid, fromType)
}

public fun typeNpcUidVarn(varn: VarnType): NpcVariableTypeIntDelegate<NpcUid?> {
    val fromType: (NpcUid?) -> Int = { typed -> typed?.packed ?: NpcUid.NULL.packed }
    return typeIntVarn(varn, ::NpcUid, fromType)
}

public fun typePlayerUidVarn(varn: VarnType): NpcVariableTypeIntDelegate<PlayerUid?> {
    val fromType: (PlayerUid?) -> Int = { typed -> typed?.packed ?: PlayerUid.NULL.packed }
    return typeIntVarn(varn, ::PlayerUid, fromType)
}

public fun <T> typeIntVarn(
    varn: VarnType,
    toType: (Int) -> T,
    fromType: (T) -> Int,
): NpcVariableTypeIntDelegate<T> = NpcVariableTypeIntDelegate(varn, toType, fromType)

/* Varnbit delegates */
public fun intVarnBit(varnbit: VarnBitType): NpcVariableIntBitsDelegate =
    NpcVariableIntBitsDelegate(varnbit)

public fun boolVarnBit(varnbit: VarnBitType): NpcVariableTypeIntBitsDelegate<Boolean> =
    typeIntVarnBit(varnbit, ::boolFromInt, ::boolToInt)

public fun <T> typeIntVarnBit(
    varnbit: VarnBitType,
    toType: (Int) -> T,
    fromType: (T) -> Int,
): NpcVariableTypeIntBitsDelegate<T> = NpcVariableTypeIntBitsDelegate(varnbit, toType, fromType)

/* Delegate implementations */
public class NpcVariableIntDelegate(private val varn: VarnType) {
    public operator fun getValue(thisRef: Npc, property: KProperty<*>): Int {
        return thisRef.vars[varn]
    }

    public operator fun setValue(thisRef: Npc, property: KProperty<*>, value: Int) {
        thisRef.vars[varn] = value
    }
}

public class NpcVariableTypeIntDelegate<T>(
    private val varn: VarnType,
    public val toType: (Int) -> T,
    public val fromType: (T) -> Int,
) {
    public operator fun getValue(thisRef: Npc, property: KProperty<*>): T {
        val varValue = thisRef.vars[varn]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Npc, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.vars.remove(varn)
        } else {
            val varValue = fromType(value)
            thisRef.vars[varn] = varValue
        }
    }
}

public class NpcVariableIntBitsDelegate(private val varnbit: VarnBitType) {
    public operator fun getValue(thisRef: Npc, property: KProperty<*>): Int {
        return thisRef.vars[varnbit]
    }

    public operator fun setValue(thisRef: Npc, property: KProperty<*>, value: Int) {
        thisRef.vars[varnbit] = value
    }
}

public class NpcVariableTypeIntBitsDelegate<T>(
    private val varnbit: VarnBitType,
    public val toType: (Int) -> T,
    public val fromType: (T) -> Int,
) {
    public operator fun getValue(thisRef: Npc, property: KProperty<*>): T {
        val varValue = thisRef.vars[varnbit]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Npc, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        thisRef.vars[varnbit] = varValue
    }
}

/* Utility functions */
private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

private fun boolFromInt(int: Int): Boolean = int == 1
