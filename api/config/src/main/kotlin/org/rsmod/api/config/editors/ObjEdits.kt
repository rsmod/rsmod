package org.rsmod.api.config.editors

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.stat.StatType

internal object ObjEdits : ObjEditor() {
    init {
        edit("coins") { param[params.shop_sale_restricted] = true }
        edit("platinum_token") { param[params.shop_sale_restricted] = true }
        edit("chinchompa") {
            param[params.release_note_title] = "Drop all of your chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("red_chinchompa") {
            param[params.release_note_title] = "Drop all of your red chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("black_chinchompa") {
            param[params.release_note_title] = "Drop all of your black chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("snowball") { param[params.player_op5_text] = "Pelt" }

        editSkillCapes()
        editMaxCapes()
        editGeneralStorage()
    }

    private fun editSkillCapes() {
        skillCape(stats.attack, "attack_hood", "attack_cape", "attack_cape_t")
        skillCape(stats.defence, "defence_hood", "defence_cape", "defence_cape_t")
        skillCape(stats.strength, "strength_hood", "strength_cape", "strength_cape_t")
        skillCape(stats.hitpoints, "hitpoints_hood", "hitpoints_cape", "hitpoints_cape_t")
        skillCape(stats.ranged, "ranging_hood", "ranging_cape", "ranging_cape_t")
        skillCape(stats.prayer, "prayer_hood", "prayer_cape", "prayer_cape_t")
        skillCape(stats.magic, "magic_hood", "magic_cape", "magic_cape_t")
        skillCape(stats.cooking, "cooking_hood", "cooking_cape", "cooking_cape_t")
        skillCape(stats.woodcutting, "woodcutting_hood", "woodcutting_cape", "woodcutting_cape_t")
        skillCape(stats.fletching, "fletching_hood", "fletching_cape", "fletching_cape_t")
        skillCape(stats.fishing, "fishing_hood", "fishing_cape", "fishing_cape_t")
        skillCape(stats.firemaking, "firemaking_hood", "firemaking_cape", "firemaking_cape_t")
        skillCape(stats.crafting, "crafting_hood", "crafting_cape", "crafting_cape_t")
        skillCape(stats.smithing, "smithing_hood", "smithing_cape", "smithing_cape_t")
        skillCape(stats.mining, "mining_hood", "mining_cape", "mining_cape_t")
        skillCape(stats.herblore, "herblore_hood", "herblore_cape", "herblore_cape_t")
        skillCape(stats.agility, "agility_hood", "agility_cape", "agility_cape_t")
        skillCape(stats.thieving, "thieving_hood", "thieving_cape", "thieving_cape_t")
        skillCape(stats.slayer, "slayer_hood", "slayer_cape", "slayer_cape_t")
        skillCape(stats.farming, "farming_hood", "farming_cape", "farming_cape_t")
        skillCape(stats.runecrafting, "runecraft_hood", "runecraft_cape", "runecraft_cape_t")
        skillCape(stats.hunter, "hunter_hood", "hunter_cape", "hunter_cape_t")
        skillCape(stats.construction, "construct_hood", "construct_cape", "construct_cape_t")
    }

    private fun skillCape(stat: StatType, hood: String, untrimmed: String, trimmed: String) {
        edit(hood) {
            param[params.statreq1_skill] = stat
            param[params.statreq1_level] = stat.maxLevel
            contentGroup = content.skill_hood
        }

        edit(untrimmed) {
            param[params.statreq1_skill] = stat
            param[params.statreq1_level] = stat.maxLevel
            contentGroup = content.skill_cape
        }

        edit(trimmed) {
            param[params.statreq1_skill] = stat
            param[params.statreq1_level] = stat.maxLevel
            contentGroup = content.skill_cape
        }
    }

    private fun editMaxCapes() {
        maxCape(objs.max_hood, objs.max_cape_worn, objs.max_cape_inv)
    }

    private fun maxCape(hood: ObjType, wornCape: ObjType, invCape: ObjType) {
        edit(invCape.nameValue) {
            contentGroup = content.max_cape
            transformlink = wornCape
        }
        edit(wornCape.nameValue) {
            contentGroup = content.max_cape
            transformlink = invCape
            transformtemplate = objs.template_for_transform
        }
        edit(hood.nameValue) { contentGroup = content.max_hood }
    }

    private fun editGeneralStorage() {
        edit("small_pouch") {
            param[params.bankside_extraop_bit] = 0
            param[params.bankside_extraop_varbit] = varbits.small_pouch_storage_count
        }

        edit("medium_pouch") {
            param[params.bankside_extraop_bit] = 1
            param[params.bankside_extraop_varbit] = varbits.medium_pouch_storage_count
        }
        edit("medium_pouch_5511") {
            param[params.bankside_extraop_bit] = 1
            param[params.bankside_extraop_varbit] = varbits.medium_pouch_storage_count
        }

        edit("large_pouch") {
            param[params.bankside_extraop_bit] = 2
            param[params.bankside_extraop_varbit] = varbits.large_pouch_storage_count
        }
        edit("large_pouch_5513") {
            param[params.bankside_extraop_bit] = 2
            param[params.bankside_extraop_varbit] = varbits.large_pouch_storage_count
        }

        edit("giant_pouch") {
            param[params.bankside_extraop_bit] = 3
            param[params.bankside_extraop_varbit] = varbits.giant_pouch_storage_count
        }
        edit("giant_pouch_5515") {
            param[params.bankside_extraop_bit] = 3
            param[params.bankside_extraop_varbit] = varbits.giant_pouch_storage_count
        }

        edit("colossal_pouch") {
            param[params.bankside_extraop_bit] = 4
            param[params.bankside_extraop_varbit] = varbits.colossal_pouch_storage_count
        }
        edit("colossal_pouch_degraded") {
            param[params.bankside_extraop_bit] = 4
            param[params.bankside_extraop_varbit] = varbits.colossal_pouch_storage_count
        }

        edit("coal_bag") {
            param[params.bankside_extraop_bit] = 5
            param[params.bankside_extraop_varbit] = varbits.coal_bag_storage_count
            param[params.bankside_extraop_flip] = true
        }
        edit("open_coal_bag") {
            param[params.bankside_extraop_bit] = 5
            param[params.bankside_extraop_varbit] = varbits.coal_bag_storage_count
            param[params.bankside_extraop_flip] = true
        }
    }
}
