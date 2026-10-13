package org.runerealm.content.interfaces.bank.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.objs
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.map.CoordGrid

@Execution(ExecutionMode.SAME_THREAD)
class BankLocsScriptTest {
    private val base = CoordGrid(0, 50, 50, 20, 20)

    @Test
    fun GameTestState.`bank booth bank option opens the bank`() =
        runGameTest(BankLocsScript::class, BankInvScript::class, DepositBoxScript::class) {
            allocZoneCollision(base)
            val booth = placeMapLoc(base, locType("aide_bankbooth"))
            player.teleport(base.translateX(-1))
            player.opLoc2(booth)
            advance(ticks = 2)
            assertTrue(player.ui.containsModal(interfaces.bank_main))
        }

    @Test
    fun GameTestState.`deposit box deposits inventory items`() =
        runGameTest(BankLocsScript::class, BankInvScript::class, DepositBoxScript::class) {
            allocZoneCollision(base)
            val box = placeMapLoc(base, locType("bank_deposit_box"))
            player.teleport(base.translateX(-1))
            val bank = bank(player)
            player.clearInv()
            player.inv[0] = InvObj(objs.coins, 100)
            player.opLoc1(box)
            advance(ticks = 5)
            assertTrue(
                player.ui.containsModal(depositbox_interfaces.depositbox),
                "coords=${player.coords} modals=${player.ui.modals} i=${player.interaction}",
            )

            player.ifButton(depositbox_components.inventory, comsub = 0, op = IfButtonOp.Op6)
            advance()
            assertEquals(null, player.inv[0])
            assertEquals(100, bank.count(objs.coins))
        }

    @Test
    fun GameTestState.`deposit box banks and removes single worn items`() =
        runGameTest(BankLocsScript::class, BankInvScript::class, DepositBoxScript::class) {
            allocZoneCollision(base)
            val box = placeMapLoc(base, locType("bank_deposit_box"))
            player.teleport(base.translateX(-1))
            val bank = bank(player)
            player.clearInv()
            player.worn[12] = InvObj(objs.berserker_ring, 1)
            player.opLoc1(box)
            advance(ticks = 5)
            assertTrue(player.ui.containsModal(depositbox_interfaces.depositbox))

            // "Bank" deposits the worn item straight into the bank.
            player.ifButton(depositbox_components.worn_slots.getValue(12), op = IfButtonOp.Op2)
            advance()
            assertEquals(null, player.worn[12])
            assertEquals(1, bank.count(objs.berserker_ring))

            // "Remove" unequips it into the inventory.
            player.worn[12] = InvObj(objs.archers_ring_i, 1)
            player.ifButton(depositbox_components.worn_slots.getValue(12), op = IfButtonOp.Op1)
            advance()
            assertEquals(null, player.worn[12])
            assertEquals(objs.archers_ring_i.id, player.inv[0]?.id)
        }

    private fun GameTestState.locType(name: String) =
        cacheTypes.locs.values.first { it.internalName == name }
}
