package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.game.entity.Player

internal var Player.clueScrollDisableVessels by boolVarBit(LumbridgeVarBits.vesseled_clues_disabled)

internal object LumbridgeVarBits : VarBitReferences() {
    val vesseled_clues_disabled = find("vesseled_clues_disabled", 56930885826258)
}
