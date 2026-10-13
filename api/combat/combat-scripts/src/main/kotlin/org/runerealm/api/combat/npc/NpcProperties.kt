package org.runerealm.api.combat.npc

import org.runerealm.api.config.refs.varns
import org.runerealm.api.npc.vars.intVarn
import org.runerealm.api.npc.vars.typePlayerUidVarn
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.player.PlayerUid

internal var Npc.lastAttack: Int by intVarn(varns.lastattack)
internal var Npc.attackingPlayer: PlayerUid? by typePlayerUidVarn(varns.attacking_player)

internal var Npc.lastCombat: Int by intVarn(varns.lastcombat)
internal var Npc.aggressivePlayer by typePlayerUidVarn(varns.aggressive_player)
