package org.rsmod.api.specials

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

public sealed class SpecialAttack {
    public data class Instant(val energyInHundreds: Int, val special: InstantSpecialAttack) :
        SpecialAttack() {
        public suspend fun activate(access: ProtectedAccess) {
            with(special) { access.activate() }
        }
    }

    public sealed class Combat : SpecialAttack() {
        public abstract val energyInHundreds: Int
    }

    public data class Melee(override val energyInHundreds: Int, val special: MeleeSpecialAttack) :
        Combat() {
        public suspend fun attack(
            access: ProtectedAccess,
            target: PathingEntity,
            attack: CombatAttack.Melee,
        ): Boolean = with(special) { access.attack(target, attack) }
    }

    public data class Ranged(override val energyInHundreds: Int, val special: RangedSpecialAttack) :
        Combat() {
        public suspend fun attack(
            access: ProtectedAccess,
            target: PathingEntity,
            attack: CombatAttack.Ranged,
        ): Boolean = with(special) { access.attack(target, attack) }
    }
}

public fun interface InstantSpecialAttack {
    public suspend fun ProtectedAccess.activate()
}

public interface CombatSpecialAttack

public fun interface MeleeSpecialAttack : CombatSpecialAttack {
    public suspend fun ProtectedAccess.attack(
        target: PathingEntity,
        attack: CombatAttack.Melee,
    ): Boolean

    public fun interface PlayerSpecific {
        public suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean
    }

    public fun interface NpcSpecific {
        public suspend fun ProtectedAccess.attack(target: Npc, attack: CombatAttack.Melee): Boolean
    }

    public companion object {
        public fun from(player: PlayerSpecific, npc: NpcSpecific): MeleeSpecialAttack =
            MeleeSpecialAttack { entity, attack ->
                when (entity) {
                    is Npc -> with(npc) { attack(entity, attack) }
                    is Player -> with(player) { attack(entity, attack) }
                }
            }
    }
}

public fun interface RangedSpecialAttack : CombatSpecialAttack {
    public suspend fun ProtectedAccess.attack(
        target: PathingEntity,
        attack: CombatAttack.Ranged,
    ): Boolean

    public fun interface PlayerSpecific {
        public suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Ranged,
        ): Boolean
    }

    public fun interface NpcSpecific {
        public suspend fun ProtectedAccess.attack(target: Npc, attack: CombatAttack.Ranged): Boolean
    }

    public companion object {
        public fun from(player: PlayerSpecific, npc: NpcSpecific): RangedSpecialAttack =
            RangedSpecialAttack { entity, attack ->
                when (entity) {
                    is Npc -> with(npc) { attack(entity, attack) }
                    is Player -> with(player) { attack(entity, attack) }
                }
            }
    }
}
