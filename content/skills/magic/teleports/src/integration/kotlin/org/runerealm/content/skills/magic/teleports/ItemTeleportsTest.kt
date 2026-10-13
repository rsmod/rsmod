package org.runerealm.content.skills.magic.teleports

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.inv.HeldOpScript
import org.runerealm.api.inv.WornOpScript
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.obj.Wearpos
import org.runerealm.map.CoordGrid

class ItemTeleportsTest {
    private val start = CoordGrid(3222, 3218, 0)

    @Test
    fun GameTestState.`rubbing a glory teleports and uses a charge`() =
        runGameTest(JewelleryTeleports::class, HeldOpScript::class) {
            player.teleport(start)
            allocZoneCollision(CoordGrid(3087, 3496, 0))
            val glory4 = objType("amulet_of_glory_4")
            val glory3 = objType("amulet_of_glory_3")
            player.inv[0] = InvObj(glory4)
            player.opHeld(0, glory4.iop.indexOfFirst { it == "Rub" } + 1)
            advance()
            player.resumePauseButton(components.chatmenu_pbutton, sub = 1) // Edgeville.
            advanceUntil({ player.coords != start }, timeoutTicks = 10)
            assertEquals(CoordGrid(3087, 3496, 0), player.coords)
            assertEquals(glory3.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`picking a destination from the rub submenu teleports directly`() =
        runGameTest(JewelleryTeleports::class, HeldOpScript::class) {
            player.teleport(start)
            allocZoneCollision(CoordGrid(2918, 3176, 0))
            val glory4 = objType("amulet_of_glory_4")
            val glory3 = objType("amulet_of_glory_3")
            player.inv[0] = InvObj(glory4)
            val rubOp = glory4.iop.indexOfFirst { it == "Rub" } + 1
            // "Rub" -> "Karamja", the second sub-op; no chat menu is involved.
            player.opHeldSubOp(0, op = rubOp, subop = 2)
            advanceUntil({ player.coords != start }, timeoutTicks = 10)
            assertEquals(CoordGrid(2918, 3176, 0), player.coords)
            assertEquals(glory3.id, player.inv[0]?.id)
        }

    @Test
    fun GameTestState.`rub submenu works on every teleport jewellery`() =
        runGameTest(JewelleryTeleports::class, HeldOpScript::class) {
            for (jewellery in JEWELLERY) {
                val charged = objType(jewellery.charges.first())
                val rubOp = charged.iop.indexOfFirst { it == "Rub" } + 1
                for ((index, dest) in jewellery.destinations.withIndex()) {
                    player.teleport(start)
                    allocZoneCollision(dest.coords)
                    player.inv[0] = InvObj(charged)
                    player.opHeldSubOp(0, op = rubOp, subop = index + 1)
                    advanceUntil({ player.coords != start }, timeoutTicks = 10)
                    assertEquals(dest.coords, player.coords) { "${jewellery.name} -> ${dest.name}" }
                    advance(ticks = 2)
                }
            }
        }

    @Test
    fun GameTestState.`worn jewellery teleports from the equipment tab`() =
        runGameTest(JewelleryTeleports::class, WornOpScript::class) {
            player.teleport(start)
            allocZoneCollision(CoordGrid(2440, 3090, 0))
            val ring8 = objType("ring_of_dueling_8")
            val ring7 = objType("ring_of_dueling_7")
            player.worn[Wearpos.Ring.slot] = InvObj(ring8)
            player.ifOpenOverlay(interfaces.wornitems, components.toplevel_target_side4)
            // Worn op 3 is `wear_op2`: "Castle Wars".
            player.ifButton(worn_slots.ring, op = IfButtonOp.Op3)
            advanceUntil({ player.coords != start }, timeoutTicks = 10)
            assertEquals(CoordGrid(2440, 3090, 0), player.coords)
            assertEquals(ring7.id, player.worn[Wearpos.Ring.slot]?.id)
        }

    @Test
    fun GameTestState.`worn glory uses its last charge`() =
        runGameTest(JewelleryTeleports::class, WornOpScript::class) {
            player.teleport(start)
            allocZoneCollision(CoordGrid(3087, 3496, 0))
            player.worn[Wearpos.Front.slot] = InvObj(objType("amulet_of_glory_1"))
            player.ifOpenOverlay(interfaces.wornitems, components.toplevel_target_side4)
            player.ifButton(worn_slots.neck, op = IfButtonOp.Op2) // "Edgeville"
            advanceUntil({ player.coords != start }, timeoutTicks = 10)
            assertEquals(CoordGrid(3087, 3496, 0), player.coords)
            assertEquals(objType("amulet_of_glory").id, player.worn[Wearpos.Front.slot]?.id)
        }

    @Test
    fun GameTestState.`breaking a tablet teleports and uses it up`() =
        runGameTest(TeleportTablets::class, HeldOpScript::class) {
            player.teleport(start)
            allocZoneCollision(CoordGrid(3213, 3424, 0))
            val tablet = objType("poh_tablet_varrockteleport")
            player.inv[0] = InvObj(tablet)
            player.opHeld(0, tablet.iop.indexOfFirst { it == "Break" } + 1)
            advanceUntil({ player.coords != start }, timeoutTicks = 10)
            assertEquals(CoordGrid(3213, 3424, 0), player.coords)
            assertEquals(null, player.inv[0])
        }

    @Test
    fun GameTestState.`tablets do not work above level 20 wilderness`() =
        runGameTest(TeleportTablets::class, HeldOpScript::class) {
            val deep = CoordGrid(3100, 3700, 0)
            player.teleport(deep)
            val tablet = objType("poh_tablet_varrockteleport")
            player.inv[0] = InvObj(tablet)
            player.opHeld(0, tablet.iop.indexOfFirst { it == "Break" } + 1)
            advance(ticks = 5)
            assertEquals(deep, player.coords)
            assertEquals(tablet.id, player.inv[0]?.id)
        }

    private fun GameTestState.objType(name: String) =
        cacheTypes.objs.values.first { it.internalName == name }
}

internal object worn_slots : ComponentReferences() {
    val neck = find("wornitems:slot2")
    val ring = find("wornitems:slot12")
}
