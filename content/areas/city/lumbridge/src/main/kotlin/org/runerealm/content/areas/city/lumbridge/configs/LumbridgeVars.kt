package org.runerealm.content.areas.city.lumbridge.configs

import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.game.entity.Player

internal var Player.clueScrollDisableVessels by boolVarBit(LumbridgeVarBits.vesseled_clues_disabled)

internal object LumbridgeVarBits : VarBitReferences() {
    val vesseled_clues_disabled = find("vesseled_clues_disabled", 56930885826258)
}
