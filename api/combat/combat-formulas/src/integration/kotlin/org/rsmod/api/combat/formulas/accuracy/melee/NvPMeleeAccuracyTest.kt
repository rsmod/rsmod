package org.rsmod.api.combat.formulas.accuracy.melee

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import jakarta.inject.Inject
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.weapon.scripts.WeaponAttackStylesScript
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.back
import org.rsmod.api.player.feet
import org.rsmod.api.player.front
import org.rsmod.api.player.hands
import org.rsmod.api.player.hat
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ring
import org.rsmod.api.player.torso
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.game.entity.Npc
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.ParamMap
import org.rsmod.game.type.util.ParamMapBuilder
import org.rsmod.game.type.varbit.VarBitType

class NvPMeleeAccuracyTest {
    @TestWithArgs(MatchupProvider::class)
    fun `calculate matchup hit chance`(matchup: Matchup, state: GameTestState) =
        state.runInjectedGameTest(
            MeleeAccuracyTestDependencies::class,
            MeleeAccuracyTestModule,
            WeaponAttackStylesScript::class,
        ) {
            val accuracy = it.accuracy

            player.setCurrentLevel(stats.defence, matchup.defenceLvl)
            player.setBaseLevel(stats.defence, matchup.baseDefenceLvl)
            player.setCurrentLevel(stats.hitpoints, matchup.hitpoints)
            player.setBaseLevel(stats.hitpoints, matchup.baseHitpointsLvl)

            player.hat = matchup.hat
            player.back = matchup.back
            player.front = matchup.front
            player.righthand = matchup.righthand
            player.torso = matchup.torso
            player.lefthand = matchup.lefthand
            player.legs = matchup.legs
            player.hands = matchup.hands
            player.feet = matchup.feet
            player.ring = matchup.ring

            for (prayer in matchup.prayers) {
                player.setVarBit(prayer, 1)
            }

            val npc = Npc(matchup.npc)
            val accuracyRoll =
                accuracy.getHitChance(npc = npc, target = player, attackType = matchup.attackType)
            assertEquals(matchup.expectedAccuracy, accuracyRoll / 100.0)
        }

    data class Matchup(
        val expectedAccuracy: Double,
        val npc: UnpackedNpcType = man,
        val attackType: MeleeAttackType? = null,
        val hat: InvObj? = null,
        val back: InvObj? = null,
        val front: InvObj? = null,
        val righthand: InvObj? = null,
        val torso: InvObj? = null,
        val lefthand: InvObj? = null,
        val legs: InvObj? = null,
        val hands: InvObj? = null,
        val feet: InvObj? = null,
        val ring: InvObj? = null,
        val defenceLvl: Int = 99,
        val baseDefenceLvl: Int = 99,
        val hitpoints: Int = 99,
        val baseHitpointsLvl: Int = 99,
        val prayers: Set<VarBitType> = emptySet(),
    ) {
        fun withNpcSource(npc: UnpackedNpcType) = copy(npc = npc)

        fun withAttackType(attackType: MeleeAttackType?) = copy(attackType = attackType)

        fun withHelm(obj: ObjType?) = copy(hat = obj?.let(::InvObj))

        fun withCape(obj: ObjType?) = copy(back = obj?.let(::InvObj))

        fun withAmulet(obj: ObjType?) = copy(front = obj?.let(::InvObj))

        fun withWeapon(obj: ObjType?) = copy(righthand = obj?.let(::InvObj))

        fun withBody(obj: ObjType?) = copy(torso = obj?.let(::InvObj))

        fun withShield(obj: ObjType?) = copy(lefthand = obj?.let(::InvObj))

        fun withLegs(obj: ObjType?) = copy(legs = obj?.let(::InvObj))

        fun withGloves(obj: ObjType?) = copy(hands = obj?.let(::InvObj))

        fun withFeet(obj: ObjType?) = copy(feet = obj?.let(::InvObj))

        fun withRing(obj: ObjType?) = copy(ring = obj?.let(::InvObj))

        fun withDefenceLevel(defenceLvl: Int) = copy(defenceLvl = defenceLvl)

        fun withPrayers(vararg prayers: VarBitType) = copy(prayers = prayers.toSet())
    }

    // Each matchup runs a full integrated test scope, meaning it initializes an entire game
    // environment. Avoid excessive matchups to prevent unnecessary overhead.
    private object MatchupProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                Matchup(expectedAccuracy = 4.67)
                    .withNpcSource(man)
                    .withAttackType(MeleeAttackType.Crush),
                Matchup(expectedAccuracy = 19.32)
                    .withNpcSource(dagannothRex)
                    .withAttackType(MeleeAttackType.Slash)
                    .withHelm(objs.torva_full_helm)
                    .withBody(objs.torva_platebody)
                    .withLegs(objs.torva_platelegs)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_rancour)
                    .withGloves(objs.ferocious_gloves)
                    .withFeet(objs.primordial_boots)
                    .withRing(objs.ultor_ring)
                    .withPrayers(varbits.chivalry),
                Matchup(expectedAccuracy = 27.41)
                    .withNpcSource(glod)
                    .withAttackType(MeleeAttackType.Crush)
                    .withHelm(objs.justiciar_faceguard)
                    .withBody(objs.justiciar_chestguard)
                    .withLegs(objs.justiciar_legguards)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_fury)
                    .withGloves(objs.barrows_gloves)
                    .withWeapon(objs.dinhs_bulwark)
                    .withDefenceLevel(defenceLvl = 50),
                Matchup(expectedAccuracy = 1.21)
                    .withNpcSource(abyssalWalker)
                    .withAttackType(MeleeAttackType.Crush)
                    .withHelm(objs.justiciar_faceguard)
                    .withBody(objs.justiciar_chestguard)
                    .withLegs(objs.justiciar_legguards)
                    .withCape(objs.infernal_cape)
                    .withAmulet(objs.amulet_of_fury)
                    .withGloves(objs.barrows_gloves)
                    .withWeapon(objs.dinhs_bulwark)
                    .withDefenceLevel(defenceLvl = 75),
                Matchup(expectedAccuracy = 27.25)
                    .withNpcSource(giantRat)
                    .withAttackType(MeleeAttackType.Slash)
                    .withHelm(objs.helm_of_neitiznot)
                    .withBody(objs.fighter_torso)
                    .withLegs(objs.obsidian_platelegs)
                    .withCape(objs.fire_cape)
                    .withAmulet(objs.amulet_of_fury)
                    .withShield(objs.dragon_defender)
                    .withGloves(objs.barrows_gloves)
                    .withFeet(objs.dragon_boots)
                    .withRing(objs.berserker_ring)
                    .withDefenceLevel(defenceLvl = 80),
                Matchup(expectedAccuracy = 0.28)
                    .withNpcSource(chicken)
                    .withAttackType(MeleeAttackType.Stab)
                    .withHelm(objs.helm_of_neitiznot)
                    .withBody(objs.fighter_torso)
                    .withLegs(objs.obsidian_platelegs)
                    .withCape(objs.fire_cape)
                    .withAmulet(objs.amulet_of_fury)
                    .withShield(objs.dragon_defender)
                    .withGloves(objs.barrows_gloves)
                    .withFeet(objs.dragon_boots)
                    .withRing(objs.berserker_ring)
                    .withDefenceLevel(defenceLvl = 99),
            )
        }
    }

    private class MeleeAccuracyTestDependencies @Inject constructor(val accuracy: NvPMeleeAccuracy)

    private object MeleeAccuracyTestModule : AbstractModule() {
        override fun configure() {
            bind(AttackStyles::class.java).`in`(Scopes.SINGLETON)
        }
    }

    private companion object {
        val man =
            npcTypeFactory.create {
                name = "Man"
                hitpoints = 7
            }

        val dagannothRex =
            npcTypeFactory.create {
                name = "Dagannoth Rex"
                hitpoints = 255
                attack = 255
            }

        val glod =
            npcTypeFactory.create {
                name = "Glod (Hard)"
                hitpoints = 255
                attack = 230
            }

        val abyssalWalker =
            npcTypeFactory.create {
                name = "Abyssal walker"
                hitpoints = 95
                attack = 5
                paramMap = buildParams { this[params.attack_melee] = 5 }
            }

        val giantRat =
            npcTypeFactory.create {
                name = "Giant rat (Scurrius)"
                hitpoints = 15
                attack = 100
                paramMap = buildParams { this[params.attack_melee] = 68 }
            }

        val chicken =
            npcTypeFactory.create {
                name = "Chicken"
                hitpoints = 3
                paramMap = buildParams { this[params.attack_melee] = -47 }
            }

        private fun buildParams(init: ParamMapBuilder.() -> Unit): ParamMap {
            return ParamMapBuilder().apply(init).toParamMap()
        }
    }
}
