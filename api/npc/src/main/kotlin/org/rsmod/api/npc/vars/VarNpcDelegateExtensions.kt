package org.rsmod.api.npc.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.type.varn.VarnType
import org.rsmod.game.type.varnbit.VarnBitType
import org.rsmod.map.CoordGrid
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits

/* Varnpc delegates */
public fun intVarn(varn: VarnType): NpcVariableIntDelegate = NpcVariableIntDelegate(varn)

public fun boolVarn(varn: VarnType): NpcVariableTypeIntDelegate<Boolean> =
    typeIntVarn(varn, ::boolFromInt, ::boolToInt)

public fun typeCoordVarp(varn: VarnType): NpcVariableTypeIntDelegate<CoordGrid?> {
    val fromType: (CoordGrid?) -> Int = { typed -> typed?.packed ?: CoordGrid.NULL.packed }
    return typeIntVarn(varn, ::CoordGrid, fromType)
}

public fun typeNpcUidVarp(varn: VarnType): NpcVariableTypeIntDelegate<NpcUid?> {
    val fromType: (NpcUid?) -> Int = { typed -> typed?.packed ?: NpcUid.NULL.packed }
    return typeIntVarn(varn, ::NpcUid, fromType)
}

public fun typePlayerUidVarp(varn: VarnType): NpcVariableTypeIntDelegate<PlayerUid?> {
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
    private val baseVar: VarnType
        get() = varnbit.baseVar

    private val bitRange: IntRange
        get() = varnbit.bits

    public operator fun getValue(thisRef: Npc, property: KProperty<*>): Int {
        val mappedValue = thisRef.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return extracted
    }

    public operator fun setValue(thisRef: Npc, property: KProperty<*>, value: Int) {
        val mappedValue = thisRef.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, value)
        thisRef.vars[baseVar] = packedValue
    }
}

public class NpcVariableTypeIntBitsDelegate<T>(
    private val varnbit: VarnBitType,
    public val toType: (Int) -> T,
    public val fromType: (T) -> Int,
) {
    private val baseVar: VarnType
        get() = varnbit.baseVar

    private val bitRange: IntRange
        get() = varnbit.bits

    public operator fun getValue(thisRef: Npc, property: KProperty<*>): T {
        val mappedValue = thisRef.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return toType(extracted)
    }

    public operator fun setValue(thisRef: Npc, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        val mappedValue = thisRef.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, varValue)
        thisRef.vars[baseVar] = packedValue
    }
}

/* Utility functions */
private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

private fun boolFromInt(int: Int): Boolean = int == 1
