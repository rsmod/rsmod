package org.runerealm.content.other.special.attacks

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import jakarta.inject.Inject
import kotlin.reflect.KClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.commons.CombatStance
import org.runerealm.api.combat.commons.styles.MeleeAttackStyle
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.weapon.scripts.WeaponAttackStylesScript
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.combat.weapon.types.AttackTypes
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.walktriggers
import org.runerealm.api.freeze.FreezeScript
import org.runerealm.api.freeze.isFreezeImmune
import org.runerealm.api.freeze.isFrozen
import org.runerealm.api.freeze.remainingFreezeCycles
import org.runerealm.api.hit.plugin.NpcHitScript
import org.runerealm.api.hit.plugin.PlayerHitScript
import org.runerealm.api.player.isProtectionPrayerDisabled
import org.runerealm.api.specials.SpecialAttack
import org.runerealm.api.specials.SpecialAttackModule as ApiSpecialAttackModule
import org.runerealm.api.specials.SpecialAttackRegistry
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.factory.npcTypeFactory
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.other.special.attacks.configs.special_objs
import org.runerealm.content.other.special.attacks.configs.special_spots
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.npc.NpcMode
import org.runerealm.game.inv.InvObj
import org.runerealm.game.spot.EntitySpotanim
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.walktrig.isType
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript

@Execution(ExecutionMode.SAME_THREAD)
class CombatSpecialAttacksTest {
    @Test
    fun GameTestState.`spec weapons are registered with their energy requirements`() =
        runSpecialsTest { deps ->
            fun assertEnergy(obj: ObjType, expected: Int) {
                val special = deps.registry[InvObj(obj)]
                assertNotNull(special)
                val energy =
                    when (special) {
                        is SpecialAttack.Melee -> special.energyInHundreds
                        is SpecialAttack.Ranged -> special.energyInHundreds
                        else -> error("Unexpected special type for $obj: $special")
                    }
                assertEquals(expected, energy)
            }

            assertEnergy(special_objs.dragon_dagger, 250)
            assertEnergy(special_objs.dragon_dagger_p, 250)
            assertEnergy(special_objs.dragon_dagger_p_plus, 250)
            assertEnergy(special_objs.dragon_dagger_p_plus_plus, 250)
            assertEnergy(special_objs.abyssal_whip, 500)
            assertEnergy(special_objs.volcanic_abyssal_whip, 500)
            assertEnergy(special_objs.frozen_abyssal_whip, 500)
            assertEnergy(special_objs.dragon_scimitar, 550)
            assertEnergy(special_objs.dragon_scimitar_or, 550)
            assertEnergy(special_objs.dragon_mace, 250)
            assertEnergy(special_objs.armadyl_godsword, 500)
            assertEnergy(special_objs.armadyl_godsword_or, 500)
            assertEnergy(special_objs.bandos_godsword, 500)
            assertEnergy(special_objs.bandos_godsword_or, 500)
            assertEnergy(special_objs.saradomin_godsword, 500)
            assertEnergy(special_objs.saradomin_godsword_or, 500)
            assertEnergy(special_objs.zamorak_godsword, 500)
            assertEnergy(special_objs.zamorak_godsword_or, 500)
            assertEnergy(special_objs.dragon_warhammer, 500)
            assertEnergy(special_objs.dragon_warhammer_cr, 500)
            assertEnergy(special_objs.dragon_claws, 500)
            assertEnergy(special_objs.dragon_claws_cr, 500)
            assertEnergy(special_objs.magic_shortbow, 550)
            assertEnergy(special_objs.magic_shortbow_i, 500)
        }

    @Test
    fun GameTestState.`dragon dagger hits twice`() = runSpecialsTest { deps ->
        val npc = spawnDummy()
        random.next = HIT_ROLL
        random.then = 10
        random.then = HIT_ROLL
        random.then = 7
        useMeleeSpecial(deps, special_objs.dragon_dagger, npc, MeleeAttackType.Stab)
        advance(ticks = 1)
        assertEquals(DUMMY_HITPOINTS - 17, npc.hitpoints)
    }

    @Test
    fun GameTestState.`dragon warhammer drains defence by 30 percent on hit`() =
        runSpecialsTest { deps ->
            val npc = spawnDummy()
            random.next = HIT_ROLL
            random.then = 5
            useMeleeSpecial(deps, special_objs.dragon_warhammer, npc, MeleeAttackType.Crush)
            assertEquals(DUMMY_DEFENCE - (DUMMY_DEFENCE * 30) / 100, npc.defenceLvl)
        }

    @Test
    fun GameTestState.`dragon warhammer does not drain defence on miss`() =
        runSpecialsTest { deps ->
            val npc = spawnDummy()
            random.next = MISS_ROLL
            useMeleeSpecial(deps, special_objs.dragon_warhammer, npc, MeleeAttackType.Crush)
            assertEquals(DUMMY_DEFENCE, npc.defenceLvl)
        }

    @Test
    fun GameTestState.`bandos godsword drains defence then strength by damage dealt`() =
        runSpecialsTest { deps ->
            val npc = spawnDummy()
            random.next = HIT_ROLL
            random.then = DUMMY_DEFENCE + 7
            useMeleeSpecial(deps, special_objs.bandos_godsword, npc, MeleeAttackType.Slash)
            assertEquals(0, npc.defenceLvl)
            assertEquals(DUMMY_STRENGTH - 7, npc.strengthLvl)
        }

    @Test
    fun GameTestState.`dragon claws cascade from the second hit`() = runSpecialsTest { deps ->
        val npc = spawnDummy()
        random.next = MISS_ROLL
        random.then = HIT_ROLL
        random.then = 20
        useMeleeSpecial(deps, special_objs.dragon_claws, npc, MeleeAttackType.Slash)
        advance(ticks = 1)
        assertEquals(DUMMY_HITPOINTS - 20, npc.hitpoints)
        advance(ticks = 1)
        // 0, 20, 10, 11
        assertEquals(DUMMY_HITPOINTS - 41, npc.hitpoints)
    }

    @Test
    fun GameTestState.`zamorak godsword freezes the target on a successful hit`() =
        runSpecialsTest { deps ->
            val npc = spawnDummy()
            advance()
            random.next = HIT_ROLL
            random.then = 5
            useMeleeSpecial(deps, special_objs.zamorak_godsword, npc, MeleeAttackType.Slash)
            assertTrue(npc.isFrozen())
            assertEquals(ICE_CLEAVE_FREEZE_CYCLES, npc.remainingFreezeCycles())
            val spotanims = npc.pendingSpotanims.map { EntitySpotanim(it).id }
            assertEquals(listOf(special_spots.ice_barrage_impact.id), spotanims)

            advance(ticks = ICE_CLEAVE_FREEZE_CYCLES - 1)
            assertTrue(npc.isFrozen())
            advance()
            assertFalse(npc.isFrozen())
            assertTrue(npc.isFreezeImmune())
        }

    @Test
    fun GameTestState.`zamorak godsword does not freeze on a miss`() = runSpecialsTest { deps ->
        val npc = spawnDummy()
        advance()
        random.next = MISS_ROLL
        useMeleeSpecial(deps, special_objs.zamorak_godsword_or, npc, MeleeAttackType.Slash)
        assertFalse(npc.isFrozen())
        assertTrue(npc.pendingSpotanims.isEmpty())
    }

    @Test
    fun GameTestState.`zamorak godsword does not refresh an existing freeze`() =
        runSpecialsTest { deps ->
            val npc = spawnDummy()
            advance()
            random.next = HIT_ROLL
            random.then = 5
            useMeleeSpecial(deps, special_objs.zamorak_godsword, npc, MeleeAttackType.Slash)
            advance(ticks = 10)

            random.next = HIT_ROLL
            random.then = 5
            useMeleeSpecial(deps, special_objs.zamorak_godsword, npc, MeleeAttackType.Slash)
            assertEquals(ICE_CLEAVE_FREEZE_CYCLES - 10, npc.remainingFreezeCycles())
        }

    @Test
    fun GameTestState.`zamorak godsword freeze stops a player target from walking`() =
        runSpecialsTest { deps ->
            val target = spawnPlayerTarget()
            advance()
            random.next = HIT_ROLL
            random.then = 5
            useMeleeSpecial(deps, special_objs.zamorak_godsword, target, MeleeAttackType.Slash)
            assertTrue(target.isFrozen())
            assertTrue(target.walkTrigger.isType(walktriggers.pvp_frozen))

            val start = target.coords
            target.moveGameClick(start.translateX(3))
            advance()
            assertEquals(start, target.coords)
        }

    @Test
    fun GameTestState.`dragon scimitar disables protection prayers on a successful hit`() =
        runSpecialsTest { deps ->
            val target = spawnPlayerTarget()
            target.setVarBit(varbits.protect_from_melee, 1)
            target.setVarBit(varbits.piety, 1)
            target.overheadIcon = constants.overhead_protect_from_melee
            advance()

            random.next = HIT_ROLL
            random.then = 3
            useMeleeSpecial(deps, special_objs.dragon_scimitar, target, MeleeAttackType.Slash)
            assertEquals(0, target.vars[varbits.protect_from_melee])
            assertEquals(1, target.vars[varbits.piety])
            assertNull(target.overheadIcon)
            assertTrue(target.isProtectionPrayerDisabled())

            advance(ticks = SEVER_DISABLE_CYCLES - 1)
            assertTrue(target.isProtectionPrayerDisabled())
            advance()
            assertFalse(target.isProtectionPrayerDisabled())
        }

    @Test
    fun GameTestState.`dragon scimitar does not disable protection prayers on a miss`() =
        runSpecialsTest { deps ->
            val target = spawnPlayerTarget()
            target.setVarBit(varbits.protect_from_melee, 1)
            advance()

            random.next = MISS_ROLL
            useMeleeSpecial(deps, special_objs.dragon_scimitar_or, target, MeleeAttackType.Slash)
            assertEquals(1, target.vars[varbits.protect_from_melee])
            assertFalse(target.isProtectionPrayerDisabled())
        }

    private fun GameTestScope.spawnPlayerTarget(): Player {
        val target = registerPlayer(CoordGrid(0, 50, 50, 0, 0))
        target.setBaseLevel(stats.prayer, 99)
        target.setCurrentLevel(stats.prayer, 99)
        player.placeAt(target.coords.translateZ(1))
        player.stats[stats.attack] = 99
        player.stats[stats.strength] = 99
        return target
    }

    private fun GameTestScope.spawnDummy(): Npc {
        val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), dummy)
        player.placeAt(npc.coords.translateZ(1))
        player.stats[stats.attack] = 99
        player.stats[stats.strength] = 99
        return npc
    }

    private fun GameTestScope.useMeleeSpecial(
        deps: SpecialsTestDependencies,
        weapon: ObjType,
        target: PathingEntity,
        type: MeleeAttackType,
    ) {
        val special = deps.registry[InvObj(weapon)]
        check(special is SpecialAttack.Melee) { "Expected melee special for $weapon: $special" }
        val attack =
            CombatAttack.Melee(
                InvObj(weapon),
                type,
                MeleeAttackStyle.Accurate,
                CombatStance.Stance1,
            )
        player.withProtectedAccess { special.attack(this, target, attack) }
    }

    private fun GameTestState.runSpecialsTest(
        testBody: GameTestScope.(SpecialsTestDependencies) -> Unit
    ) =
        runInjectedGameTest(
            SpecialsTestDependencies::class,
            SpecialsTestModule,
            specialAttackScript,
            NpcHitScript::class,
            PlayerHitScript::class,
            FreezeScript::class,
            WeaponAttackStylesScript::class,
            testBody = testBody,
        )

    class SpecialsTestDependencies @Inject constructor(val registry: SpecialAttackRegistry)

    private object SpecialsTestModule : AbstractModule() {
        override fun configure() {
            install(ApiSpecialAttackModule())
            install(SpecialAttackModule())
            bind(AttackTypes::class.java).`in`(Scopes.SINGLETON)
            bind(AttackStyles::class.java).`in`(Scopes.SINGLETON)
        }
    }

    private companion object {
        /** A roll of `0` always passes the accuracy check (`hitChance > roll`). */
        private const val HIT_ROLL = 0

        /** The highest possible roll, which fails any accuracy check below 100%. */
        private const val MISS_ROLL = 9_999

        private const val DUMMY_HITPOINTS = 200
        private const val DUMMY_DEFENCE = 50
        private const val DUMMY_STRENGTH = 60

        private const val ICE_CLEAVE_FREEZE_CYCLES = 32
        private const val SEVER_DISABLE_CYCLES = 8

        /** `SpecialAttackScript` is internal to `api:specials`; it registers every special map. */
        @Suppress("UNCHECKED_CAST")
        private val specialAttackScript: KClass<out PluginScript> =
            Class.forName("org.runerealm.api.specials.scripts.SpecialAttackScript").kotlin
                as KClass<out PluginScript>

        private val dummy =
            npcTypeFactory.create {
                name = "Dummy"
                size = 1
                hitpoints = DUMMY_HITPOINTS
                defence = DUMMY_DEFENCE
                strength = DUMMY_STRENGTH
                defaultMode = NpcMode.None
            }
    }
}
