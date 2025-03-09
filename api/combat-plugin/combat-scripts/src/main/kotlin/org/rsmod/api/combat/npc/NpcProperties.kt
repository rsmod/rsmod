package org.rsmod.api.combat.npc

import org.rsmod.api.config.refs.varns
import org.rsmod.api.npc.vars.intVarn
import org.rsmod.api.npc.vars.typePlayerUidVarn
import org.rsmod.game.entity.Npc

internal var Npc.lastCombat: Int by intVarn(varns.lastcombat)

internal var Npc.aggressivePlayer by typePlayerUidVarn(varns.aggressive_player)
