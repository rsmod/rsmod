package org.rsmod.api.player.vars

import kotlin.reflect.KProperty
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
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): VariableTypeIntDelegate<T> = VariableTypeIntDelegate(varp, toType, fromType)

public fun <T> typeStrVarp(
    varp: VarpType,
    toType: (String?) -> T,
    fromType: (T) -> String,
): VariableTypeStringDelegate<T> = VariableTypeStringDelegate(varp, toType, fromType)

/* Varbit delegates */
public fun intVarp(varBit: VarBitType): VariableIntBitsDelegate = VariableIntBitsDelegate(varBit)

public fun boolVarp(varBit: VarBitType): VariableTypeIntBitsDelegate<Boolean> =
    typeIntVarp(varBit, ::boolFromInt, ::boolToInt)

public fun <T> typeIntVarp(
    varBit: VarBitType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): VariableTypeIntBitsDelegate<T> = VariableTypeIntBitsDelegate(varBit, toType, fromType)

/* Delegate implementations */
public class VariableIntDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): Int {
        return thisRef.vars[varp]
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: Int) {
        thisRef.syncVarp(varp, value)
    }
}

public class VariableTypeIntDelegate<T>(
    private val varp: VarpType,
    public val toType: (Int?) -> T,
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
}

public class VariableTypeIntBitsDelegate<T>(
    private val varbit: VarBitType,
    public val toType: (Int?) -> T,
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
}

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
            thisRef.syncVarpStr(varp, null)
        } else {
            val varValue = fromType(value)
            thisRef.syncVarpStr(varp, varValue)
        }
    }
}

/* Utility functions */
private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

private fun boolFromInt(int: Int?): Boolean = int == 1
