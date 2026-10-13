package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.invs
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.death.Gravestones
import org.runerealm.api.death.configs.death_npcs
import org.runerealm.api.death.configs.death_objs
import org.runerealm.api.death.configs.death_varps
import org.runerealm.api.death.plugin.DeathOfficeScript
import org.runerealm.api.death.plugin.GravestoneScript
import org.runerealm.api.death.plugin.PlayerDeathScript
import org.runerealm.api.player.queueDeath
import org.runerealm.api.registry.obj.ObjRegistry
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.inv.InvObj
import org.runerealm.map.CoordGrid

class DeathItemsTest {
    @Test
    fun GameTestState.`pvm death puts lost items in a gravestone`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            pvmDeath(deps)
            // The three most valuable items are kept.
            assertEquals(1, player.count(objs.abyssal_whip))
            assertEquals(1, player.count(objs.rune_platebody))
            assertEquals(1, player.count(objs.bronze_axe))
            assertEquals(0, player.count(objs.coins))
            assertEquals(0, player.count(objs.beer))

            val grave = deps.gravestones.gravestoneInv(player)
            assertEquals(1000, grave.count(objs.coins))
            assertEquals(1, grave.count(objs.beer))
            val npc = checkNotNull(deps.gravestones.graveNpc(player))
            assertEquals(FIELD, npc.coords)
            assertTrue(deps.gravestones.ticksLeft(player) > Gravestones.GRAVESTONE_DURATION - 20)
        }

    @Test
    fun GameTestState.`gravestone timer pauses while logged out`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            pvmDeath(deps)
            eventBus.publish(SessionStateEvent.Logout(player))
            assertNull(deps.gravestones.graveNpc(player))
            val ticks = deps.gravestones.ticksLeft(player)
            advance(ticks = 20)
            assertEquals(ticks, deps.gravestones.ticksLeft(player))

            eventBus.publish(SessionStateEvent.Login(player))
            assertEquals(FIELD, deps.gravestones.graveNpc(player)?.coords)
            advance(ticks = 5)
            assertEquals(ticks - 5, deps.gravestones.ticksLeft(player))
        }

    @Test
    fun GameTestState.`owner can loot their gravestone`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            pvmDeath(deps)
            val npc = checkNotNull(deps.gravestones.graveNpc(player))
            player.telejump(FIELD.translateX(1))
            player.opNpc3(npc)
            advance(ticks = 3)

            assertEquals(1000, player.count(objs.coins))
            assertEquals(1, player.count(objs.beer))
            assertFalse(deps.gravestones.hasGrave(player))
            assertNull(deps.gravestones.graveNpc(player))
        }

    @Test
    fun GameTestState.`looting valuable items from a gravestone costs a fee from the bank`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            player.observerUUID = 1
            player.telejump(FIELD)
            player.skullIcon = constants.skullicon_default
            player.inv[0] = InvObj(objs.abyssal_whip)
            player.queueDeath()
            advanceUntil(predicate = { deps.gravestones.hasGrave(player) })
            val npc = checkNotNull(deps.gravestones.graveNpc(player))
            player.telejump(FIELD.translateX(1))

            // The whip is worth over 100k: 1,000 coins, which the player cannot afford yet.
            player.opNpc3(npc)
            advance(ticks = 3)
            assertEquals(0, player.count(objs.abyssal_whip))
            assertTrue(deps.gravestones.hasGrave(player))

            val bank = player.invMap.getOrPut(cacheTypes.invs[invs.bank])
            bank[0] = InvObj(objs.coins, 1500)
            player.opNpc3(npc)
            advance(ticks = 3)
            assertEquals(1, player.count(objs.abyssal_whip))
            assertEquals(500, bank.count(objs.coins))
            assertFalse(deps.gravestones.hasGrave(player))
        }

    @Test
    fun GameTestState.`expired gravestone items are reclaimed from death`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            pvmDeath(deps)
            player.setVarp(death_varps.gravestone_ticks_left, 2)
            advance(ticks = 3)
            assertFalse(deps.gravestones.hasGrave(player))
            assertNull(deps.gravestones.graveNpc(player))
            val office = deps.gravestones.deathOfficeInv(player)
            assertEquals(1000, office.count(objs.coins))

            val death = spawnNpc(player.coords.translateX(2), npcTypes[death_npcs.death])
            player.opNpc3(death)
            advance(ticks = 3)
            assertEquals(1000, player.count(objs.coins))
            assertEquals(1, player.count(objs.beer))
            assertTrue(office.isEmpty())
        }

    @Test
    fun GameTestState.`pvp death breaks a fire cape and gives the killer its repair cost`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            val killer = pvpSetup()
            player.inv[0] = InvObj(death_objs.fire_cape)
            player.inv[1] = InvObj(objs.abyssal_whip)
            dieToPlayer(killer)

            assertEquals(0, player.count(death_objs.fire_cape))
            assertEquals(1, player.count(death_objs.fire_cape_broken))
            assertEquals(0, player.count(objs.abyssal_whip))
            val drops = deps.groundObjs(WILDERNESS)
            assertTrue(drops.contains(objs.coins.id to 150_000)) { "drops=$drops" }
            assertTrue(drops.contains(objs.abyssal_whip.id to 1)) { "drops=$drops" }
        }

    @Test
    fun GameTestState.`always-kept untradeables survive a skulled pvp death`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            val killer = pvpSetup()
            player.inv[0] = InvObj(death_objs.skavid_map)
            player.inv[1] = InvObj(objs.abyssal_whip)
            player.inv[2] = InvObj(death_objs.looting_bag)
            dieToPlayer(killer)

            assertEquals(1, player.count(death_objs.skavid_map))
            assertEquals(0, player.count(objs.abyssal_whip))
            assertEquals(0, player.count(death_objs.looting_bag))
            val drops = deps.groundObjs(WILDERNESS).map { it.first }
            assertFalse(drops.contains(death_objs.looting_bag.id)) { "drops=$drops" }
        }

    @Test
    fun GameTestState.`protect item keeps one item on a skulled pvp death`() =
        runInjectedGameTest(Deps::class, null, *SCRIPTS) { deps ->
            val killer = pvpSetup()
            player.setVarBit(varbits.protect_item, 1)
            player.inv[0] = InvObj(objs.rune_platebody)
            player.inv[1] = InvObj(objs.abyssal_whip)
            dieToPlayer(killer)

            assertEquals(1, player.count(objs.abyssal_whip))
            assertEquals(0, player.count(objs.rune_platebody))
            val drops = deps.groundObjs(WILDERNESS)
            assertTrue(drops.contains(objs.rune_platebody.id to 1)) { "drops=$drops" }
        }

    private fun GameTestScope.pvmDeath(deps: Deps) {
        player.observerUUID = 1 // Normally assigned on login; required to own ground objs.
        player.telejump(FIELD)
        player.inv[0] = InvObj(objs.coins, 1000)
        player.inv[1] = InvObj(objs.bronze_axe)
        player.inv[2] = InvObj(objs.rune_platebody)
        player.inv[3] = InvObj(objs.abyssal_whip)
        player.inv[4] = InvObj(objs.beer)
        player.queueDeath()
        advanceUntil(predicate = { deps.gravestones.hasGrave(player) && player.coords != FIELD })
    }

    private fun GameTestScope.pvpSetup(): Player {
        player.observerUUID = 1 // Normally assigned on login; required to own ground objs.
        player.telejump(WILDERNESS)
        player.skullIcon = constants.skullicon_default
        val killer = registerPlayer(WILDERNESS.translateX(2))
        killer.observerUUID = 2
        return killer
    }

    private fun GameTestScope.dieToPlayer(killer: Player) {
        player.heroPoints(killer, 10)
        player.queueDeath()
        advanceUntil(predicate = { !WildernessArea.contains(player.coords) })
    }

    class Deps
    @Inject
    constructor(val gravestones: Gravestones, private val objRegistry: ObjRegistry) {
        fun groundObjs(coords: CoordGrid): List<Pair<Int, Int>> =
            objRegistry.findAll(coords).map { it.type to it.count }.toList()
    }

    private companion object {
        val SCRIPTS =
            arrayOf(PlayerDeathScript::class, GravestoneScript::class, DeathOfficeScript::class)
        val FIELD = CoordGrid(0, 50, 50, 30, 30)
        val WILDERNESS = CoordGrid(0, 48, 56, 28, 16)
    }
}
