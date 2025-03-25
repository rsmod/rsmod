package org.rsmod.content.other.special.weapons.ranged

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.RangedAmmoManager
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.projanims
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.quiver
import org.rsmod.api.weapons.RangedWeapon
import org.rsmod.api.weapons.WeaponAttackManager
import org.rsmod.api.weapons.WeaponMap
import org.rsmod.api.weapons.WeaponRepository
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.spot.SpotanimType

class DarkBowWeapons
@Inject
constructor(private val objTypes: ObjTypeList, private val ammunition: RangedAmmoManager) :
    WeaponMap {
    override fun WeaponRepository.register(manager: WeaponAttackManager) {
        register(objs.dark_bow, DarkBow(manager, ammunition, objTypes))
        register(objs.dark_bow_green, DarkBow(manager, ammunition, objTypes))
        register(objs.dark_bow_blue, DarkBow(manager, ammunition, objTypes))
        register(objs.dark_bow_yellow, DarkBow(manager, ammunition, objTypes))
        register(objs.dark_bow_white, DarkBow(manager, ammunition, objTypes))
        register(objs.dark_bow_bh, DarkBow(manager, ammunition, objTypes))
    }

    private class DarkBow(
        private val manager: WeaponAttackManager,
        private val ammunition: RangedAmmoManager,
        private val objTypes: ObjTypeList,
    ) : RangedWeapon {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Ranged,
        ): Boolean {
            shoot(target, attack)
            return true
        }

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Ranged,
        ): Boolean {
            shoot(target, attack)
            return true
        }

        private fun ProtectedAccess.shoot(target: PathingEntity, attack: CombatAttack.Ranged) {
            val righthandType = objTypes[attack.weapon]
            val quiverType = objTypes.getOrNull(player.quiver)

            val canUseAmmo = ammunition.attemptAmmoUsage(player, righthandType, quiverType)
            if (!canUseAmmo) {
                manager.clearCombat(this)
                return
            }

            // All valid ammunition requires a `proj_travel` param to build the projectiles.
            val travelSpotanim = quiverType?.paramOrNull(params.proj_travel)
            if (travelSpotanim == null) {
                manager.clearCombat(this)
                mes("Your ammunition appears to be stuck.")
                return
            }

            val launchSpotanim = quiverType.paramOrNull(params.proj_launch)
            val quiverCount = player.quiver?.count ?: 0

            if (quiverCount == 1) {
                shootSingleArrow(target, attack, quiverType, launchSpotanim, travelSpotanim)
                manager.continueCombat(this, target)
                return
            }

            if (quiverCount >= 2) {
                val doubleLaunchSpotanim =
                    quiverType.paramOrNull(params.proj_launch_double) ?: launchSpotanim
                shootDoubleArrow(target, attack, quiverType, doubleLaunchSpotanim, travelSpotanim)
                manager.continueCombat(this, target)
                return
            }
        }

        private fun ProtectedAccess.shootSingleArrow(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
            quiverType: UnpackedObjType,
            launchSpot: SpotanimType?,
            travelSpot: SpotanimType,
        ) {
            anim(seqs.human_bow_attack)
            soundSynth(synths.darkbow_fire)
            spotanim(launchSpot, height = 96, slot = constants.spotanim_slot_combat)

            val projanim = manager.spawnProjectile(this, target, travelSpot, projanims.arrow)

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = projanim.serverCycles,
            )

            val damage = manager.rollRangedDamage(this, target, attack)
            manager.queueRangedProjectileHit(this, target, quiverType, damage, projanim)
        }

        private fun ProtectedAccess.shootDoubleArrow(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
            quiverType: UnpackedObjType,
            launchSpot: SpotanimType?,
            travelSpot: SpotanimType,
        ) {
            anim(seqs.human_bow_attack)
            soundSynth(synths.darkbow_doublefire)
            spotanim(launchSpot, height = 96, slot = constants.spotanim_slot_combat)

            val proj1 = manager.spawnProjectile(this, target, travelSpot, projanims.doublearrow_one)
            val proj2 = manager.spawnProjectile(this, target, travelSpot, projanims.doublearrow_two)
            val hitDelay1 = proj1.serverCycles
            val hitDelay2 = proj2.serverCycles

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = hitDelay1,
            )

            val damage1 = manager.rollRangedDamage(this, target, attack)
            manager.queueRangedHit(this, target, quiverType, damage1, proj2.clientCycles, hitDelay1)

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = hitDelay2,
            )

            val damage2 = manager.rollRangedDamage(this, target, attack)
            manager.queueRangedDamage(this, target, quiverType, damage2, hitDelay2)

            if (player.quiver?.count == 1) {
                mes("You now have only 1 arrow left in your quiver.")
            }
        }
    }
}
