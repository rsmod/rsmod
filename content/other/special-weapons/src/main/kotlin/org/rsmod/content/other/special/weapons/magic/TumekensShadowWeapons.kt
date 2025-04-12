package org.rsmod.content.other.special.weapons.magic

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.CombatChargeManager
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.projanims
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varobjs
import org.rsmod.api.obj.charges.ObjChargeManager.Companion.isFailure
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.weapons.MagicWeapon
import org.rsmod.api.weapons.WeaponAttackManager
import org.rsmod.api.weapons.WeaponMap
import org.rsmod.api.weapons.WeaponRepository
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player

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

            anim(seqs.tumekens_shadow_cast)
            spotanim(spotanims.tumekens_shadow_launch)

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
                    hitSpot = spotanims.tumekens_shadow_hit,
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
