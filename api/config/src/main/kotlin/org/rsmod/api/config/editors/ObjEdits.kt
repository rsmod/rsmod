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
        edit("platinum") { param[params.shop_sale_restricted] = true }
        edit("chinchompa_captured") {
            param[params.release_note_title] = "Drop all of your chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("chinchompa_big_captured") {
            param[params.release_note_title] = "Drop all of your red chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("chinchompa_black") {
            param[params.release_note_title] = "Drop all of your black chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit("gublinch_snowball") { param[params.player_op5_text] = "Pelt" }

        editSkillCapes()
        editMaxCapes()
        editGeneralStorage()
        editBoneWeapons()
    }

    private fun editSkillCapes() {
        skillCape(
            stats.attack,
            "skillcape_attack_hood",
            "skillcape_attack",
            "skillcape_attack_trimmed",
        )
        skillCape(
            stats.defence,
            "skillcape_defence_hood",
            "skillcape_defence",
            "skillcape_defence_trimmed",
        )
        skillCape(
            stats.strength,
            "skillcape_strength_hood",
            "skillcape_strength",
            "skillcape_strength_trimmed",
        )
        skillCape(
            stats.hitpoints,
            "skillcape_hitpoints_hood",
            "skillcape_hitpoints",
            "skillcape_hitpoints_trimmed",
        )
        skillCape(
            stats.ranged,
            "skillcape_ranging_hood",
            "skillcape_ranging",
            "skillcape_ranging_trimmed",
        )
        skillCape(
            stats.prayer,
            "skillcape_prayer_hood",
            "skillcape_prayer",
            "skillcape_prayer_trimmed",
        )
        skillCape(stats.magic, "skillcape_magic_hood", "skillcape_magic", "skillcape_magic_trimmed")
        skillCape(
            stats.cooking,
            "skillcape_cooking_hood",
            "skillcape_cooking",
            "skillcape_cooking_trimmed",
        )
        skillCape(
            stats.woodcutting,
            "skillcape_woodcutting_hood",
            "skillcape_woodcutting",
            "skillcape_woodcutting_trimmed",
        )
        skillCape(
            stats.fletching,
            "skillcape_fletching_hood",
            "skillcape_fletching",
            "skillcape_fletching_trimmed",
        )
        skillCape(
            stats.fishing,
            "skillcape_fishing_hood",
            "skillcape_fishing",
            "skillcape_fishing_trimmed",
        )
        skillCape(
            stats.firemaking,
            "skillcape_firemaking_hood",
            "skillcape_firemaking",
            "skillcape_firemaking_trimmed",
        )
        skillCape(
            stats.crafting,
            "skillcape_crafting_hood",
            "skillcape_crafting",
            "skillcape_crafting_trimmed",
        )
        skillCape(
            stats.smithing,
            "skillcape_smithing_hood",
            "skillcape_smithing",
            "skillcape_smithing_trimmed",
        )
        skillCape(
            stats.mining,
            "skillcape_mining_hood",
            "skillcape_mining",
            "skillcape_mining_trimmed",
        )
        skillCape(
            stats.herblore,
            "skillcape_herblore_hood",
            "skillcape_herblore",
            "skillcape_herblore_trimmed",
        )
        skillCape(
            stats.agility,
            "skillcape_agility_hood",
            "skillcape_agility",
            "skillcape_agility_trimmed",
        )
        skillCape(
            stats.thieving,
            "skillcape_thieving_hood",
            "skillcape_thieving",
            "skillcape_thieving_trimmed",
        )
        skillCape(
            stats.slayer,
            "skillcape_slayer_hood",
            "skillcape_slayer",
            "skillcape_slayer_trimmed",
        )
        skillCape(
            stats.farming,
            "skillcape_farming_hood",
            "skillcape_farming",
            "skillcape_farming_trimmed",
        )
        skillCape(
            stats.runecrafting,
            "skillcape_runecrafting_hood",
            "skillcape_runecrafting",
            "skillcape_runecrafting_trimmed",
        )
        skillCape(
            stats.hunter,
            "skillcape_hunting_hood",
            "skillcape_hunting",
            "skillcape_hunting_trimmed",
        )
        skillCape(
            stats.construction,
            "skillcape_construction_hood",
            "skillcape_construction",
            "skillcape_construction_trimmed",
        )
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
        edit(invCape.internalNameValue) {
            contentGroup = content.max_cape
            transformlink = wornCape
        }
        edit(wornCape.internalNameValue) {
            contentGroup = content.max_cape
            transformlink = invCape
            transformtemplate = objs.template_for_transform
        }
        edit(hood.internalNameValue) { contentGroup = content.max_hood }
    }

    private fun editGeneralStorage() {
        edit("rcu_pouch_small") {
            param[params.bankside_extraop_bit] = 0
            param[params.bankside_extraop_varbit] = varbits.small_pouch_storage_count
        }

        edit("rcu_pouch_medium") {
            param[params.bankside_extraop_bit] = 1
            param[params.bankside_extraop_varbit] = varbits.medium_pouch_storage_count
        }
        edit("rcu_pouch_medium_degrade") {
            param[params.bankside_extraop_bit] = 1
            param[params.bankside_extraop_varbit] = varbits.medium_pouch_storage_count
        }

        edit("rcu_pouch_large") {
            param[params.bankside_extraop_bit] = 2
            param[params.bankside_extraop_varbit] = varbits.large_pouch_storage_count
        }
        edit("rcu_pouch_large_degrade") {
            param[params.bankside_extraop_bit] = 2
            param[params.bankside_extraop_varbit] = varbits.large_pouch_storage_count
        }

        edit("rcu_pouch_giant") {
            param[params.bankside_extraop_bit] = 3
            param[params.bankside_extraop_varbit] = varbits.giant_pouch_storage_count
        }
        edit("rcu_pouch_giant_degrade") {
            param[params.bankside_extraop_bit] = 3
            param[params.bankside_extraop_varbit] = varbits.giant_pouch_storage_count
        }

        edit("rcu_pouch_colossal") {
            param[params.bankside_extraop_bit] = 4
            param[params.bankside_extraop_varbit] = varbits.colossal_pouch_storage_count
        }
        edit("rcu_pouch_colossal_degrade") {
            param[params.bankside_extraop_bit] = 4
            param[params.bankside_extraop_varbit] = varbits.colossal_pouch_storage_count
        }

        edit("coal_bag") {
            param[params.bankside_extraop_bit] = 5
            param[params.bankside_extraop_varbit] = varbits.coal_bag_storage_count
            param[params.bankside_extraop_flip] = true
        }
        edit("coal_bag_open") {
            param[params.bankside_extraop_bit] = 5
            param[params.bankside_extraop_varbit] = varbits.coal_bag_storage_count
            param[params.bankside_extraop_flip] = true
        }
    }

    private fun editBoneWeapons() {
        boneWeapon("dttd_bone_dagger")
        boneWeapon("dttd_bone_dagger_p")
        boneWeapon("dttd_bone_dagger_p+")
        boneWeapon("dttd_bone_dagger_p++")
        boneWeapon("cave_goblin_bone_spear")
        boneWeapon("cave_goblin_bone_club")
        boneWeapon("dttd_bone_crossbow")
        boneWeapon("dttd_bone_crossbow_bolt")
    }

    private fun boneWeapon(internal: String) {
        edit(internal) { param[params.bone_weapon] = 1 }
    }
}
