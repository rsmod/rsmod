package org.rsmod.api.specials.combat

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public interface CombatSpecialAttack<T : CombatAttack> {
    public suspend fun ProtectedAccess.attack(target: Npc, attack: T): Boolean

    public suspend fun ProtectedAccess.attack(target: Player, attack: T): Boolean
}

public interface MeleeSpecialAttack : CombatSpecialAttack<CombatAttack.Melee>

public interface RangedSpecialAttack : CombatSpecialAttack<CombatAttack.Ranged>

public interface MagicSpecialAttack : CombatSpecialAttack<CombatAttack.Staff>
