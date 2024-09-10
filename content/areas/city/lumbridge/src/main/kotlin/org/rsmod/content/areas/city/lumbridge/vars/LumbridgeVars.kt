package org.rsmod.content.areas.city.lumbridge.vars

import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.game.entity.Player

internal var Player.clueScrollDisableVessels by
    boolVarp(LumbridgeVarBits.skill_challenge_disable_vessels)

internal object LumbridgeVarBits : VarBitReferences() {
    val skill_challenge_disable_vessels = find(250910356)
}
