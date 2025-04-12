package org.rsmod.content.other.special.attacks.ranged

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.manager.RangedAmmoManager
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.projanims
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.quiver
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.api.specials.combat.RangedSpecialAttack
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.spot.SpotanimType

class DarkBowSpecialAttack
@Inject
constructor(private val objTypes: ObjTypeList, private val ammunition: RangedAmmoManager) :
    SpecialAttackMap {
    override fun SpecialAttackRepository.register(manager: SpecialAttackManager) {
        registerRanged(objs.dark_bow, DarkBow(manager, ammunition, objTypes))
        registerRanged(objs.dark_bow_green, DarkBow(manager, ammunition, objTypes))
        registerRanged(objs.dark_bow_blue, DarkBow(manager, ammunition, objTypes))
        registerRanged(objs.dark_bow_yellow, DarkBow(manager, ammunition, objTypes))
        registerRanged(objs.dark_bow_white, DarkBow(manager, ammunition, objTypes))
        registerRanged(objs.dark_bow_bh, DarkBow(manager, ammunition, objTypes))
    }

    private class DarkBow(
        private val manager: SpecialAttackManager,
        private val ammunition: RangedAmmoManager,
        private val objTypes: ObjTypeList,
    ) : RangedSpecialAttack {
        override suspend fun ProtectedAccess.attack(
            target: Npc,
            attack: CombatAttack.Ranged,
        ): Boolean = selectAndShootSpecial(target, attack)

        override suspend fun ProtectedAccess.attack(
            target: Player,
            attack: CombatAttack.Ranged,
        ): Boolean = selectAndShootSpecial(target, attack)

        private fun ProtectedAccess.selectAndShootSpecial(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
        ): Boolean {
            val righthandType = objTypes[attack.weapon]
            val quiverType = objTypes.getOrNull(player.quiver)

            val canUseAmmo = ammunition.attemptAmmoUsage(player, righthandType, quiverType)
            if (!canUseAmmo) {
                manager.stopCombat(this)
                return false
            }

            // All valid ammunition requires a `proj_travel` param to build the projectiles.
            val travelSpotanim = quiverType?.paramOrNull(params.proj_travel)
            if (travelSpotanim == null) {
                manager.stopCombat(this)
                mes("You are unable to fire your ammunition.")
                return false
            }

            val quiverCount = player.quiver?.count ?: 0
            if (quiverCount < 2) {
                manager.stopCombat(this)
                mes("You need to have at least 2 arrows in your quiver for this special attack.")
                return false
            }

            val descentOfDragons = quiverType.isCategoryType(categories.dragon_arrow)
            if (descentOfDragons) {
                descentOfDragons(target, attack, quiverType, travelSpotanim)
                manager.continueCombat(this, target)
                return true
            }

            descentOfDarkness(target, attack, quiverType, travelSpotanim)
            manager.continueCombat(this, target)
            return true
        }

        private fun ProtectedAccess.descentOfDarkness(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
            quiverType: UnpackedObjType,
            travelSpot: SpotanimType,
        ) {
            val launchSpot = quiverType.paramOrNull(params.proj_launch_double)
            anim(seqs.human_bow_attack)
            soundSynth(synths.darkbow_doublefire)
            soundSynth(synths.darkbow_shadow_attack)
            spotanim(launchSpot, height = 96, slot = constants.spotanim_slot_combat)

            val descentTravel = spotanims.darkbow_shadow_travel
            val descentImpact = spotanims.darkbow_shadow_impact
            val impactSynth = synths.darkbow_shadow_impact

            manager.spawnProjectile(this, target, descentTravel, projanims.doublearrow_one)
            val proj1 = manager.spawnProjectile(this, target, travelSpot, projanims.doublearrow_one)
            val clientDelay1 = proj1.clientCycles
            manager.soundArea(target, impactSynth, delay = clientDelay1, radius = 10)

            manager.spawnProjectile(this, target, descentTravel, projanims.doublearrow_two)
            val proj2 = manager.spawnProjectile(this, target, travelSpot, projanims.doublearrow_two)
            val clientDelay2 = proj2.clientCycles
            manager.soundArea(target, impactSynth, delay = clientDelay2, radius = 10)

            target.spotanim(descentImpact, height = 96, delay = clientDelay2)

            val damage =
                calculateDamage(target, attack, damageRange = 5..Int.MAX_VALUE, maxHitBoost = 30)
            val hitDelay1 = proj1.serverCycles
            val hitDelay2 = proj2.serverCycles

            manager.giveCombatXp(this, target, attack, damage.total)

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = hitDelay1,
            )

            manager.queueRangedHit(this, target, quiverType, damage[0], clientDelay2, hitDelay1)

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = hitDelay2,
            )

            manager.queueRangedDamage(this, target, quiverType, damage[1], hitDelay2)

            if (player.quiver?.count == 1) {
                mes("You now have only 1 arrow left in your quiver.")
            }
        }

        private fun ProtectedAccess.descentOfDragons(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
            quiverType: UnpackedObjType,
            travelSpot: SpotanimType,
        ) {
            val launchSpot = quiverType.paramOrNull(params.proj_launch_double)
            anim(seqs.human_bow_attack)
            soundSynth(synths.darkbow_doublefire)
            soundSynth(synths.darkbow_dragon_attack)
            spotanim(launchSpot, height = 96, slot = constants.spotanim_slot_combat)

            val descentTravel = spotanims.darkbow_dragon_travel
            val descentImpact = spotanims.darkbow_dragon_impact
            val impactSynth = synths.darkbow_shadow_impact

            manager.spawnProjectile(this, target, descentTravel, projanims.doublearrow_one)
            val proj1 = manager.spawnProjectile(this, target, travelSpot, projanims.doublearrow_one)
            val clientDelay1 = proj1.clientCycles
            manager.soundArea(target, impactSynth, delay = clientDelay1, radius = 10)

            manager.spawnProjectile(this, target, descentTravel, projanims.doublearrow_two)
            val proj2 = manager.spawnProjectile(this, target, travelSpot, projanims.doublearrow_two)
            val clientDelay2 = proj2.clientCycles
            manager.soundArea(target, impactSynth, delay = clientDelay2, radius = 10)

            target.spotanim(descentImpact, height = 96, delay = clientDelay2)

            val damage = calculateDamage(target, attack, damageRange = 8..48, maxHitBoost = 50)
            val hitDelay1 = proj1.serverCycles
            val hitDelay2 = proj2.serverCycles

            manager.giveCombatXp(this, target, attack, damage.total)

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = hitDelay1,
            )

            manager.queueRangedHit(this, target, quiverType, damage[0], clientDelay2, hitDelay1)

            ammunition.useQuiverAmmo(
                player = player,
                quiverType = quiverType,
                dropCoord = target.coords,
                dropDelay = hitDelay2,
            )

            manager.queueRangedDamage(this, target, quiverType, damage[1], hitDelay2)

            if (player.quiver?.count == 1) {
                mes("You now have only 1 arrow left in your quiver.")
            }
        }

        private fun ProtectedAccess.calculateDamage(
            target: PathingEntity,
            attack: CombatAttack.Ranged,
            damageRange: IntRange,
            maxHitBoost: Int,
        ): DescentHit {
            val damage =
                manager.calculateRangedMaxHit(
                    source = this,
                    target = target,
                    attackType = attack.type,
                    attackStyle = attack.style,
                    percentBoost = maxHitBoost,
                    boltSpecDamage = 0,
                )
            val first = random.of(0..damage).coerceIn(damageRange)
            val second = random.of(0..damage).coerceIn(damageRange)
            return DescentHit(first, second)
        }

        private data class DescentHit(val first: Int, val second: Int) {
            val total: Int
                get() = first + second

            operator fun get(index: Int): Int =
                when (index) {
                    0 -> first
                    1 -> second
                    else -> throw ArrayIndexOutOfBoundsException()
                }
        }
    }
}
