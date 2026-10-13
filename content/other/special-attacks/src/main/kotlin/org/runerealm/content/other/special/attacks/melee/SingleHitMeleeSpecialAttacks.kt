package org.runerealm.content.other.special.attacks.melee

import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.config.constants
import org.runerealm.api.player.disableProtectionPrayers
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
import org.runerealm.game.type.seq.SeqType
import org.runerealm.game.type.spot.SpotanimType

/**
 * Melee special attacks that consist of a single hit with modified accuracy and/or max hit.
 * - Dragon scimitar "Sever": 25% increased accuracy. A successful hit on a player deactivates their
 *   protection prayers and stops them from being used for 8 cycles (4.8 seconds).
 * - Dragon mace "Shatter": 25% increased accuracy and 50% increased max hit.
 */
class SingleHitMeleeSpecialAttacks : SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        val sever =
            SingleHit(
                manager = manager,
                seq = special_seqs.dragon_scimitar,
                spot = special_spots.dragon_scimitar,
                accuracyMultiplier = 1.25,
                maxHitMultiplier = 1.0,
                protectionPrayerDisableCycles = SEVER_PROTECTION_PRAYER_DISABLE_CYCLES,
            )
        registerMelee(special_objs.dragon_scimitar, sever)
        registerMelee(special_objs.dragon_scimitar_or, sever)

        val shatter =
            SingleHit(
                manager = manager,
                seq = special_seqs.dragon_mace,
                spot = special_spots.dragon_mace,
                accuracyMultiplier = 1.25,
                maxHitMultiplier = 1.5,
            )
        registerMelee(special_objs.dragon_mace, shatter)
    }

    private class SingleHit(
        private val manager: SpecialAttackManager,
        private val seq: SeqType,
        private val spot: SpotanimType,
        private val accuracyMultiplier: Double,
        private val maxHitMultiplier: Double,
        private val protectionPrayerDisableCycles: Int = 0,
    ) : MeleeSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Melee,
        ): Boolean {
            hit(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Melee,
        ): Boolean {
            val damage = hit(target, attack)
            // Only a successful (accurate) hit has the effect; accurate hits deal at least 1.
            if (protectionPrayerDisableCycles > 0 && damage > 0) {
                target.disableProtectionPrayers(protectionPrayerDisableCycles)
            }
            return true
        }

        private fun ProtectedAccess.hit(target: PathingEntity, attack: CombatAttack.Melee): Int {
            anim(seq)
            spotanim(spot = spot, slot = constants.spotanim_slot_combat, height = 96)

            val damage =
                manager.rollMeleeDamage(
                    source = this,
                    target = target,
                    attack = attack,
                    accuracyMultiplier = accuracyMultiplier,
                    maxHitMultiplier = maxHitMultiplier,
                )
            manager.giveCombatXp(this, target, attack, damage)
            manager.queueMeleeHit(this, target, damage)
            manager.continueCombat(this, target)
            return damage
        }
    }

    private companion object {
        private const val SEVER_PROTECTION_PRAYER_DISABLE_CYCLES = 8
    }
}
