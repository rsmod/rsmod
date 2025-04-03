package org.rsmod.api.combat.player

import org.rsmod.api.combat.commons.CombatStance
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.player.vars.typeNpcUidVarp
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcUid

internal val ProtectedAccess.spellbook by enumVarBit<Spellbook>(varbits.spellbook)
internal var ProtectedAccess.autocastEnabled by boolVarBit(varbits.autocast_enabled)
internal val ProtectedAccess.autocastSpell by intVarBit(varbits.autocast_spell)
internal val ProtectedAccess.defensiveCasting by boolVarBit(varbits.defensive_casting_mode)
internal var ProtectedAccess.combatStance by enumVarp<CombatStance>(varps.attackstyle)

internal var ProtectedAccess.aggressiveNpc: NpcUid? by typeNpcUidVarp(varps.aggressive_npc)

internal var ProtectedAccess.specialAttackType by enumVarp<SpecialAttackType>(varps.sa_type)

internal var Player.lastCombat: Int by intVarp(varps.lastcombat)
internal var Player.aggressiveNpc: NpcUid? by typeNpcUidVarp(varps.aggressive_npc)
