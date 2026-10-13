package org.runerealm.content.other.special.attacks.melee

import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.config.constants
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.specials.SpecialAttackManager
import org.runerealm.api.specials.SpecialAttackMap
import org.runerealm.api.specials.SpecialAttackRepository
import org.runerealm.api.specials.combat.MeleeSpecialAttack
import org.runerealm.content.other.special.attacks.configs.special_objs
import org.runerealm.content.other.special.attacks.configs.special_seqs
import org.runerealm.content.other.special.attacks.configs.special_spots
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player

/**
 * Dragon dagger "Puncture": two hits in quick succession, each with 15% increased accuracy and 15%
 * increased max hit. Each hit rolls accuracy independently and both land on the same cycle.
 */
class DragonDaggerSpecialAttack : SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerMelee(special_objs.dragon_dagger, DragonDagger(manager))
        registerMelee(special_objs.dragon_dagger_p, DragonDagger(manager))
        registerMelee(special_objs.dragon_dagger_p_plus, DragonDagger(manager))
        registerMelee(special_objs.dragon_dagger_p_plus_plus, DragonDagger(manager))
    }

    private class DragonDagger(private val manager: SpecialAttackManager) : MeleeSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean {
            puncture(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean {
            puncture(target, attack)
            return true
        }

        private fun ProtectedAccess.puncture(target: PathingEntity, attack: CombatAttack.Melee) {
            anim(special_seqs.dragon_dagger)
            spotanim(
                spot = special_spots.dragon_dagger,
                slot = constants.spotanim_slot_combat,
                height = 96,
            )

            val first = rollPuncture(target, attack)
            val second = rollPuncture(target, attack)
            manager.giveCombatXp(this, target, attack, first + second)
            manager.queueMeleeHit(this, target, first)
            manager.queueMeleeHit(this, target, second)
            manager.continueCombat(this, target)
        }

        private fun ProtectedAccess.rollPuncture(
            target: PathingEntity,
            attack: CombatAttack.Melee,
        ): Int =
            manager.rollMeleeDamage(
                source = this,
                target = target,
                attack = attack,
                accuracyMultiplier = ACCURACY_MULTIPLIER,
                maxHitMultiplier = MAX_HIT_MULTIPLIER,
            )
    }

    private companion object {
        private const val ACCURACY_MULTIPLIER = 1.15
        private const val MAX_HIT_MULTIPLIER = 1.15
    }
}
