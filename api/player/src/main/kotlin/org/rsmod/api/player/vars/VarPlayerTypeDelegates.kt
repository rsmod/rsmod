package org.rsmod.api.player.vars

import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries
import kotlin.reflect.KProperty
import org.rsmod.api.player.output.VarpSync
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.utils.vars.VarEnumDelegate
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits
import org.rsmod.utils.time.InlineLocalDateTime

/* Varplayer delegates */
public fun intVarp(varp: VarpType): VariableIntDelegate = VariableIntDelegate(varp)

public fun strVarp(varp: VarpType): VariableStringDelegate = VariableStringDelegate(varp)

public fun boolVarp(varp: VarpType): VariableTypeIntDelegate<Boolean> =
    typeIntVarp(varp, ::boolFromInt, ::boolToInt)

/**
 * Important to note that this local date time variant does not allow for years before
 * [org.rsmod.utils.time.InlineLocalDateTime.YEAR_OFFSET].
 *
 * @see [org.rsmod.utils.time.InlineLocalDateTime.YEAR_OFFSET]
 */
public fun dateVarp(varp: VarpType): VariableLocalDateTimeDelegate =
    VariableLocalDateTimeDelegate(varp)

public fun <T> typeIntVarp(
    varp: VarpType,
    toType: (Int) -> T,
    fromType: (T) -> Int,
): VariableTypeIntDelegate<T> = VariableTypeIntDelegate(varp, toType, fromType)

public fun <T> typeStrVarp(
    varp: VarpType,
    toType: (String?) -> T,
    fromType: (T) -> String,
): VariableTypeStringDelegate<T> = VariableTypeStringDelegate(varp, toType, fromType)

public inline fun <reified V> enumVarp(
    varp: VarpType,
    entries: EnumEntries<V> = enumEntries(),
    default: V = entries.firstOrNull { it.varValue == 0 } ?: entries.first(),
): VariableTypeIntDelegate<V> where V : Enum<V>, V : VarEnumDelegate {
    val toType: (Int) -> V = { varValue ->
        entries.firstOrNull { varValue == it.varValue } ?: default
    }
    val fromType: (V) -> Int = { typed -> typed.varValue }
    return VariableTypeIntDelegate(varp, toType, fromType)
}

public inline fun <reified V> enumVarpOrNull(
    varp: VarpType,
    entries: EnumEntries<V> = enumEntries(),
    nullVarValue: Int = 0,
): VariableTypeIntDelegate<V?> where V : Enum<V>, V : VarEnumDelegate {
    val invalidEntry = entries.firstOrNull { it.varValue == nullVarValue }
    require(invalidEntry == null) {
        "Entry found with default var value ($nullVarValue), " +
            "consider using `enumVarp` instead: $invalidEntry"
    }

    val toType: (Int) -> V? = { varValue -> entries.firstOrNull { varValue == it.varValue } }
    val fromType: (V?) -> Int = { typed -> typed?.varValue ?: nullVarValue }
    return VariableTypeIntDelegate(varp, toType, fromType)
}

/* Varbit delegates */
public fun intVarBit(varBit: VarBitType): VariableIntBitsDelegate = VariableIntBitsDelegate(varBit)

public fun boolVarBit(varBit: VarBitType): VariableTypeIntBitsDelegate<Boolean> =
    typeIntVarBit(varBit, ::boolFromInt, ::boolToInt)

public fun <T> typeIntVarBit(
    varBit: VarBitType,
    toType: (Int) -> T,
    fromType: (T) -> Int,
): VariableTypeIntBitsDelegate<T> = VariableTypeIntBitsDelegate(varBit, toType, fromType)

public inline fun <reified V> enumVarBit(
    varBit: VarBitType,
    entries: EnumEntries<V> = enumEntries(),
    default: V = entries.firstOrNull { it.varValue == 0 } ?: entries.first(),
): VariableTypeIntBitsDelegate<V> where V : Enum<V>, V : VarEnumDelegate {
    val toType: (Int) -> V = { varValue ->
        entries.firstOrNull { varValue == it.varValue } ?: default
    }
    val fromType: (V) -> Int = { typed -> typed.varValue }
    return VariableTypeIntBitsDelegate(varBit, toType, fromType)
}

public inline fun <reified V> enumVarBitOrNull(
    varBit: VarBitType,
    entries: EnumEntries<V> = enumEntries(),
    nullVarValue: Int = 0,
): VariableTypeIntBitsDelegate<V?> where V : Enum<V>, V : VarEnumDelegate {
    val invalidEntry = entries.firstOrNull { it.varValue == nullVarValue }
    require(invalidEntry == null) {
        "Entry found with default var value ($nullVarValue), " +
            "consider using `enumVarBit` instead: $invalidEntry"
    }

    val toType: (Int) -> V? = { varValue -> entries.firstOrNull { varValue == it.varValue } }
    val fromType: (V?) -> Int = { typed -> typed?.varValue ?: nullVarValue }
    return VariableTypeIntBitsDelegate(varBit, toType, fromType)
}

/* Delegate implementations */
public class VariableIntDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): Int {
        return thisRef.vars[varp]
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: Int) {
        thisRef.syncVarp(varp, value)
    }

    public operator fun getValue(thisRef: ProtectedAccess, property: KProperty<*>): Int {
        return thisRef.player.vars[varp]
    }

    public operator fun setValue(thisRef: ProtectedAccess, property: KProperty<*>, value: Int) {
        thisRef.syncVarp(varp, value)
    }
}

public class VariableTypeIntDelegate<T>(
    private val varp: VarpType,
    public val toType: (Int) -> T,
    public val fromType: (T) -> Int,
) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): T {
        val varValue = thisRef.vars[varp]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.syncVarp(varp, 0)
        } else {
            val varValue = fromType(value)
            thisRef.syncVarp(varp, varValue)
        }
    }

    public operator fun getValue(thisRef: ProtectedAccess, property: KProperty<*>): T {
        val varValue = thisRef.player.vars[varp]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: ProtectedAccess, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.syncVarp(varp, 0)
        } else {
            val varValue = fromType(value)
            thisRef.syncVarp(varp, varValue)
        }
    }
}

public class VariableIntBitsDelegate(private val varbit: VarBitType) {
    private val baseVar: VarpType
        get() = varbit.baseVar

    private val bitRange: IntRange
        get() = varbit.bits

    public operator fun getValue(thisRef: Player, property: KProperty<*>): Int {
        val mappedValue = thisRef.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return extracted
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: Int) {
        val mappedValue = thisRef.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, value)
        thisRef.syncVarp(baseVar, packedValue)
    }

    public operator fun getValue(thisRef: ProtectedAccess, property: KProperty<*>): Int {
        val mappedValue = thisRef.player.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return extracted
    }

    public operator fun setValue(thisRef: ProtectedAccess, property: KProperty<*>, value: Int) {
        val mappedValue = thisRef.player.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, value)
        thisRef.syncVarp(baseVar, packedValue)
    }
}

public class VariableTypeIntBitsDelegate<T>(
    private val varbit: VarBitType,
    public val toType: (Int) -> T,
    public val fromType: (T) -> Int,
) {
    private val baseVar: VarpType
        get() = varbit.baseVar

    private val bitRange: IntRange
        get() = varbit.bits

    public operator fun getValue(thisRef: Player, property: KProperty<*>): T {
        val mappedValue = thisRef.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return toType(extracted)
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        val mappedValue = thisRef.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, varValue)
        thisRef.syncVarp(baseVar, packedValue)
    }

    public operator fun getValue(thisRef: ProtectedAccess, property: KProperty<*>): T {
        val mappedValue = thisRef.player.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return toType(extracted)
    }

    public operator fun setValue(thisRef: ProtectedAccess, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        val mappedValue = thisRef.player.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, varValue)
        thisRef.syncVarp(baseVar, packedValue)
    }
}

public class VariableLocalDateTimeDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): InlineLocalDateTime {
        val packed = thisRef.vars[varp]
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

    public operator fun getValue(
        thisRef: ProtectedAccess,
        property: KProperty<*>,
    ): InlineLocalDateTime {
        val packed = thisRef.player.vars[varp]
        return InlineLocalDateTime(packed)
    }

    public operator fun setValue(
        thisRef: ProtectedAccess,
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

public class VariableStringDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): String? {
        return thisRef.strVars[varp]
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: String?) {
        thisRef.syncVarpStr(varp, value)
    }

    public operator fun getValue(thisRef: ProtectedAccess, property: KProperty<*>): String? {
        return thisRef.player.strVars[varp]
    }

    public operator fun setValue(thisRef: ProtectedAccess, property: KProperty<*>, value: String?) {
        thisRef.syncVarpStr(varp, value)
    }
}

public class VariableTypeStringDelegate<T>(
    private val varp: VarpType,
    public val toType: (String?) -> T,
    public val fromType: (T) -> String,
) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): T {
        val varValue = thisRef.strVars[varp]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.syncVarpStr(varp, null)
        } else {
            val varValue = fromType(value)
            thisRef.syncVarpStr(varp, varValue)
        }
    }

    public operator fun getValue(thisRef: ProtectedAccess, property: KProperty<*>): T {
        val varValue = thisRef.player.strVars[varp]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: ProtectedAccess, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.syncVarpStr(varp, null)
        } else {
            val varValue = fromType(value)
            thisRef.syncVarpStr(varp, varValue)
        }
    }
}

/* Utility functions */
private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

private fun boolFromInt(int: Int): Boolean = int == 1

private fun Player.syncVarp(varp: VarpType, value: Int) {
    val previous = vars.backing[varp.id]

    vars.backing[varp.id] = value

    val transmit = varp.transmit
    if (transmit.always) {
        VarpSync.writeVarp(this, varp, value)
    } else if (transmit.onDiff && previous != value) {
        VarpSync.writeVarp(this, varp, value)
    }
}

private fun Player.syncVarpStr(varp: VarpType, value: String?) {
    strVars[varp] = value
}

private fun ProtectedAccess.syncVarp(varp: VarpType, value: Int) {
    val previous = player.vars.backing[varp.id]

    player.vars.backing[varp.id] = value

    val transmit = varp.transmit
    if (transmit.always) {
        VarpSync.writeVarp(player, varp, value)
    } else if (transmit.onDiff && previous != value) {
        VarpSync.writeVarp(player, varp, value)
    }
}

private fun ProtectedAccess.syncVarpStr(varp: VarpType, value: String?) {
    player.strVars[varp] = value
}
