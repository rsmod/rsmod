package org.rsmod.api.player.vars

import org.rsmod.api.player.output.VarpSync
import org.rsmod.game.entity.Player
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.utils.bits.withBits

/**
 * Utility object for performing **dynamic** player var operations.
 *
 * The recommended approach for getting and setting player vars is to use the delegates from
 * `VarPlayerTypeDelegates.kt`. However, those delegates do not support cases where
 * dynamically-resolved varbit and varp types must be used.
 *
 * ### When to Use:
 * If a varp or varbit type needs to be resolved dynamically (e.g., retrieved from a function)
 * before being set for a player, this utility provides a way to do so.
 *
 * #### Example Usage:
 * ```
 * val resolvedVarBit = resolveVarBitType(...)
 *
 * player.vars[resolvedVarBit] = 1 // Not allowed - `VarPlayerIntMap` does not support set operator.
 *
 * VarPlayerIntMapSetter.set(player, resolvedVarBit, 1) // Allowed
 * ```
 *
 * @see [VarPlayerIntMap]
 */
public object VarPlayerIntMapSetter {
    public fun set(player: Player, varp: VarpType, value: Int) {
        val previous = player.vars.backing[varp.id]

        player.vars.backing[varp.id] = value

        val engineLoggedIn = player.processedMapClock > 0
        if (!engineLoggedIn) {
            return
        }

        val transmit = varp.transmit
        if (transmit.always) {
            VarpSync.writeVarp(player, varp, value)
        } else if (transmit.onDiff && previous != value) {
            VarpSync.writeVarp(player, varp, value)
        }
    }

    public fun set(player: Player, varbit: VarBitType, value: Int) {
        VarPlayerIntMap.assertVarBitBounds(varbit, value)
        val mappedValue = player.vars[varbit.baseVar]
        val packedValue = mappedValue.withBits(varbit.bits, value)
        set(player, varbit.baseVar, packedValue)
    }
}
