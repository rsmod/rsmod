package org.rsmod.api.player.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Controller
import org.rsmod.game.type.varcon.VarConType
import org.rsmod.game.type.varconbit.VarConBitType
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits
import org.rsmod.utils.time.InlineLocalDateTime

/* Varcon delegates */
public fun intVarCon(varcon: VarConType): ControllerVariableIntDelegate =
    ControllerVariableIntDelegate(varcon)

public fun boolVarCon(varcon: VarConType): ControllerVariableTypeIntDelegate<Boolean> =
    typedIntVarCon(varcon, ::boolFromInt, ::boolToInt)

/**
 * Important to note that this local date time variant does not allow for years before
 * [org.rsmod.utils.time.InlineLocalDateTime.YEAR_OFFSET].
 *
 * @see [org.rsmod.utils.time.InlineLocalDateTime.YEAR_OFFSET]
 */
public fun dateVarCon(varcon: VarConType): ControllerVariableLocalDateTimeDelegate =
    ControllerVariableLocalDateTimeDelegate(varcon)

public fun <T> typedIntVarCon(
    varcon: VarConType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): ControllerVariableTypeIntDelegate<T> =
    ControllerVariableTypeIntDelegate(varcon, toType, fromType)

/* Varconbit delegates */
public fun intVarCon(varconbit: VarConBitType): ControllerVariableIntBitsDelegate =
    ControllerVariableIntBitsDelegate(varconbit)

public fun boolVarCon(varconbit: VarConBitType): ControllerVariableTypeIntBitsDelegate<Boolean> =
    typedIntVarCon(varconbit, ::boolFromInt, ::boolToInt)

public fun <T> typedIntVarCon(
    varconbit: VarConBitType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): ControllerVariableTypeIntBitsDelegate<T> =
    ControllerVariableTypeIntBitsDelegate(varconbit, toType, fromType)

/* Delegate implementations */
public class ControllerVariableIntDelegate(private val varcon: VarConType) {
    public operator fun getValue(thisRef: Controller, property: KProperty<*>): Int {
        return thisRef.vars[varcon]
    }

    public operator fun setValue(thisRef: Controller, property: KProperty<*>, value: Int) {
        thisRef.vars[varcon] = value
    }
}

public class ControllerVariableTypeIntDelegate<T>(
    private val varcon: VarConType,
    public val toType: (Int?) -> T,
    public val fromType: (T) -> Int,
) {
    public operator fun getValue(thisRef: Controller, property: KProperty<*>): T {
        val varValue = thisRef.vars[varcon]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Controller, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.vars.remove(varcon)
        } else {
            val varValue = fromType(value)
            thisRef.vars[varcon] = varValue
        }
    }
}

public class ControllerVariableIntBitsDelegate(private val varbit: VarConBitType) {
    private val baseVar: VarConType
        get() = varbit.baseVar

    private val bitRange: IntRange
        get() = varbit.bits

    public operator fun getValue(thisRef: Controller, property: KProperty<*>): Int {
        val mappedValue = thisRef.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return extracted
    }

    public operator fun setValue(thisRef: Controller, property: KProperty<*>, value: Int) {
        val mappedValue = thisRef.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, value)
        thisRef.vars[baseVar] = packedValue
    }
}

public class ControllerVariableTypeIntBitsDelegate<T>(
    private val varbit: VarConBitType,
    public val toType: (Int?) -> T,
    public val fromType: (T) -> Int,
) {
    private val baseVar: VarConType
        get() = varbit.baseVar

    private val bitRange: IntRange
        get() = varbit.bits

    public operator fun getValue(thisRef: Controller, property: KProperty<*>): T {
        val mappedValue = thisRef.vars[baseVar]
        val extracted = mappedValue.getBits(bitRange)
        return toType(extracted)
    }

    public operator fun setValue(thisRef: Controller, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        val mappedValue = thisRef.vars[baseVar]
        val packedValue = mappedValue.withBits(bitRange, varValue)
        thisRef.vars[baseVar] = packedValue
    }
}

public class ControllerVariableLocalDateTimeDelegate(private val varcon: VarConType) {
    public operator fun getValue(thisRef: Controller, property: KProperty<*>): InlineLocalDateTime {
        val packed = thisRef.vars[varcon]
        return InlineLocalDateTime(packed)
    }

    public operator fun setValue(
        thisRef: Controller,
        property: KProperty<*>,
        value: InlineLocalDateTime,
    ) {
        if (value == InlineLocalDateTime.NULL) {
            thisRef.vars[varcon] = 0
        } else {
            thisRef.vars[varcon] = value.packed
        }
    }
}

/* Utility functions */
private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

private fun boolFromInt(int: Int?): Boolean = int == 1
