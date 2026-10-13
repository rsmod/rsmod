package org.runerealm.content.other.special.weapons.magic

import jakarta.inject.Inject
import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.manager.CombatChargeManager
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.projanims
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.spotanims
import org.runerealm.api.config.refs.synths
import org.runerealm.api.config.refs.varobjs
import org.runerealm.api.obj.charges.ObjChargeManager.Companion.isFailure
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.magicLvl
import org.runerealm.api.weapons.MagicWeapon
import org.runerealm.api.weapons.WeaponAttackManager
import org.runerealm.api.weapons.WeaponMap
import org.runerealm.api.weapons.WeaponRepository
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player

class TumekensShadowWeapons @Inject constructor(private val charges: CombatChargeManager) :
    WeaponMap {
    override fun WeaponRepository.register(manager: WeaponAttackManager) {
        register(objs.tumekens_shadow_uncharged, UnchargedTumekensShadow(manager))
        register(objs.tumekens_shadow, TumekensShadow(manager, charges))
    }

    private class TumekensShadow(
        private val manager: WeaponAttackManager,
        private val charges: CombatChargeManager,
    ) : MagicWeapon {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Staff,
        ): Boolean {
            cast(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Staff,
        ): Boolean {
            cast(target, attack)
            return true
        }

        private fun ProtectedAccess.cast(target: PathingEntity, attack: CombatAttack.Staff) {
            val chargeResult = charges.attemptDetractWeapon(player, varobjs.tumeken_charges)
            if (chargeResult.isFailure()) {
                manager.stopCombat(this)
                return
            }
            // Tumeken's Shadow has an attack rate of 5, unlike standard powered staves which
            // typically have a rate of 4.
            manager.setNextAttackDelay(this, 5)

            anim(seqs.toa_sot_cast_b)
            spotanim(spotanims.tumekens_shadow_casting)

            val proj =
                manager.spawnProjectile(
                    source = this,
                    target = target,
                    spotanim = spotanims.tumekens_shadow_travel,
                    projanim = projanims.tumekens_shadow,
                )
            val (serverDelay, clientDelay) = proj.durations

            val castSound = synths.toa_shadow_weapon_cast_fire_01
            val splash = manager.rollStaffSplash(this, target, attack)
            if (splash) {
                manager.playSplashFx(this, target, clientDelay, castSound, soundRadius = 10)
                manager.queueSplashHit(this, target, clientDelay, serverDelay)
            } else {
                val baseMaxHit = getMaxHit(player.magicLvl)
                val damage = manager.rollStaffMaxHit(this, target, baseMaxHit)
                manager.playMagicHitFx(
                    source = this,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    soundRadius = 10,
                    hitSpot = spotanims.tumekens_shadow_impact,
                    hitSpotHeight = 124,
                    hitSound = synths.contact_darkness_impact,
                )
                manager.giveCombatXp(this, target, attack, damage)
                manager.queueMagicHit(this, target, damage, clientDelay, serverDelay)
            }

            if (chargeResult.fullyUncharged) {
                manager.stopCombat(this)
                mes("Tumeken's shadow has run out of charges.")
                return
            }
            manager.continueCombat(this, target)
        }

        private fun getMaxHit(magicLvl: Int): Int = (magicLvl / 3) + 1
    }

    private class UnchargedTumekensShadow(private val manager: WeaponAttackManager) : MagicWeapon {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Staff,
        ): Boolean {
            terminateAttack()
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Staff,
        ): Boolean {
            terminateAttack()
            return true
        }

        private fun ProtectedAccess.terminateAttack() {
            mes(
                "Tumeken's Shadow has no charges! You need to " +
                    "charge it with soul runes and chaos runes."
            )
            manager.stopCombat(this)
        }
    }
}
