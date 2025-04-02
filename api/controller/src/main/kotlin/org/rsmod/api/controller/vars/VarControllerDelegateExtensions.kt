package org.rsmod.api.controller.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Controller
import org.rsmod.game.type.varcon.VarConType
import org.rsmod.game.type.varconbit.VarConBitType
import org.rsmod.utils.time.InlineLocalDateTime

/* Varcon delegates */
public fun intVarCon(varcon: VarConType): ControllerVariableIntDelegate =
    ControllerVariableIntDelegate(varcon)

public fun boolVarCon(varcon: VarConType): ControllerVariableTypeIntDelegate<Boolean> =
    typeIntVarCon(varcon, ::boolFromInt, ::boolToInt)

/**
 * Important to note that this local date time variant does not allow for years before
 * [InlineLocalDateTime.YEAR_OFFSET].
 *
 * @see [InlineLocalDateTime.YEAR_OFFSET]
 */
public fun dateVarCon(varcon: VarConType): ControllerVariableLocalDateTimeDelegate =
    ControllerVariableLocalDateTimeDelegate(varcon)

public fun <T> typeIntVarCon(
    varcon: VarConType,
    toType: (Int) -> T,
    fromType: (T) -> Int,
): ControllerVariableTypeIntDelegate<T> =
    ControllerVariableTypeIntDelegate(varcon, toType, fromType)

/* Varconbit delegates */
public fun intVarConBit(varconbit: VarConBitType): ControllerVariableIntBitsDelegate =
    ControllerVariableIntBitsDelegate(varconbit)

public fun boolVarConBit(varconbit: VarConBitType): ControllerVariableTypeIntBitsDelegate<Boolean> =
    typeIntVarConBit(varconbit, ::boolFromInt, ::boolToInt)

public fun <T> typeIntVarConBit(
    varconbit: VarConBitType,
    toType: (Int) -> T,
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
    public val toType: (Int) -> T,
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

public class ControllerVariableIntBitsDelegate(private val varconbit: VarConBitType) {
    public operator fun getValue(thisRef: Controller, property: KProperty<*>): Int {
        return thisRef.vars[varconbit]
    }

    public operator fun setValue(thisRef: Controller, property: KProperty<*>, value: Int) {
        thisRef.vars[varconbit] = value
    }
}

public class ControllerVariableTypeIntBitsDelegate<T>(
    private val varconbit: VarConBitType,
    public val toType: (Int) -> T,
    public val fromType: (T) -> Int,
) {
    public operator fun getValue(thisRef: Controller, property: KProperty<*>): T {
        val varValue = thisRef.vars[varconbit]
        return toType(varValue)
    }

    public operator fun setValue(thisRef: Controller, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        thisRef.vars[varconbit] = varValue
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
