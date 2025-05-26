package org.rsmod.content.interfaces.equipment.death

import jakarta.inject.Inject
import java.util.Objects
import kotlin.math.abs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.isInCombat
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.stopInvTransmit
import org.rsmod.api.script.onIfClose
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.content.interfaces.equipment.configs.equip_components
import org.rsmod.content.interfaces.equipment.configs.equip_interfaces
import org.rsmod.content.interfaces.equipment.configs.equip_invs
import org.rsmod.content.interfaces.equipment.configs.equip_objs
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ItemsKeptOnDeathScript
@Inject
constructor(
    private val protectedAccess: ProtectedAccessLauncher,
    private val invTypes: InvTypeList,
    private val objTypes: ObjTypeList,
    private val marketPrices: MarketPrices,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(equip_components.items_kept_on_death) { player.selectKeptOnDeath() }
        onIfClose(equip_interfaces.deathkeep) { player.closeKeptOnDeath() }
    }

    private fun Player.selectKeptOnDeath() {
        if (isInCombat()) {
            mes("You cannot view items kept on death while in combat.")
            return
        }
        if (isAccessProtected) {
            mes("Please finish what you're doing before opening this menu.")
            soundSynth(synths.pillory_wrong)
            return
        }
        protectedAccess.launch(this) {
            stopAction()
            showKeptOnDeath()
        }
    }

    private suspend fun ProtectedAccess.showKeptOnDeath() {
        val deathSettings = DeathSettings()
        val deathInventory = createDeathInventory(deathSettings)
        updateDeathInventory(deathInventory)
        openDeathInventory(deathInventory, deathSettings)
        initChangeListener(deathSettings)
    }

    private fun ProtectedAccess.updateDeathInventory(deathInventory: DeathInventory) {
        invTransmit(deathInventory.lost)
        invTransmit(deathInventory.data)
    }

    private fun ProtectedAccess.openDeathInventory(
        deathInventory: DeathInventory,
        deathSettings: DeathSettings,
    ) {
        ifOpenMainModal(equip_interfaces.deathkeep)
        deathKeepInit(deathInventory, deathSettings)
        ifSetEvents(equip_components.items_kept_on_death_pbutton, 0..3, IfEvent.PauseButton)
        updateDeathRisk(deathInventory)
    }

    private fun ProtectedAccess.updateDeathRisk(deathInventory: DeathInventory) {
        ifSetText(
            equip_components.items_kept_on_death_risk,
            "Guide risk value:<br><col=ffffff>" +
                "${deathInventory.calculateRisk().formatAmount}</col>",
        )
    }

    private suspend fun ProtectedAccess.initChangeListener(settings: DeathSettings) {
        val update = pauseButton()

        ifClose()

        // Verify the pause button input came from items kept on death interface.
        if (!update.component.isType(equip_components.items_kept_on_death_pbutton)) {
            return
        }

        val updatedSettings =
            when (val sub = update.subcomponent) {
                0 -> settings.copy(protectItemPrayer = !settings.protectItemPrayer)
                1 -> settings.copy(skullActive = !settings.skullActive)
                2 -> settings.copy(playerKill = !settings.playerKill)
                3 -> settings.copy(wildernessLvl = abs(settings.wildernessLvl - 21))
                else -> throw IllegalStateException("Invalid sub component: $sub")
            }

        val updatedInventory = createDeathInventory(updatedSettings)
        updateDeathInventory(updatedInventory)
        openDeathInventory(updatedInventory, updatedSettings)

        initChangeListener(updatedSettings)
    }

    private fun Player.closeKeptOnDeath() {
        stopInvTransmit(itemsKeptOnDeath)
        stopInvTransmit(itemsKeptOnDeathData)
    }

    private fun ProtectedAccess.createDeathInventory(settings: DeathSettings): DeathInventory {
        val keptInventory = Inventory.create(invTypes[equip_invs.kept])
        val lostInventory = Inventory.create(invTypes[equip_invs.death])
        val dataInventory = Inventory.create(invTypes[equip_invs.death_data])

        check(keptInventory.size == 4) {
            "Size for `keptInventory` expected to be `4`. (size=${keptInventory.size})"
        }

        check(lostInventory.size == dataInventory.size) {
            "Unexpected size mismatch for inventories: lost=$lostInventory, data=$dataInventory"
        }

        check(inv.size + worn.size <= lostInventory.size) {
            "Death inventory can only fit `${lostInventory.size}` objs."
        }

        val carried = sortedCarriedObjs().toMutableList()
        val (kept, lost) = carried.partition(settings.keepCount())

        val keptAddResult = invMoveAll(keptInventory, kept)
        val lostAddResult = invMoveAll(lostInventory, lost)

        check(kept.isEmpty() || keptAddResult.success) {
            "Could not add `inv` and `worn` into kept inventory. (result=$keptAddResult)"
        }

        check(lost.isEmpty() || lostAddResult.success) {
            "Could not add `inv` and `worn` into lost inventory. (result=$lostAddResult)"
        }

        // Convert all objs from `lost` inventory into the respective "death" obj that are
        // substitutes used by cs2 to send "extra data" per inv obj.
        for (i in dataInventory.indices) {
            val converted = convertToDataObj(lostInventory[i])
            dataInventory[i] = converted
        }

        return DeathInventory(keptInventory, lostInventory, dataInventory)
    }

    private fun ProtectedAccess.sortedCarriedObjs(): Sequence<InvObj> {
        val overall = inv.filterNotNull() + worn.filterNotNull()
        return overall
            .asSequence()
            .filterNot { objTypes[it].param(params.bond_item) }
            .sortedByDescending(::marketPriceSingle)
    }

    private fun MutableList<InvObj>.partition(keepCount: Int): Pair<List<InvObj>, List<InvObj>> {
        var pointer = 0
        val kept = mutableListOf<InvObj>()
        for (i in 0 until keepCount) {
            val obj = getOrNull(pointer) ?: break
            kept += obj.copy(count = 1)

            if (obj.count == 1) {
                pointer++
                continue
            }

            this[pointer] = obj.copy(count = obj.count - 1)
        }
        val lost = drop(pointer)
        return kept to lost
    }

    private fun DeathSettings.keepCount(): Int {
        var keep = if (skullActive) 0 else 3
        if (protectItemPrayer) {
            keep++
        }
        return keep
    }

    private fun convertToDataObj(obj: InvObj?): InvObj {
        if (obj == null) {
            return InvObj(equip_objs.deleted)
        }
        val type = objTypes[obj]
        val price = marketPrices[type] ?: type.cost
        val fee = calculateFee(price)
        return InvObj(equip_objs.gravestone, fee + 1)
    }

    private fun calculateFee(marketPrice: Int): Int =
        when {
            marketPrice < 100_000 -> 0
            marketPrice in 100_000..<1_000_000 -> 1000
            marketPrice in 1_000_000..<10_000_000 -> 10_000
            else -> 100_000
        }

    private fun marketPriceSingle(obj: InvObj?): Long {
        if (obj == null) {
            return 0
        }
        val type = objTypes[obj]
        val price = marketPrices[type] ?: 1
        return price.toLong()
    }

    private fun marketPriceTotal(obj: InvObj?): Long = marketPriceSingle(obj) * (obj?.count ?: 0)

    private fun DeathInventory.calculateRisk(): Long = lost.sumOf(::marketPriceTotal)

    private data class DeathInventory(
        val kept: Inventory,
        val lost: Inventory,
        val data: Inventory,
    )

    private data class DeathSettings(
        val header: String = "",
        val skullActive: Boolean = false,
        val protectItemPrayer: Boolean = false,
        val wildernessLvl: Int = 0,
        val playerKill: Boolean = false,
    )

    private fun ProtectedAccess.deathKeepInit(inventory: DeathInventory, settings: DeathSettings) {
        val skullActive = settings.skullActive
        val protectItemPrayer = settings.protectItemPrayer
        val wildernessLvl = settings.wildernessLvl
        val playerKill = settings.playerKill
        val headerText = settings.header
        val keepCount = inventory.kept.count(Objects::nonNull)
        val keepObjs = inventory.kept.map { it?.id ?: -1 }
        runClientScript(
            972,
            if (skullActive) 1 else 0,
            if (protectItemPrayer) 1 else 0,
            wildernessLvl,
            if (playerKill) 1 else 0,
            headerText,
            keepCount,
            keepObjs[0],
            keepObjs[1],
            keepObjs[2],
            keepObjs[3],
        )
    }
}

private val Player.itemsKeptOnDeath: Inventory
    get() = invMap.getValue(equip_invs.death)

private val Player.itemsKeptOnDeathData: Inventory
    get() = invMap.getValue(equip_invs.death_data)
