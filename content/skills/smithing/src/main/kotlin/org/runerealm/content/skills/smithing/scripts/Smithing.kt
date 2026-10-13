package org.runerealm.content.skills.smithing.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.hands
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.ring
import org.runerealm.api.player.stat.smithingLvl
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLoc2
import org.runerealm.api.script.onOpLocU
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.content.skills.smithing.configs.SmithingBar
import org.runerealm.content.skills.smithing.configs.SmithingBars
import org.runerealm.content.skills.smithing.configs.SmithingCategories
import org.runerealm.content.skills.smithing.configs.SmithingComponents
import org.runerealm.content.skills.smithing.configs.SmithingEnums
import org.runerealm.content.skills.smithing.configs.SmithingInterfaces
import org.runerealm.content.skills.smithing.configs.SmithingLocs
import org.runerealm.content.skills.smithing.configs.SmithingProduct
import org.runerealm.content.skills.smithing.configs.SmithingProductTable
import org.runerealm.content.skills.smithing.configs.SmithingQueues
import org.runerealm.content.skills.smithing.configs.SmithingSeqs
import org.runerealm.content.skills.smithing.configs.SmithingVarBits
import org.runerealm.content.skills.smithing.configs.SmithingVarps
import org.runerealm.content.skills.smithing.configs.smithing_objs
import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.game.type.enums.findOrNull
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

// TODO:
// - cannonballs (ammo mould)
// - Varrock armour double bars, smithing cape and blast furnace
// - lovakite bars and shayzien armour
class Smithing
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val objTypes: ObjTypeList,
    private val enumTypes: EnumTypeList,
    private val xpMods: XpModifiers,
) : PluginScript() {
    private val productsByBar: Map<Int, Map<Int, SmithingProduct>> =
        SmithingProductTable.products
            .groupBy { it.bar.bar.id }
            .mapValues { (_, products) -> products.associateBy { it.component.packed } }

    private val productComponents = SmithingProductTable.products.map { it.component }.distinct()

    private val smeltOres: Set<Int> =
        SmithingBars.all.flatMap { bar -> bar.ores.map { it.first.id } }.toSet()

    override fun ScriptContext.startup() {
        for (type in furnaceTypes()) {
            onOpLoc2(type) { smeltMenu(SmithingBars.all) }
            onOpLocU(type) { useOnFurnace(it.objType) }
        }
        for (type in anvilTypes()) {
            onOpLoc1(type) { openAnvil(type, bar = null) }
            onOpLocU(type) { useOnAnvil(type, it.objType) }
        }

        onIfModalButton(SmithingComponents.make_1) { vars[SmithingVarps.makex_quantity] = 1 }
        onIfModalButton(SmithingComponents.make_5) { vars[SmithingVarps.makex_quantity] = 5 }
        onIfModalButton(SmithingComponents.make_10) { vars[SmithingVarps.makex_quantity] = 10 }
        onIfModalButton(SmithingComponents.make_x) {
            val amount = countDialog("Enter amount:")
            if (amount > 0) {
                vars[SmithingVarps.makex_quantity] = amount.coerceAtMost(MAX_QUANTITY)
            }
        }
        onIfModalButton(SmithingComponents.make_all) {
            vars[SmithingVarps.makex_quantity] = MAX_QUANTITY
        }
        for (component in productComponents) {
            onIfModalButton(component) { selectProduct(it.component.packed) }
        }

        onPlayerQueueWithArgs<SmeltTask>(SmithingQueues.smelt) { smelt(it.args) }
        onPlayerQueueWithArgs<SmithTask>(SmithingQueues.smith) { smith(it.args) }
    }

    /** Every furnace in the world: the `furnace` cache category plus a few uncategorised ones. */
    private fun furnaceTypes(): List<UnpackedLocType> {
        val extra =
            setOf(
                SmithingLocs.dwarf_keldagrim_furnace.id,
                SmithingLocs.enakh_furnace.id,
                SmithingLocs.burgh_furnace.id,
                SmithingLocs.zq_furnace.id,
                SmithingLocs.lovaquest_furnace.id,
                SmithingLocs.cof_furnace.id,
            )
        return locTypes.values.filter {
            it.op.getOrNull(1).equals(SMELT_OP, ignoreCase = true) &&
                (it.category == SmithingCategories.furnace.id || it.id in extra)
        }
    }

    /** Every anvil in the world: the `anvil` cache category plus a few uncategorised ones. */
    private fun anvilTypes(): List<UnpackedLocType> {
        val extra =
            setOf(
                SmithingLocs.brut_anvil.id,
                SmithingLocs.ds2_forge_anvil.id,
                SmithingLocs.chasm_anvil.id,
            )
        return locTypes.values.filter {
            it.id != SmithingLocs.camdozaal_anvil.id &&
                it.op.getOrNull(0).equals(SMITH_OP, ignoreCase = true) &&
                (it.category == SmithingCategories.anvil.id || it.id in extra)
        }
    }

    /* Furnace */

    private suspend fun ProtectedAccess.useOnFurnace(obj: UnpackedObjType) {
        if (obj.id !in smeltOres) {
            mes(constants.dm_default)
            return
        }
        val bars = SmithingBars.all.filter { bar -> bar.ores.any { it.first.id == obj.id } }
        smeltMenu(bars)
    }

    private suspend fun ProtectedAccess.smeltMenu(bars: List<SmithingBar>) {
        val makeable = bars.map { makeableBars(it) }
        val selection =
            skillMulti(
                title = "What would you like to smelt?",
                choices = bars.map { it.bar },
                maxQuantity = makeable.max().coerceAtLeast(1),
            )
        val bar = bars.first { it.bar.id == selection.obj.id }
        weakQueue(SmithingQueues.smelt, FIRST_ACTION_DELAY, SmeltTask(bar, selection.quantity))
    }

    private fun ProtectedAccess.makeableBars(bar: SmithingBar): Int =
        bar.ores.minOf { (ore, count) -> invTotal(inv, ore) / count }

    private fun ProtectedAccess.smelt(task: SmeltTask) {
        val bar = task.bar
        if (player.smithingLvl < bar.level) {
            mes("You need a Smithing level of ${bar.level} to smelt ${bar.metal}.")
            return
        }
        if (makeableBars(bar) <= 0) {
            mes("You need ${oreList(bar)} to make ${articleFor(bar.metal)} ${bar.metal} bar.")
            return
        }

        anim(SmithingSeqs.furnace)
        val hadForgingRing = player.isWearingForgingRing()
        for ((ore, count) in bar.ores) {
            invDel(inv, ore, count)
        }

        if (!refineSucceeds(bar)) {
            spam("The ore is too impure and you fail to refine it.")
        } else {
            invAdd(inv, bar.bar)
            val xp =
                if (bar == SmithingBars.gold && player.isWearingGoldsmithGauntlets()) {
                    SmithingBars.GOLDSMITH_GAUNTLETS_XP
                } else {
                    bar.xp
                }
            statAdvance(stats.smithing, xp * xpMods.get(player, stats.smithing))
            spam("You retrieve a bar of ${bar.metal}.")
            publish(SmeltBar(player, bar))
        }

        val remaining = task.remaining - 1
        if (remaining <= 0 || makeableBars(bar) <= 0) {
            return
        }
        if (bar == SmithingBars.iron && hadForgingRing && !player.isWearingForgingRing()) {
            // The ring of forging melted: smelting stops automatically.
            return
        }
        weakQueue(SmithingQueues.smelt, SMELT_DELAY, task.copy(remaining = remaining))
    }

    /**
     * Iron ore has a 50% chance to be too impure, unless a ring of forging is worn. Every other ore
     * always succeeds. ([Smithing](https://oldschool.runescape.wiki/w/Smithing#Success_rate))
     */
    private fun ProtectedAccess.refineSucceeds(bar: SmithingBar): Boolean {
        if (bar != SmithingBars.iron) {
            return true
        }
        if (player.isWearingForgingRing()) {
            useForgingRingCharge()
            return true
        }
        return random.of(1, 100) <= SmithingBars.IRON_SUCCESS_PERCENT
    }

    private fun ProtectedAccess.useForgingRingCharge() {
        val used = player.forgingChargesUsed + 1
        if (used >= SmithingBars.RING_OF_FORGING_CHARGES) {
            player.forgingChargesUsed = 0
            player.ring = null
            mes("<col=7f007f>Your Ring of Forging has melted.</col>")
            return
        }
        player.forgingChargesUsed = used
    }

    private fun oreList(bar: SmithingBar): String =
        bar.ores.joinToString(" and ") { (ore, count) ->
            val name = objTypes[ore].name.lowercase()
            "${numberWord(count)} $name"
        }

    /* Anvil */

    private suspend fun ProtectedAccess.useOnAnvil(type: UnpackedLocType, obj: UnpackedObjType) {
        val bar = SmithingBars.anvilBars.firstOrNull { it.bar.id == obj.id }
        if (bar == null) {
            if (type.id == SmithingLocs.lumbridge_anvil.id && obj.id in allBarIds) {
                mes("You can only use bronze bars to smith on this anvil.")
                return
            }
            mes(constants.dm_default)
            return
        }
        openAnvil(type, bar)
    }

    private suspend fun ProtectedAccess.openAnvil(type: UnpackedLocType, bar: SmithingBar?) {
        val lumbridge = type.id == SmithingLocs.lumbridge_anvil.id
        if (lumbridge && bar != null && bar != SmithingBars.bronze) {
            mes("You can only use bronze bars to smith on this anvil.")
            return
        }
        if (!hasHammer()) {
            mesbox("You need a hammer to work the metal with.")
            return
        }

        val selected =
            when {
                bar != null -> bar
                lumbridge ->
                    if (SmithingBars.bronze.bar in inv) {
                        SmithingBars.bronze
                    } else {
                        mes("You need a bronze bar to smith equipment on this anvil.")
                        return
                    }
                else ->
                    defaultAnvilBar()
                        ?: run {
                            mes(
                                "You should select an item from your inventory and use it on the anvil."
                            )
                            return
                        }
            }

        vars[SmithingVarBits.bar_type] = selected.anvilIndex
        if (vars[SmithingVarps.makex_quantity] <= 0) {
            vars[SmithingVarps.makex_quantity] = 1
        }
        ifOpenMainModal(SmithingInterfaces.smithing)
        for (button in makeButtons) {
            ifSetEvents(button, -1..-1, IfEvent.Op1)
        }
        for (component in productComponents) {
            ifSetEvents(component, -1..-1, IfEvent.Op1)
        }
    }

    /**
     * The highest tier bar in the inventory that the player can smith something from, or the lowest
     * tier bar they carry if they cannot smith any of them.
     */
    private fun ProtectedAccess.defaultAnvilBar(): SmithingBar? {
        val carried = SmithingBars.anvilBars.filter { it.bar in inv }
        if (carried.isEmpty()) {
            return null
        }
        val usable = carried.lastOrNull { player.smithingLvl >= minimumLevel(it) }
        return usable ?: carried.first()
    }

    private fun minimumLevel(bar: SmithingBar): Int =
        productsByBar[bar.bar.id]?.values?.minOfOrNull { productLevel(it.obj) } ?: bar.level

    private fun ProtectedAccess.selectProduct(component: Int) {
        val barIndex = vars[SmithingVarBits.bar_type]
        val bar = SmithingBars.anvilBars.firstOrNull { it.anvilIndex == barIndex } ?: return
        val product = productsByBar[bar.bar.id]?.get(component) ?: return
        ifClose()

        val quantity = vars[SmithingVarps.makex_quantity].coerceIn(1, MAX_QUANTITY)
        if (!canSmith(product)) {
            return
        }
        weakQueue(SmithingQueues.smith, FIRST_ACTION_DELAY, SmithTask(product, quantity))
    }

    private fun ProtectedAccess.canSmith(product: SmithingProduct): Boolean {
        val level = productLevel(product.obj)
        val name = objTypes[product.obj].name.lowercase()
        if (player.smithingLvl < level) {
            mes("You need a Smithing level of $level to make ${articleFor(name)} $name.")
            return false
        }
        if (!hasHammer()) {
            mes("You need a hammer to work the metal with.")
            return false
        }
        val bars = productBars(product.obj)
        if (invTotal(inv, product.bar.bar) < bars) {
            val barName = objTypes[product.bar.bar].name.lowercase()
            mes("You don't have enough ${barName}s to make ${articleFor(name)} $name.")
            return false
        }
        return true
    }

    private fun ProtectedAccess.smith(task: SmithTask) {
        val product = task.product
        if (!canSmith(product)) {
            return
        }

        val bars = productBars(product.obj)
        val quantity = productQuantity(product.obj)
        anim(SmithingSeqs.smithing)
        invDel(inv, product.bar.bar, bars)
        invAdd(inv, product.obj, quantity)
        val xp = product.bar.anvilXp * bars * xpMods.get(player, stats.smithing)
        statAdvance(stats.smithing, xp)

        val name = objTypes[product.obj].name.lowercase()
        val metal = if (product.bar == SmithingBars.bronze) "bronze" else product.bar.metal
        val made = if (quantity > 1) "some $name" else "${articleFor(name)} $name"
        spam("You hammer the $metal and make $made.")
        publish(SmithItem(player, product))

        val remaining = task.remaining - 1
        if (remaining <= 0 || invTotal(inv, product.bar.bar) < bars) {
            return
        }
        weakQueue(SmithingQueues.smith, SMITH_DELAY, task.copy(remaining = remaining))
    }

    private fun productBars(obj: ObjType): Int =
        enumTypes[SmithingEnums.product_bars].findOrNull(InvObj(obj))?.coerceAtLeast(1) ?: 1

    private fun productLevel(obj: ObjType): Int =
        enumTypes[SmithingEnums.product_level].findOrNull(InvObj(obj))?.coerceAtLeast(1) ?: 1

    private fun productQuantity(obj: ObjType): Int =
        enumTypes[SmithingEnums.product_quantity].findOrNull(InvObj(obj))?.coerceAtLeast(1) ?: 1

    private fun ProtectedAccess.hasHammer(): Boolean =
        smithing_objs.hammer in inv || smithing_objs.imcando_hammer in inv

    data class SmeltTask(val bar: SmithingBar, val remaining: Int)

    data class SmithTask(val product: SmithingProduct, val remaining: Int)

    data class SmeltBar(val player: Player, val bar: SmithingBar) : UnboundEvent

    data class SmithItem(val player: Player, val product: SmithingProduct) : UnboundEvent

    companion object {
        private const val SMELT_OP = "Smelt"
        private const val SMITH_OP = "Smith"

        private const val MAX_QUANTITY = 28

        // Weak queues are decremented on the same cycle they are queued, so a queue with a delay
        // of `n` executes `n - 1` cycles later.
        private const val FIRST_ACTION_DELAY = 2

        /** Smelting takes 4 ticks per bar. */
        private const val SMELT_DELAY = 5

        /** Smithing on an anvil takes 5 ticks per item. */
        private const val SMITH_DELAY = 6

        private val makeButtons =
            listOf(
                SmithingComponents.make_1,
                SmithingComponents.make_5,
                SmithingComponents.make_10,
                SmithingComponents.make_x,
                SmithingComponents.make_all,
            )

        private val allBarIds: Set<Int> by lazy { SmithingBars.all.mapTo(HashSet()) { it.bar.id } }

        private var Player.forgingChargesUsed by intVarp(SmithingVarps.ring_of_forging_charges)

        fun Player.isWearingForgingRing(): Boolean = ring?.id == smithing_objs.ring_of_forging.id

        fun Player.isWearingGoldsmithGauntlets(): Boolean =
            hands?.id == smithing_objs.goldsmith_gauntlets.id

        private fun articleFor(name: String): String =
            if (name.isNotEmpty() && name[0].lowercaseChar() in "aeiou") "an" else "a"

        private fun numberWord(count: Int): String =
            when (count) {
                1 -> "one"
                2 -> "two"
                4 -> "four"
                6 -> "six"
                8 -> "eight"
                else -> count.toString()
            }
    }
}
