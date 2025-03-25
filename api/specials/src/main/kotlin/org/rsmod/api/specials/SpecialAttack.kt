package org.rsmod.api.specials

import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.combat.CombatSpecialAttack
import org.rsmod.api.specials.combat.MagicSpecialAttack
import org.rsmod.api.specials.combat.MeleeSpecialAttack
import org.rsmod.api.specials.combat.RangedSpecialAttack
import org.rsmod.api.specials.instant.InstantSpecialAttack
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

public sealed class SpecialAttack {
    public data class Instant(val energyInHundreds: Int, val special: InstantSpecialAttack) :
        SpecialAttack() {
        public suspend fun activate(access: ProtectedAccess) {
            special.activate(access)
        }
    }

    public sealed class Combat : SpecialAttack()

    public data class Melee(
        public val energyInHundreds: Int,
        public val special: MeleeSpecialAttack,
    ) : Combat() {
        public suspend fun attack(
            access: ProtectedAccess,
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean = special.attack(access, target, attack)

        public suspend fun attack(
            access: ProtectedAccess,
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean = special.attack(access, target, attack)

        public suspend fun attack(
            access: ProtectedAccess,
            target: PathingEntity,
            attack: CombatAttack.Melee,
        ): Boolean =
            when (target) {
                is Npc -> attack(access, target, attack)
                is Player -> attack(access, target, attack)
            }
    }

    public data class Ranged(
        public val energyInHundreds: Int,
        public val special: RangedSpecialAttack,
    ) : Combat() {
        public suspend fun attack(
            access: ProtectedAccess,
            target: Npc,
            attack: CombatAttack.Ranged,
        ): Boolean = special.attack(access, target, attack)

        public suspend fun attack(
            access: ProtectedAccess,
            target: Player,
            attack: CombatAttack.Ranged,
        ): Boolean = special.attack(access, target, attack)

        public suspend fun attack(
            access: ProtectedAccess,
            target: PathingEntity,
            attack: CombatAttack.Ranged,
        ): Boolean =
            when (target) {
                is Npc -> attack(access, target, attack)
                is Player -> attack(access, target, attack)
            }
    }

    public data class Magic(
        public val energyInHundreds: Int,
        public val special: MagicSpecialAttack,
    ) : Combat() {
        public suspend fun attack(
            access: ProtectedAccess,
            target: Npc,
            attack: CombatAttack.Staff,
        ): Boolean = special.attack(access, target, attack)

        public suspend fun attack(
            access: ProtectedAccess,
            target: Player,
            attack: CombatAttack.Staff,
        ): Boolean = special.attack(access, target, attack)

        public suspend fun attack(
            access: ProtectedAccess,
            target: PathingEntity,
            attack: CombatAttack.Staff,
        ): Boolean =
            when (target) {
                is Npc -> attack(access, target, attack)
                is Player -> attack(access, target, attack)
            }
    }
}

private suspend fun InstantSpecialAttack.activate(access: ProtectedAccess) {
    access.activate()
}

private suspend fun <T : CombatAttack> CombatSpecialAttack<T>.attack(
    access: ProtectedAccess,
    target: Npc,
    attack: T,
): Boolean = access.attack(target, attack)

private suspend fun <T : CombatAttack> CombatSpecialAttack<T>.attack(
    access: ProtectedAccess,
    target: Player,
    attack: T,
): Boolean = access.attack(target, attack)
