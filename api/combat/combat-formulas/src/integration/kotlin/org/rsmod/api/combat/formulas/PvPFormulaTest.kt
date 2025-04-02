package org.rsmod.api.combat.formulas

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatStance
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.formulas.accuracy.magic.PvPMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.PvPMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.PvPRangedAccuracy
import org.rsmod.api.combat.formulas.maxhit.magic.PvPMagicMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.ranged.PvPRangedMaxHit
import org.rsmod.api.combat.weapon.scripts.WeaponAttackStylesScript
import org.rsmod.api.combat.weapon.scripts.WeaponAttackTypesScript
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.combat.weapon.types.AttackTypes
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.vars.VarPlayerIntMapSetter
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.api.testing.scope.GameTestScope
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.varbit.VarBitType

class PvPFormulaTest {
    @TestWithArgs(MatchupProvider::class)
    fun `calculate matchup combat rolls`(matchup: Matchup, state: GameTestState) =
        state.runInjectedGameTest(
            TestDependencies::class,
            TestModule,
            WeaponAttackStylesScript::class,
            WeaponAttackTypesScript::class,
        ) {
            val attacker = registerPlayer().also { a -> copy(a, matchup.attacker) }
            val defender = registerPlayer().also { d -> copy(d, matchup.defender) }

            val attackerResults = attacker.calculateRolls(it, defender, matchup.attacker)
            val defenderResults = defender.calculateRolls(it, attacker, matchup.defender)

            assertEquals(matchup.attackerRolls.accuracy, attackerResults.accuracy / 100.0)
            assertEquals(matchup.attackerRolls.maxHit, attackerResults.maxHit)

            assertEquals(matchup.defenderRolls.accuracy, defenderResults.accuracy / 100.0)
            assertEquals(matchup.defenderRolls.maxHit, defenderResults.maxHit)
        }

    private fun Player.calculateRolls(
        deps: TestDependencies,
        defender: Player,
        mp: Matchup.MatchupPlayer,
    ): Result {
        val accuracyBoost = mp.specialAccuracy
        val damageBoost = mp.specialDamage
        val spell = mp.castSpell

        val attackType = deps.types.get(this)
        val attackStyle = deps.styles.get(this)
        return when {
            spell != null -> {
                val baseMaxHit = checkNotNull(mp.spellMaxHit)
                val accuracy =
                    deps.magicAccuracy.getSpellHitChance(
                        this,
                        defender,
                        spell,
                        mp.spellbook,
                        usedSunfireRune = false,
                    )
                val maxHit =
                    deps.magicMaxHit.getSpellMaxHit(
                        this,
                        defender,
                        spell,
                        mp.spellbook,
                        baseMaxHit,
                        usedSunfireRune = false,
                    )
                Result(accuracy, maxHit.last)
            }
            attackType?.isMagic == true -> {
                val magicAttackStyle = MagicAttackStyle.from(attackStyle)
                val baseMaxHit = checkNotNull(mp.staffMaxHit)
                val accuracy =
                    deps.magicAccuracy.getStaffHitChance(
                        this,
                        defender,
                        magicAttackStyle,
                        accuracyBoost,
                    )
                val maxHit =
                    deps.magicMaxHit.getStaffMaxHit(this, defender, baseMaxHit, damageBoost)
                Result(accuracy, maxHit)
            }
            attackType?.isRanged == true -> {
                val rangedAttackType = RangedAttackType.from(attackType)
                val rangedAttackStyle = RangedAttackStyle.from(attackStyle)
                val accuracy =
                    deps.rangedAccuracy.getHitChance(
                        this,
                        defender,
                        rangedAttackType,
                        rangedAttackStyle,
                        accuracyBoost,
                    )
                val maxHit =
                    deps.rangedMaxHit.getMaxHit(
                        this,
                        defender,
                        rangedAttackType,
                        rangedAttackStyle,
                        damageBoost,
                        boltSpecDamage = 0,
                    )
                Result(accuracy, maxHit)
            }
            else -> {
                val meleeAttackType = MeleeAttackType.from(attackType)
                val meleeAttackStyle = MeleeAttackStyle.from(attackStyle)
                val accuracy =
                    deps.meleeAccuracy.getHitChance(
                        this,
                        defender,
                        meleeAttackType,
                        meleeAttackStyle,
                        meleeAttackType,
                        accuracyBoost,
                    )
                val maxHit =
                    deps.meleeMaxHit.getMaxHit(
                        this,
                        defender,
                        meleeAttackType,
                        meleeAttackStyle,
                        damageBoost,
                    )
                Result(accuracy, maxHit)
            }
        }
    }

    private data class Result(val accuracy: Int, val maxHit: Int)

    @MatchupDsl
    data class Matchup(val description: String) {
        lateinit var attacker: MatchupPlayer
            private set

        lateinit var defender: MatchupPlayer
            private set

        lateinit var attackerRolls: MatchupRolls
            private set

        lateinit var defenderRolls: MatchupRolls
            private set

        fun setup(init: MatchupSetup.() -> Unit) {
            val setup = MatchupSetup().apply(init)
            attacker = checkNotNull(setup.attacker)
            defender = checkNotNull(setup.defender)
        }

        fun expect(init: MatchupExpect.() -> Unit) {
            val set = MatchupExpect().apply(init)
            attackerRolls = checkNotNull(set.attacker)
            defenderRolls = checkNotNull(set.defender)
        }

        override fun toString(): String = description

        @MatchupDsl data class MatchupRolls(var accuracy: Double? = null, var maxHit: Int? = null)

        @MatchupDsl
        data class MatchupExpect(
            var attacker: MatchupRolls? = null,
            var defender: MatchupRolls? = null,
        ) {
            fun attacker(init: MatchupRolls.() -> Unit) {
                attacker = MatchupRolls().apply(init)
            }

            fun defender(init: MatchupRolls.() -> Unit) {
                defender = MatchupRolls().apply(init)
            }
        }

        @MatchupDsl
        data class MatchupPlayer(
            var stance: CombatStance = CombatStance.Stance1,
            var helm: ObjType? = null,
            var cape: ObjType? = null,
            var amulet: ObjType? = null,
            var ammo: ObjType? = null,
            var weapon: ObjType? = null,
            var body: ObjType? = null,
            var shield: ObjType? = null,
            var legs: ObjType? = null,
            var gloves: ObjType? = null,
            var feet: ObjType? = null,
            var ring: ObjType? = null,
            var attackLvl: Int = 99,
            var defenceLvl: Int = 99,
            var rangedLvl: Int = 99,
            var strengthLvl: Int = 99,
            var magicLvl: Int = 99,
            var hitpoints: Int = 99,
            val vars: MutableMap<VarBitType, Int> = mutableMapOf(),
            var spellbook: Spellbook = Spellbook.Standard,
            var castSpell: ObjType? = null,
            var spellMaxHit: Int? = null,
            var staffMaxHit: Int? = null,
            var specialAccuracy: Double = 1.0,
            var specialDamage: Double = 1.0,
        )

        @MatchupDsl
        data class MatchupSetup(
            var attacker: MatchupPlayer? = null,
            var defender: MatchupPlayer? = null,
        ) {
            fun attacker(init: MatchupPlayer.() -> Unit) {
                attacker = MatchupPlayer().apply(init)
            }

            fun defender(init: MatchupPlayer.() -> Unit) {
                defender = MatchupPlayer().apply(init)
            }
        }

        companion object {
            fun create(description: String, init: Matchup.() -> Unit): Matchup {
                return Matchup(description).apply(init)
            }
        }
    }

    // Note: The dps calculator we used as a reference for these formulae does not support pvp.
    // There is another calculator that does, and their results mostly align - except for magic,
    // where results are slightly skewed (even in pvm). Because of this discrepancy, we currently
    // exclude magic from our test cases until a reliable source becomes available.
    private object MatchupProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                Matchup.create("Unarmed (No Stats) vs Unarmed (No Stats)") {
                    setup {
                        attacker {
                            stance = CombatStance.Stance4 // Unarmed "Defensive" stance.
                            attackLvl = 1
                            defenceLvl = 1
                            strengthLvl = 1
                            hitpoints = 10
                            rangedLvl = 1
                            magicLvl = 1
                        }

                        defender {
                            stance = CombatStance.Stance1 // Unarmed "Accurate" stance.
                            attackLvl = 1
                            defenceLvl = 1
                            strengthLvl = 1
                            hitpoints = 10
                            rangedLvl = 1
                            magicLvl = 1
                        }
                    }

                    expect {
                        attacker {
                            accuracy = 49.91
                            maxHit = 1
                        }

                        defender {
                            accuracy = 49.93
                            maxHit = 1
                        }
                    }
                },
                Matchup.create("Unarmed (No Stats) vs Unarmed (Max Stats)") {
                    setup {
                        attacker {
                            attackLvl = 1
                            defenceLvl = 1
                            strengthLvl = 1
                            hitpoints = 10
                            rangedLvl = 1
                            magicLvl = 1
                        }

                        defender {
                            /* no-op */
                        }
                    }

                    expect {
                        attacker {
                            accuracy = 5.61
                            maxHit = 1
                        }

                        defender {
                            accuracy = 95.90
                            maxHit = 11
                        }
                    }
                },
                Matchup.create("Torva vs Unarmed") {
                    setup {
                        attacker {
                            weapon = objs.armadyl_godsword
                            amulet = objs.amulet_of_fury
                            helm = objs.torva_full_helm
                            body = objs.torva_platebody
                            legs = objs.torva_platelegs
                            cape = objs.infernal_cape
                            feet = objs.primordial_boots
                            gloves = objs.barrows_gloves
                            ring = objs.ultor_ring
                            vars[varbits.piety] = 1
                        }

                        defender {
                            defenceLvl = 118
                            vars[varbits.thick_skin] = 1
                        }
                    }

                    expect {
                        attacker {
                            accuracy = 85.49
                            maxHit = 52
                        }

                        defender {
                            accuracy = 7.02
                            maxHit = 11
                        }
                    }
                },
                Matchup.create("Torva vs Torva") {
                    setup {
                        attacker {
                            weapon = objs.abyssal_whip
                            amulet = objs.amulet_of_fury
                            helm = objs.torva_full_helm
                            body = objs.torva_platebody
                            legs = objs.torva_platelegs
                            shield = objs.dragon_defender
                            cape = objs.infernal_cape
                            feet = objs.primordial_boots
                            gloves = objs.barrows_gloves
                            ring = objs.ultor_ring
                            vars[varbits.piety] = 1
                        }

                        defender {
                            weapon = objs.abyssal_whip
                            amulet = objs.amulet_of_fury
                            helm = objs.torva_full_helm
                            body = objs.torva_platebody
                            legs = objs.torva_platelegs
                            shield = objs.dragon_defender
                            cape = objs.infernal_cape
                            feet = objs.primordial_boots
                            gloves = objs.barrows_gloves
                            ring = objs.ultor_ring
                            vars[varbits.piety] = 1
                        }
                    }

                    expect {
                        attacker {
                            accuracy = 24.49
                            maxHit = 43
                        }

                        defender {
                            accuracy = 24.49
                            maxHit = 43
                        }
                    }
                },
                Matchup.create("Torva vs Tank") {
                    setup {
                        attacker {
                            attackLvl = 118
                            weapon = objs.abyssal_whip
                            amulet = objs.amulet_of_fury
                            helm = objs.torva_full_helm
                            body = objs.torva_platebody
                            legs = objs.torva_platelegs
                            cape = objs.infernal_cape
                            feet = objs.primordial_boots
                            gloves = objs.barrows_gloves
                            ring = objs.ultor_ring
                            vars[varbits.piety] = 1
                        }

                        defender {
                            defenceLvl = 118
                            weapon = objs.dinhs_bulwark
                            amulet = objs.amulet_of_fury
                            helm = objs.justiciar_faceguard
                            body = objs.justiciar_chestguard
                            legs = objs.justiciar_legguards
                            cape = objs.infernal_cape
                            feet = objs.primordial_boots
                            gloves = objs.barrows_gloves
                            ring = objs.ultor_ring
                            vars[varbits.piety] = 1
                        }
                    }

                    expect {
                        attacker {
                            accuracy = 16.44
                            maxHit = 42
                        }

                        defender {
                            accuracy = 27.77
                            maxHit = 40
                        }
                    }
                },
                Matchup.create("Magic shortbow vs Welfare") {
                    setup {
                        attacker {
                            rangedLvl = 112
                            ammo = objs.rune_arrow
                            weapon = objs.magic_shortbow
                            amulet = objs.amulet_of_fury
                            helm = objs.helm_of_neitiznot
                            body = objs.black_dhide_body
                            legs = objs.black_dhide_chaps
                            cape = objs.avas_assembler
                            feet = objs.pegasian_boots
                            gloves = objs.barrows_gloves
                            ring = objs.archers_ring_i
                            vars[varbits.rigour] = 1
                        }

                        defender {
                            defenceLvl = 80
                            ammo = objs.rune_arrow
                            weapon = objs.magic_shortbow
                            amulet = objs.amulet_of_glory_4
                            helm = objs.helm_of_neitiznot
                            body = objs.black_dhide_body
                            legs = objs.rune_platelegs
                            cape = objs.avas_accumulator
                            feet = objs.snakeskin_boots
                            gloves = objs.barrows_gloves
                            vars[varbits.eagle_eye] = 1
                        }
                    }

                    expect {
                        attacker {
                            accuracy = 72.56
                            maxHit = 27
                        }

                        defender {
                            accuracy = 40.41
                            maxHit = 22
                        }
                    }
                },
            )
        }
    }

    private fun GameTestScope.copy(player: Player, matchup: Matchup.MatchupPlayer) {
        player.setVarp(varps.attackstyle, matchup.stance.varValue)

        player.setBaseLevel(stats.attack, matchup.attackLvl)
        player.setBaseLevel(stats.defence, matchup.defenceLvl)
        player.setBaseLevel(stats.ranged, matchup.rangedLvl)
        player.setBaseLevel(stats.strength, matchup.strengthLvl)
        player.setBaseLevel(stats.magic, matchup.magicLvl)
        player.setBaseLevel(stats.hitpoints, matchup.hitpoints)

        player.setCurrentLevel(stats.attack, matchup.attackLvl)
        player.setCurrentLevel(stats.defence, matchup.defenceLvl)
        player.setCurrentLevel(stats.ranged, matchup.rangedLvl)
        player.setCurrentLevel(stats.strength, matchup.strengthLvl)
        player.setCurrentLevel(stats.magic, matchup.magicLvl)
        player.setCurrentLevel(stats.hitpoints, matchup.hitpoints)

        player.worn[Wearpos.Head.slot] = matchup.helm?.let(::InvObj)
        player.worn[Wearpos.Back.slot] = matchup.cape?.let(::InvObj)
        player.worn[Wearpos.Front.slot] = matchup.amulet?.let(::InvObj)
        player.worn[Wearpos.Quiver.slot] = matchup.ammo?.let(::InvObj)
        player.worn[Wearpos.RightHand.slot] = matchup.weapon?.let(::InvObj)
        player.worn[Wearpos.Torso.slot] = matchup.body?.let(::InvObj)
        player.worn[Wearpos.LeftHand.slot] = matchup.shield?.let(::InvObj)
        player.worn[Wearpos.Legs.slot] = matchup.legs?.let(::InvObj)
        player.worn[Wearpos.Hands.slot] = matchup.gloves?.let(::InvObj)
        player.worn[Wearpos.Feet.slot] = matchup.feet?.let(::InvObj)
        player.worn[Wearpos.Ring.slot] = matchup.ring?.let(::InvObj)

        for ((varbit, value) in matchup.vars) {
            VarPlayerIntMapSetter.set(player, varbit, value)
        }
    }

    @DslMarker private annotation class MatchupDsl

    private class TestDependencies
    @Inject
    constructor(
        val meleeMaxHit: PvPMeleeMaxHit,
        val meleeAccuracy: PvPMeleeAccuracy,
        val rangedMaxHit: PvPRangedMaxHit,
        val rangedAccuracy: PvPRangedAccuracy,
        val magicMaxHit: PvPMagicMaxHit,
        val magicAccuracy: PvPMagicAccuracy,
        val types: AttackTypes,
        val styles: AttackStyles,
    )

    private object TestModule : AbstractModule() {
        override fun configure() {
            bind(AttackTypes::class.java).`in`(Scopes.SINGLETON)
            bind(AttackStyles::class.java).`in`(Scopes.SINGLETON)
        }
    }
}
