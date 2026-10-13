package org.runerealm.api.combat.player

import org.runerealm.api.combat.commons.CombatStance
import org.runerealm.api.combat.commons.magic.Spellbook
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.enumVarBit
import org.runerealm.api.player.vars.enumVarp
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.player.vars.typeNpcUidVarp
import org.runerealm.api.player.vars.typePlayerUidVarp
import org.runerealm.api.specials.SpecialAttackType
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.npc.NpcUid
import org.runerealm.game.entity.player.PlayerUid

internal val ProtectedAccess.spellbook by enumVarBit<Spellbook>(varbits.spellbook)
internal var ProtectedAccess.autocastEnabled by boolVarBit(varbits.autocast_set)
internal val ProtectedAccess.autocastSpell by intVarBit(varbits.autocast_spell)
internal val ProtectedAccess.defensiveCasting by boolVarBit(varbits.autocast_defmode)
internal var ProtectedAccess.combatStance by enumVarp<CombatStance>(varps.com_mode)

internal var ProtectedAccess.aggressiveNpc: NpcUid? by typeNpcUidVarp(varps.aggressive_npc)
internal var ProtectedAccess.pkPrey1: PlayerUid? by typePlayerUidVarp(varps.pk_prey1)
internal var ProtectedAccess.pkPrey2: PlayerUid? by typePlayerUidVarp(varps.pk_prey2)
internal var ProtectedAccess.pkPredator1: PlayerUid? by typePlayerUidVarp(varps.pk_predator1)

internal var ProtectedAccess.specialAttackType by enumVarp<SpecialAttackType>(varps.sa_attack)

internal var Player.lastCombat: Int by intVarp(varps.lastcombat)
internal var Player.lastCombatPvp: Int by intVarp(varps.lastcombat_pvp)
internal var Player.aggressiveNpc: NpcUid? by typeNpcUidVarp(varps.aggressive_npc)
internal var Player.pkPredator1: PlayerUid? by typePlayerUidVarp(varps.pk_predator1)
internal var Player.pkPredator2: PlayerUid? by typePlayerUidVarp(varps.pk_predator2)
internal var Player.pkPredator3: PlayerUid? by typePlayerUidVarp(varps.pk_predator3)
