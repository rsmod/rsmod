package org.rsmod.api.spells.attack

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

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
