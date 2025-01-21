package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.game.entity.Player

internal var Player.clueScrollDisableVessels by
    boolVarp(LumbridgeVarBits.skill_challenge_disable_vessels)

internal object LumbridgeVarBits : VarBitReferences() {
    val skill_challenge_disable_vessels = find("skill_challenge_disable_vessels", 2125764051)
}
