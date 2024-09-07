package org.rsmod.api.player.vars

import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType

/* Start of varplayer delegates */

public fun intVarp(varp: VarpType): VariableIntDelegate = varpIntDelegate(varp)

public fun strVarp(varp: VarpType): VariableStringDelegate = varpStringDelegate(varp)

public fun boolVarp(varp: VarpType): VariableTypeIntDelegate<Boolean> =
    typeIntVarp(varp, ::boolFromInt, ::boolToInt)

/**
 * Important to note that this local date time variant does not allow for years before
 * [org.rsmod.utils.time.InlineLocalDateTime.YEAR_OFFSET].
 *
 * @see [org.rsmod.utils.time.InlineLocalDateTime.YEAR_OFFSET]
 */
public fun dateVarp(varp: VarpType): VariableLocalDateTimeDelegate = varpDateDelegate(varp)

public fun <T> typeIntVarp(
    varp: VarpType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): VariableTypeIntDelegate<T> = varpIntTypedDelegate(varp, toType, fromType)

public fun <T> typeStrVarp(
    varp: VarpType,
    toType: (String?) -> T,
    fromType: (T) -> String,
): VariableTypeStringDelegate<T> = varpStringTypedDelegate(varp, toType, fromType)

/* Start of varbit delegates */

public fun intVarp(varBit: VarBitType): VariableIntBitsDelegate = varpIntDelegate(varBit)

public fun boolVarp(varBit: VarBitType): VariableTypeIntBitsDelegate<Boolean> =
    typeIntVarp(varBit, ::boolFromInt, ::boolToInt)

public fun <T> typeIntVarp(
    varBit: VarBitType,
    toType: (Int?) -> T,
    fromType: (T) -> Int,
): VariableTypeIntBitsDelegate<T> = varpIntTypedDelegate(varBit, toType, fromType)

/* Start of utility functions */

private fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

private fun boolFromInt(int: Int?): Boolean = int == 1
