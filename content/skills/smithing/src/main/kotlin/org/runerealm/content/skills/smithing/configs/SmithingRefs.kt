package org.runerealm.content.skills.smithing.configs

import org.runerealm.api.type.builders.varp.VarpBuilder
import org.runerealm.api.type.refs.category.CategoryReferences
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.queue.QueueReferences
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.game.type.obj.ObjType

typealias smithing_objs = SmithingObjs

object SmithingObjs : ObjReferences() {
    val copper_ore = find("copper_ore")
    val tin_ore = find("tin_ore")
    val blurite_ore = find("blurite_ore")
    val iron_ore = find("iron_ore")
    val silver_ore = find("silver_ore")
    val coal = find("coal")
    val gold_ore = find("gold_ore")
    val mithril_ore = find("mithril_ore")
    val adamantite_ore = find("adamantite_ore")
    val runite_ore = find("runite_ore")

    val bronze_bar = find("bronze_bar")
    val blurite_bar = find("blurite_bar")
    val iron_bar = find("iron_bar")
    val silver_bar = find("silver_bar")
    val steel_bar = find("steel_bar")
    val gold_bar = find("gold_bar")
    val mithril_bar = find("mithril_bar")
    val adamantite_bar = find("adamantite_bar")
    val runite_bar = find("runite_bar")

    val hammer = find("hammer")
    val imcando_hammer = find("imcando_hammer")
    val ring_of_forging = find("ring_of_forging")
    val goldsmith_gauntlets = find("gauntlets_of_goldsmithing")
}

object SmithingInterfaces : InterfaceReferences() {
    val smithing = find("smithing")
}

object SmithingComponents : ComponentReferences() {
    val make_1 = find("smithing:make_1")
    val make_5 = find("smithing:make_5")
    val make_10 = find("smithing:make_10")
    val make_x = find("smithing:make_x")
    val make_all = find("smithing:make_all")
}

object SmithingVarBits : VarBitReferences() {
    /** The bar shown on the `smithing` interface: an index of `enum_1253` (1 = bronze). */
    val bar_type = find("smithing_bar_type")
    val bar_lasttype = find("smithing_bar_lasttype")
    val item_lasttype = find("smithing_item_lasttype")
}

object SmithingVarps : VarpReferences() {
    /** The quantity selected on make-x (`skillmain`) interfaces, such as the smithing one. */
    val makex_quantity = find("makexcrafting")

    /** Server-side: ring of forging charges used. Charges are tied to the player. */
    val ring_of_forging_charges = find("ring_of_forging_charges")
}

internal object SmithingVarpBuilder : VarpBuilder() {
    init {
        build("ring_of_forging_charges")
    }
}

object SmithingSeqs : SeqReferences() {
    val smithing = find("human_smithing")
    val furnace = find("human_furnace")
}

object SmithingCategories : CategoryReferences() {
    val furnace = find("furnace")
    val anvil = find("anvil")
}

object SmithingLocs : LocReferences() {
    // Furnaces and anvils that are not in the `furnace`/`anvil` cache categories.
    val dwarf_keldagrim_furnace = find("dwarf_keldagrim_furnace")
    val enakh_furnace = find("enakh_new_furnace_lit")
    val burgh_furnace = find("burgh_furnace_fired")
    val zq_furnace = find("zqfurnace_lit")
    val lovaquest_furnace = find("lovaquest_tower_furnace_lit")
    val cof_furnace = find("cof_furnace")
    val brut_anvil = find("brut_anvil")
    val ds2_forge_anvil = find("ds2_ac_forge_anvil")
    val chasm_anvil = find("chasm_anvil")

    /** Locs in the `anvil` category that are not regular anvils. */
    val camdozaal_anvil = find("camdozaal_anvil")

    /** The rusted anvil in Lumbridge, which only smiths bronze. */
    val lumbridge_anvil = find("lumbridge_anvil")
}

object SmithingEnums : EnumReferences() {
    val product_quantity = find<ObjType, Int>("smithing_product_to_quantity")
    val product_bars = find<ObjType, Int>("smithing_product_to_bars_required")
    val product_level = find<ObjType, Int>("smithing_product_to_requirement")
}

object SmithingQueues : QueueReferences() {
    val smelt = find("smelting")
    val smith = find("smithing")
}
