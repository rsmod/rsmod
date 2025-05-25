package org.rsmod.api.combat.commons.npc

import kotlin.math.max
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.varns
import org.rsmod.api.npc.apPlayer2
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.api.npc.opPlayer2
import org.rsmod.api.npc.vars.intVarn
import org.rsmod.api.npc.vars.typePlayerUidVarn
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList

private var Npc.lastCombat: Int by intVarn(varns.lastcombat)
private var Npc.aggressivePlayer by typePlayerUidVarn(varns.aggressive_player)
private var Npc.attackingPlayer by typePlayerUidVarn(varns.attacking_player)

public fun Npc.canRetaliate(): Boolean {
    if (actionDelay + constants.combat_activecombat_delay < currentMapClock) {
        return true
    }
    return mode != NpcMode.OpPlayer2 && mode != NpcMode.ApPlayer2 && mode != NpcMode.PlayerEscape
}

public fun Npc.queueCombatRetaliate(source: Player, delay: Int = 1) {
    queue(queues.com_retaliate_player, delay)
    aggressivePlayer = source.uid
    lastCombat = max(lastCombat, currentMapClock)
}

public fun Npc.combatDefaultRetaliateOp(interactions: AiPlayerInteractions) {
    if (!canRetaliate()) {
        return
    }
    val target = interactions.resolvePlayer(aggressivePlayer) ?: return
    attackingPlayer = target.uid
    actionDelay = currentMapClock + (attackRate() / 2)
    retaliate(target, interactions, ap = false)
}

public fun Npc.combatDefaultRetaliateAp(interactions: AiPlayerInteractions) {
    if (!canRetaliate()) {
        return
    }
    val target = interactions.resolvePlayer(aggressivePlayer) ?: return
    attackingPlayer = target.uid
    actionDelay = currentMapClock + (attackRate() / 2)
    retaliate(target, interactions, ap = true)
}

private fun Npc.retaliate(target: Player, interactions: AiPlayerInteractions, ap: Boolean) {
    when {
        hitpoints <= param(params.retreat) -> {
            playerEscape(target)
        }
        visType.wanderRange > 0 && !target.isWithinDistance(spawnCoords, aggressionRange()) -> {
            playerEscape(target)
        }
        ap -> {
            apPlayer2(target, interactions)
        }
        else -> {
            opPlayer2(target, interactions)
        }
    }
}

public fun Npc.combatPlayDefendAnim(clientDelay: Int = 0) {
    val defendAnim = visType.paramOrNull(params.defend_anim)
    if (defendAnim != null) {
        anim(defendAnim, delay = clientDelay)
    }
}

public fun Npc.combatPlayDefendSpot(objTypes: ObjTypeList, ammo: ObjType?, clientDelay: Int) {
    val type = ammo?.let(objTypes::get) ?: return
    if (!type.isCategoryType(categories.javelin)) {
        return
    }
    spotanim(spotanims.ballista_special, delay = clientDelay, height = 146)
}

public fun Npc.attackRate(): Int = visType.param(params.attackrate)

public fun Npc.aggressionRange(): Int = visType.maxRange + visType.attackRange

public fun Npc.resolveCombatXpMultiplier(): Double = combatXpMultiplier / 1000.0
