package org.rsmod.content.other.special.weapons.scripts.charge

import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.varobjs
import org.rsmod.api.obj.charges.ObjChargeManager
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeld2
import org.rsmod.api.script.onOpHeld3
import org.rsmod.api.script.onOpHeld4
import org.rsmod.api.script.onOpHeld5
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onOpWorn2
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class TumekensShadowCharging
@Inject
constructor(private val charges: ObjChargeManager, private val objRepo: ObjRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpHeld2(objs.tumekens_shadow_uncharged) { wieldUncharged() }
        onOpHeld3(objs.tumekens_shadow_uncharged) { charge(it.inventory, it.slot, it.type) }
        onOpHeld4(objs.tumekens_shadow) { charge(it.inventory, it.slot, it.type) }
        onOpHeld5(objs.tumekens_shadow) { uncharge(it.inventory, it.slot) }
        onOpHeld3(objs.tumekens_shadow) { checkCharges(it.inventory[it.slot]) }
        onOpWorn2(objs.tumekens_shadow) { checkCharges(player.righthand) }

        onOpHeldU(objs.tumekens_shadow, objs.soul_rune) { charge(inv, it.firstSlot, it.first) }
        onOpHeldU(objs.tumekens_shadow, objs.chaos_rune) { charge(inv, it.firstSlot, it.first) }
        onOpHeldU(objs.tumekens_shadow_uncharged, objs.soul_rune) {
            charge(inv, it.firstSlot, it.first)
        }
        onOpHeldU(objs.tumekens_shadow_uncharged, objs.chaos_rune) {
            charge(inv, it.firstSlot, it.first)
        }
    }

    private fun ProtectedAccess.wieldUncharged() {
        mes(
            "Tumeken's Shadow has no charges! You need to " +
                "charge it with soul runes and chaos runes."
        )
    }

    private suspend fun ProtectedAccess.charge(inventory: Inventory, invSlot: Int, obj: ObjType) {
        if (objs.soul_rune !in inv) {
            mes("You don't appear to have any soul runes to charge Tumeken's shadow with.")
            return
        }

        if (objs.chaos_rune !in inv) {
            mes("You don't appear to have any chaos runes to charge Tumeken's shadow with.")
            return
        }

        // Official behavior: `countDialog` is shown as long as the player has at least one of each
        // rune, even if the total is not enough to apply a single charge.
        val currCharges = charges.getCharges(inventory[invSlot], varobjs.tumeken_charges)
        if (currCharges >= MAX_CHARGES) {
            // TODO(content): Correct message when already fully charged.
            mes("Your Tumeken's shadow is fully charged.")
            return
        }

        val maxCharges = min(MAX_CHARGES - currCharges, getMaxRuneCharges())
        val question = "How many charges do you want to apply? (Up to $maxCharges)"
        val requested = min(countDialog(question), maxCharges)
        if (requested == 0) {
            return
        }

        val removeRunes =
            invDel(
                inv = inv,
                type1 = objs.chaos_rune,
                count1 = requested * CHAOS_PER_CHARGE,
                type2 = objs.soul_rune,
                count2 = requested * SOUL_PER_CHARGE,
            )

        if (removeRunes.failure) {
            return
        }

        // Paranoid check: Should always be the case.
        check(inventory[invSlot].isType(obj))

        charges.addCharges(inventory, invSlot, requested, varobjs.tumeken_charges, MAX_CHARGES)
        // Official message: Uses "charges" even when applying a single charge.
        objbox(objs.tumekens_shadow, 400, "You apply $requested charges to your Tumeken's shadow.")
    }

    private fun ProtectedAccess.getMaxRuneCharges(): Int {
        val chaos = invTotal(inv, objs.chaos_rune) / CHAOS_PER_CHARGE
        val soul = invTotal(inv, objs.soul_rune) / SOUL_PER_CHARGE
        return min(chaos, soul)
    }

    private fun ProtectedAccess.checkCharges(obj: InvObj?) {
        // Official message: "s" is lowercase and always uses "charges."
        val charges = charges.getCharges(obj, varobjs.tumeken_charges)
        mes("Tumeken's shadow has $charges charges remaining.")
    }

    private suspend fun ProtectedAccess.uncharge(inventory: Inventory, invSlot: Int) {
        val currCharges = charges.getCharges(inventory[invSlot], varobjs.tumeken_charges)
        if (currCharges == 0) {
            charges.removeAllCharges(inventory, invSlot, varobjs.tumeken_charges)
            return
        }

        // Given how the `charge` conditions are handled when it comes to checking if player has
        // runes, we are assuming this condition is also lenient. Requires testing in the official
        // game (need max rune stacks).
        var spaceReq = 2
        if (objs.soul_rune in inv) {
            spaceReq--
        }
        if (objs.chaos_rune in inv) {
            spaceReq--
        }

        if (inv.freeSpace() < spaceReq) {
            mes(
                "You don't have enough inventory space for the runes " +
                    "gained from uncharging Tumeken's Shadow."
            )
            return
        }

        val confirmation =
            choice2(
                "Proceed.",
                true,
                "Cancel.",
                false,
                title = "Uncharge all the charges from your staff?",
            )

        if (!confirmation) {
            return
        }

        val chargesRemoved = charges.removeAllCharges(inv, invSlot, varobjs.tumeken_charges)
        check(chargesRemoved > 0)

        val soulCount = chargesRemoved * SOUL_PER_CHARGE
        invAddOrDrop(objRepo, objs.soul_rune, soulCount)

        val chaosCount = chargesRemoved * CHAOS_PER_CHARGE
        invAddOrDrop(objRepo, objs.chaos_rune, chaosCount)

        val message =
            "You uncharge your Tumeken's shadow, regaining ${soulCount.formatAmount} " +
                "soul runes and ${chaosCount.formatAmount} chaos runes in the process."
        objbox(objs.tumekens_shadow_uncharged, 400, message)
    }

    private companion object {
        const val MAX_CHARGES = 20_000

        const val CHAOS_PER_CHARGE = 5
        const val SOUL_PER_CHARGE = 2
    }
}
