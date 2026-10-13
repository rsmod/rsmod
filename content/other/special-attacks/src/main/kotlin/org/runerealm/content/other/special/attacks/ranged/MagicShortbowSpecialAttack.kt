package org.runerealm.content.other.special.attacks.ranged

import jakarta.inject.Inject
import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.manager.RangedAmmoManager
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.projanims
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.quiver
import org.runerealm.api.specials.SpecialAttackManager
import org.runerealm.api.specials.SpecialAttackMap
import org.runerealm.api.specials.SpecialAttackRepository
import org.runerealm.api.specials.combat.RangedSpecialAttack
import org.runerealm.content.other.special.attacks.configs.special_objs
import org.runerealm.content.other.special.attacks.configs.special_seqs
import org.runerealm.content.other.special.attacks.configs.special_spots
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjTypeList

/**
 * Magic shortbow and Magic shortbow (i) "Snapshot": fires two arrows in quick succession, each
 * rolling accuracy and damage independently with a `10/7` accuracy multiplier.
 */
class MagicShortbowSpecialAttack
@Inject
constructor(private val objTypes: ObjTypeList, private val ammunition: RangedAmmoManager) :
    SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerRanged(special_objs.magic_shortbow, MagicShortbow(manager, ammunition, objTypes))
        registerRanged(special_objs.magic_shortbow_i, MagicShortbow(manager, ammunition, objTypes))
    }

    private class MagicShortbow(
        private val manager: SpecialAttackManager,
        private val ammunition: RangedAmmoManager,
        private val objTypes: ObjTypeList,
    ) : RangedSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Ranged,
        ): Boolean = snapshot(target, attack)

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Ranged,
        ): Boolean = snapshot(target, attack)

        private fun ProtectedAccess.snapshot(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
        ): Boolean {
            val righthandType = objTypes[attack.weapon]
            val quiverType = objTypes.getOrNull(player.quiver)

            val canUseAmmo = ammunition.attemptAmmoUsage(player, righthandType, quiverType)
            if (!canUseAmmo || quiverType == null) {
                manager.stopCombat(this)
                return false
            }

            val quiverCount = player.quiver?.count ?: 0
            if (quiverCount < 2) {
                manager.stopCombat(this)
                mes("You need to have at least 2 arrows in your quiver for this special attack.")
                return false
            }

            anim(special_seqs.magic_shortbow)
            spotanim(
                spot = special_spots.magic_shortbow_launch,
                height = 96,
                slot = constants.spotanim_slot_combat,
            )

            val travel = special_spots.magic_shortbow_travel
            val proj1 = manager.spawnProjectile(this, target, travel, projanims.doublearrow_one)
            val proj2 = manager.spawnProjectile(this, target, travel, projanims.doublearrow_two)

            val first = rollArrow(target, attack)
            val second = rollArrow(target, attack)
            manager.giveCombatXp(this, target, attack, first + second)

            ammunition.useQuiverAmmo(player, quiverType, target.coords, proj1.serverCycles)
            manager.queueRangedHit(
                source = this,
                target = target,
                ammo = quiverType,
                damage = first,
                clientDelay = proj1.clientCycles,
                hitDelay = proj1.serverCycles,
            )

            ammunition.useQuiverAmmo(player, quiverType, target.coords, proj2.serverCycles)
            manager.queueRangedHit(
                source = this,
                target = target,
                ammo = quiverType,
                damage = second,
                clientDelay = proj2.clientCycles,
                hitDelay = proj2.serverCycles,
            )

            if (player.quiver?.count == 1) {
                mes("You now have only 1 arrow left in your quiver.")
            }

            manager.continueCombat(this, target)
            return true
        }

        private fun ProtectedAccess.rollArrow(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
        ): Int =
            manager.rollRangedDamage(
                source = this,
                target = target,
                attack = attack,
                accuracyMultiplier = ACCURACY_MULTIPLIER,
            )
    }

    private companion object {
        private const val ACCURACY_MULTIPLIER = 10.0 / 7.0
    }
}
