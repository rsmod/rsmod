package org.runerealm.api.spells.attack

import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player

public interface SpellAttack {
    public suspend fun ProtectedAccess.attack(target: Npc, attack: CombatAttack.Spell)

    public suspend fun ProtectedAccess.attack(target: Player, attack: CombatAttack.Spell)
}

public suspend fun SpellAttack.attack(
    access: ProtectedAccess,
    target: Player,
    attack: CombatAttack.Spell,
): Unit = access.attack(target, attack)

public suspend fun SpellAttack.attack(
    access: ProtectedAccess,
    target: Npc,
    attack: CombatAttack.Spell,
): Unit = access.attack(target, attack)
