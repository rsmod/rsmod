@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.obj.ObjType

public typealias objs = BaseObjs

public object BaseObjs : ObjReferences() {
    public val template_for_cert: ObjType = find("template_for_cert")
    public val template_for_placeholder: ObjType = find("template_for_placeholder")

    public val coins: ObjType = find("coins")

    public val helm_of_neitiznot: ObjType = find("helm_of_neitiznot")
    public val fire_cape: ObjType = find("fire_cape")
    public val amulet_of_fury: ObjType = find("amulet_of_fury")
    public val abyssal_whip: ObjType = find("abyssal_whip")
    public val bandos_chestplate: ObjType = find("bandos_chestplate")
    public val dragon_defender: ObjType = find("dragon_defender")
    public val bandos_tassets: ObjType = find("bandos_tassets")
    public val barrows_gloves: ObjType = find("barrows_gloves")
    public val dragon_boots: ObjType = find("dragon_boots")
    public val berserker_ring: ObjType = find("berserker_ring")
    public val rune_arrow: ObjType = find("rune_arrow")

    public val pot_empty: ObjType = find("pot_empty")
    public val jug_empty: ObjType = find("jug_empty")
    public val pack_jug_empty: ObjType = find("pack_jug_empty")
    public val shears: ObjType = find("shears")
    public val knife: ObjType = find("knife")
    public val bucket_empty: ObjType = find("bucket_empty")
    public val pack_bucket: ObjType = find("pack_bucket")
    public val bowl_empty: ObjType = find("bowl_empty")
    public val cake_tin: ObjType = find("cake_tin")
    public val tinderbox: ObjType = find("tinderbox")
    public val chisel: ObjType = find("chisel")
    public val spade: ObjType = find("spade")
    public val hammer: ObjType = find("hammer")
    public val newcomer_map: ObjType = find("newcomer_map")
    public val sos_security_book: ObjType = find("sos_security_book")

    public val bobs_axe_flyer: ObjType = find("bobs_axe_flyer")
    public val bronze_pickaxe: ObjType = find("bronze_pickaxe")
    public val iron_battleaxe: ObjType = find("iron_battleaxe")
    public val steel_battleaxe: ObjType = find("steel_battleaxe")
    public val mithril_battleaxe: ObjType = find("mithril_battleaxe")
    public val beer: ObjType = find("beer")
    public val charcoal: ObjType = find("charcoal")

    public val logs: ObjType = find("logs")
    public val oak_logs: ObjType = find("oak_logs")
    public val willow_logs: ObjType = find("willow_logs")
    public val maple_logs: ObjType = find("maple_logs")
    public val yew_logs: ObjType = find("yew_logs")
    public val magic_logs: ObjType = find("magic_logs")
    public val redwood_logs: ObjType = find("redwood_logs")
    public val teak_logs: ObjType = find("teak_logs")
    public val mahogany_logs: ObjType = find("mahogany_logs")
    public val achey_tree_logs: ObjType = find("achey_tree_logs")
    public val arctic_pine_logs: ObjType = find("arctic_pine_logs")
    public val juniper_logs: ObjType = find("juniper_logs")
    public val bark: ObjType = find("bark")

    public val bronze_axe: ObjType = find("bronze_axe")
    public val iron_axe: ObjType = find("iron_axe")
    public val steel_axe: ObjType = find("steel_axe")
    public val black_axe: ObjType = find("black_axe")
    public val mithril_axe: ObjType = find("mithril_axe")
    public val adamant_axe: ObjType = find("adamant_axe")
    public val blessed_axe: ObjType = find("blessed_axe")
    public val rune_axe: ObjType = find("rune_axe")
    public val gilded_axe: ObjType = find("gilded_axe")
    public val dragon_axe: ObjType = find("dragon_axe")
    public val dragon_axe_or: ObjType = find("dragon_axe_or")
    public val third_age_axe: ObjType = find("3rd_age_axe")
    public val infernal_axe: ObjType = find("infernal_axe")
    public val infernal_axe_or: ObjType = find("infernal_axe_or")
    public val crystal_axe: ObjType = find("crystal_axe")

    public val woodcutting_icon: ObjType = find("dummyitem_woodcutting_icon", 520313320359048412)
    public val bank_icon: ObjType = find("dummyitem_bank_icon", 520313320359043779)
    public val furnace_icon: ObjType = find("dummyitem_furnace_icon", 520313320359043783)
    public val mining_icon: ObjType = find("dummyitem_mining_icon", 520313320359061270)
    public val smithing_icon: ObjType = find("dummyitem_smithing_icon", 520313320359061273)
}
