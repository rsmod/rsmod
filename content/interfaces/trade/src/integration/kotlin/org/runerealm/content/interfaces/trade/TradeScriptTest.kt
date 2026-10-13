package org.runerealm.content.interfaces.trade

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.players.OpPlayer
import net.rsprot.protocol.game.outgoing.inv.UpdateInvFull
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.invs
import org.runerealm.api.config.refs.objs
import org.runerealm.api.net.rsprot.handlers.OpPlayerHandler
import org.runerealm.api.player.ironman.IronmanMode
import org.runerealm.api.player.ironman.ironmanMode
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.capture.CaptureClient
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.inv.InvTypeList

class TradeScriptTest {
    @Test
    fun GameTestState.`request and accept opens the trade screens`() = runTradeTest { deps ->
        val bob = createBob()
        player.opPlayer4(deps, bob)
        advance()
        assertMessageSent("Sending trade offer...")
        assertMessageSent("Alice wishes to trade with you.", bob)
        assertModalNotOpen(trade_interfaces.main)

        bob.opPlayer4(deps, player)
        advance()
        assertModalOpen(trade_interfaces.main)
        assertModalOpen(trade_interfaces.side)
        assertModalOpen(trade_interfaces.main, bob)
        assertModalOpen(trade_interfaces.side, bob)
    }

    @Test
    fun GameTestState.`offering and removing moves objs and shows the partner's offer`() =
        runTradeTest { deps ->
            val bob = createBob()
            player.inv[0] = InvObj(objs.coins, 1000)
            player.inv[1] = InvObj(objs.logs)
            player.inv[2] = InvObj(objs.logs)
            player.inv[3] = InvObj(objs.logs)
            startTrade(deps, bob)

            val aliceOffer = player.offer(deps)
            player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op2)
            advance()
            assertEquals(5, aliceOffer.count(objs.coins))
            assertEquals(995, player.inv.count(objs.coins))
            val other = bob.captureClient.filterIsInstance<UpdateInvFull>()
            assertTrue(other.any { it.combinedId < -70000 && it.getObject(0) != -1L })

            player.ifButton(trade_components.side_inventory, 1, IfButtonOp.Op4)
            advance()
            assertEquals(3, aliceOffer.count(objs.logs))
            assertEquals(0, player.inv.count(objs.logs))

            player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op5)
            advance()
            player.resumeCountDialog(100)
            advance()
            assertEquals(105, aliceOffer.count(objs.coins))

            player.ifButton(trade_components.main_your_offer, 0, IfButtonOp.Op1)
            advance()
            assertEquals(104, aliceOffer.count(objs.coins))
            assertEquals(896, player.inv.count(objs.coins))

            // Removing all of the coins compacts the offer.
            player.ifButton(trade_components.main_your_offer, 0, IfButtonOp.Op4)
            advance()
            assertEquals(0, aliceOffer.count(objs.coins))
            assertEquals(1000, player.inv.count(objs.coins))
            assertEquals(3, aliceOffer.count(objs.logs))
            assertNotNull(aliceOffer[0])
        }

    @Test
    fun GameTestState.`untradeable objs cannot be offered`() = runTradeTest { deps ->
        val bob = createBob()
        val untradeable = firstObjType { !it.tradeable && !it.isPlaceholder }
        player.inv[0] = InvObj(untradeable)
        startTrade(deps, bob)
        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        advance()
        assertMessageSent("You can't trade this item.")
        assertTrue(player.offer(deps).isEmpty())
        assertNotNull(player.inv[0])
    }

    @Test
    fun GameTestState.`a full trade swaps the offers`() = runTradeTest { deps ->
        val bob = createBob()
        player.inv[0] = InvObj(objs.coins, 1000)
        bob.inv[0] = InvObj(objs.logs)
        bob.inv[1] = InvObj(objs.tinderbox)
        startTrade(deps, bob)

        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op4)
        bob.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        bob.ifButton(trade_components.side_inventory, 1, IfButtonOp.Op1)
        advance()

        player.ifButton(trade_components.main_accept)
        advance()
        assertModalOpen(trade_interfaces.main)
        bob.ifButton(trade_components.main_accept)
        advance()
        assertModalOpen(trade_interfaces.confirm)
        assertModalOpen(trade_interfaces.confirm, bob)

        bob.ifButton(trade_components.confirm_accept)
        advance()
        assertModalOpen(trade_interfaces.confirm)
        player.ifButton(trade_components.confirm_accept)
        advance()

        assertModalNotOpen(trade_interfaces.confirm)
        assertModalNotOpen(trade_interfaces.confirm, bob)
        assertMessageSent("Accepted trade.")
        assertMessageSent("Accepted trade.", bob)
        assertEquals(0, player.inv.count(objs.coins))
        assertEquals(1, player.inv.count(objs.logs))
        assertEquals(1, player.inv.count(objs.tinderbox))
        assertEquals(1000, bob.inv.count(objs.coins))
        assertEquals(0, bob.inv.count(objs.logs))
        assertTrue(player.offer(deps).isEmpty())
        assertTrue(bob.offer(deps).isEmpty())
    }

    @Test
    fun GameTestState.`changing an offer resets acceptance`() = runTradeTest { deps ->
        val bob = createBob()
        player.inv[0] = InvObj(objs.coins, 1000)
        startTrade(deps, bob)

        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        advance()
        bob.ifButton(trade_components.main_accept)
        advance()
        // Alice changes her offer after Bob accepted: Bob has to accept again.
        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        advance()
        player.ifButton(trade_components.main_accept)
        advance()
        assertModalOpen(trade_interfaces.main)
        assertModalNotOpen(trade_interfaces.confirm)
        bob.ifButton(trade_components.main_accept)
        advance()
        assertModalOpen(trade_interfaces.confirm)
    }

    @Test
    fun GameTestState.`not enough inventory space blocks accepting`() = runTradeTest { deps ->
        val bob = createBob()
        player.inv[0] = InvObj(objs.logs)
        bob.fillInv(InvObj(objs.tinderbox))
        startTrade(deps, bob)

        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        advance()
        player.ifButton(trade_components.main_accept)
        advance()
        assertMessageSent("Other player doesn't have enough inventory space for this trade.")
        bob.ifButton(trade_components.main_accept)
        advance()
        assertMessageSent("You don't have enough inventory space for this trade.", bob)
        assertModalNotOpen(trade_interfaces.confirm)
    }

    @Test
    fun GameTestState.`declining returns the offered objs`() = runTradeTest { deps ->
        val bob = createBob()
        player.inv[0] = InvObj(objs.coins, 1000)
        bob.inv[0] = InvObj(objs.logs)
        startTrade(deps, bob)

        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op4)
        bob.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        advance()
        assertTrue(player.offer(deps).isNotEmpty())

        player.ifButton(trade_components.main_decline)
        advance()
        assertModalNotOpen(trade_interfaces.main)
        assertModalNotOpen(trade_interfaces.main, bob)
        assertMessageSent("Other player declined trade.", bob)
        assertEquals(1000, player.inv.count(objs.coins))
        assertEquals(1, bob.inv.count(objs.logs))
        assertTrue(player.offer(deps).isEmpty())
        assertTrue(bob.offer(deps).isEmpty())
    }

    @Test
    fun GameTestState.`declining on the confirm screen returns the offered objs`() =
        runTradeTest { deps ->
            val bob = createBob()
            player.inv[0] = InvObj(objs.coins, 1000)
            startTrade(deps, bob)

            player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op4)
            advance()
            player.ifButton(trade_components.main_accept)
            bob.ifButton(trade_components.main_accept)
            advance()
            assertModalOpen(trade_interfaces.confirm, bob)

            bob.ifButton(trade_components.confirm_decline)
            advance()
            assertModalNotOpen(trade_interfaces.confirm)
            assertMessageSent("Other player declined trade.")
            assertEquals(1000, player.inv.count(objs.coins))
            assertEquals(0, bob.inv.count(objs.coins))
        }

    @Test
    fun GameTestState.`logging out cancels the trade`() = runTradeTest { deps ->
        val bob = createBob()
        bob.inv[0] = InvObj(objs.logs)
        startTrade(deps, bob)
        bob.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op1)
        advance()
        assertTrue(bob.offer(deps).isNotEmpty())

        eventBus.publish(SessionStateEvent.Logout(bob))
        assertEquals(1, bob.inv.count(objs.logs))
        assertTrue(bob.offer(deps).isEmpty())
        assertModalNotOpen(trade_interfaces.main)
    }

    @Test
    fun GameTestState.`walking away cancels the trade`() = runTradeTest { deps ->
        val bob = createBob()
        player.inv[0] = InvObj(objs.coins, 1000)
        startTrade(deps, bob)
        player.ifButton(trade_components.side_inventory, 0, IfButtonOp.Op4)
        advance()

        player.moveGameClick(player.coords.translateZ(3))
        advance()
        assertModalNotOpen(trade_interfaces.main)
        assertModalNotOpen(trade_interfaces.main, bob)
        assertMessageSent("Other player declined trade.", bob)
        assertEquals(1000, player.inv.count(objs.coins))
    }

    @Test
    fun GameTestState.`ironmen cannot trade`() = runTradeTest { deps ->
        val bob = createBob()
        player.ironmanMode = IronmanMode.Standard
        player.opPlayer4(deps, bob)
        advance()
        assertMessageSent("You are an Ironman. You stand alone.")
        assertMessageNotSent("Alice wishes to trade with you.", bob)

        player.ironmanMode = IronmanMode.None
        bob.ironmanMode = IronmanMode.Hardcore
        player.opPlayer4(deps, bob)
        advance()
        assertMessageSent("Bob is an Ironman. He stands alone.")
        assertMessageNotSent("Alice wishes to trade with you.", bob)
    }

    @Test
    fun GameTestState.`busy players cannot be traded`() = runTradeTest { deps ->
        val bob = createBob()
        bob.ifOpenMain(trade_interfaces.confirm)
        player.opPlayer4(deps, bob)
        advance()
        assertMessageSent("Other player is busy at the moment.")
    }

    private fun GameTestScope.createBob(): Player {
        player.displayName = "Alice"
        allocZoneCollision(player.coords)
        val bob = registerPlayer(coords = player.coords.translateX(1))
        bob.displayName = "Bob"
        return bob
    }

    private fun GameTestScope.startTrade(deps: TradeTestDependencies, bob: Player) {
        player.opPlayer4(deps, bob)
        advance()
        bob.opPlayer4(deps, player)
        advance()
        assertModalOpen(trade_interfaces.main)
        assertModalOpen(trade_interfaces.main, bob)
    }

    private fun Player.opPlayer4(deps: TradeTestDependencies, target: Player) {
        captureClient.queue(deps.opPlayerHandler, OpPlayer(target.slotId, false, 4))
    }

    private fun Player.offer(deps: TradeTestDependencies): Inventory =
        invMap.getOrPut(deps.invTypes[invs.tradeoffer])

    private val Player.captureClient: CaptureClient
        get() = client as CaptureClient

    private fun GameTestState.runTradeTest(body: GameTestScope.(TradeTestDependencies) -> Unit) =
        runInjectedGameTest(TradeTestDependencies::class, null, TradeScript::class) { deps ->
            body(deps)
        }
}

class TradeTestDependencies
@Inject
constructor(val opPlayerHandler: OpPlayerHandler, val invTypes: InvTypeList)
