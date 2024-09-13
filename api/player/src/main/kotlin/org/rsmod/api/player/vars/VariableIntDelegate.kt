package org.rsmod.api.player.vars

import kotlin.reflect.KProperty
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits

public fun varpIntDelegate(varp: VarpType): VariableIntDelegate = VariableIntDelegate(varp)

public fun <T> varpIntTypedDelegate(
    varp: VarpType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): VariableTypeIntDelegate<T> = VariableTypeIntDelegate(varp, toType, fromType)

public fun varpIntDelegate(varbit: VarBitType): VariableIntBitsDelegate =
    VariableIntBitsDelegate(varbit)

public fun <T> varpIntTypedDelegate(
    varbit: VarBitType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): VariableTypeIntBitsDelegate<T> = VariableTypeIntBitsDelegate(varbit, toType, fromType)

public class VariableIntDelegate(private val varp: VarpType) {
    public operator fun getValue(thisRef: Player, property: KProperty<*>): Int {
        return thisRef.vars[varp] ?: return 0
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
        val mappedValue = thisRef.vars[baseVar] ?: return 0
        val extracted = mappedValue.getBits(bitRange)
        return extracted
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: Int) {
        val mappedValue = thisRef.vars[baseVar] ?: 0
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
        val extracted = mappedValue?.getBits(bitRange)
        return toType(extracted)
    }

    public operator fun setValue(thisRef: Player, property: KProperty<*>, value: T?) {
        val varValue = value?.let(fromType) ?: 0
        val mappedValue = thisRef.vars[baseVar] ?: 0
        val packedValue = mappedValue.withBits(bitRange, varValue)
        thisRef.syncVarp(baseVar, packedValue)
    }
}
