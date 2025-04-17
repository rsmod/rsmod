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
        edit(objs.coins) { param[params.shop_sale_restricted] = true }
        edit(objs.platinum_tokens) { param[params.shop_sale_restricted] = true }
        edit(objs.chinchoma) {
            param[params.release_note_title] = "Drop all of your chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit(objs.red_chinchoma) {
            param[params.release_note_title] = "Drop all of your red chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit(objs.black_chinchompa) {
            param[params.release_note_title] = "Drop all of your black chinchompas?"
            param[params.release_note_message] = "You release the chinchompa and it bounds away."
        }
        edit(objs.snowball) { param[params.player_op5_text] = "Pelt" }

        editSkillCapes()
        editMaxCapes()
        editGeneralStorage()
        editBoneWeapons()
    }

    private fun editSkillCapes() {
        skillcape(stats.attack, objs.attack_hood, objs.attack_skillcape, objs.attack_skillcape_t)
        skillcape(
            stats.defence,
            objs.defence_hood,
            objs.defence_skillcape,
            objs.defence_skillcape_t,
        )
        skillcape(
            stats.strength,
            objs.strength_hood,
            objs.strength_skillcape,
            objs.strength_skillcape_t,
        )
        skillcape(
            stats.hitpoints,
            objs.hitpoints_hood,
            objs.hitpoints_skillcape,
            objs.hitpoints_skillcape_t,
        )
        skillcape(stats.ranged, objs.ranged_hood, objs.ranged_skillcape, objs.ranged_skillcape_t)
        skillcape(stats.prayer, objs.prayer_hood, objs.prayer_skillcape, objs.prayer_skillcape_t)
        skillcape(stats.magic, objs.magic_hood, objs.magic_skillcape, objs.magic_skillcape_t)
        skillcape(
            stats.cooking,
            objs.cooking_hood,
            objs.cooking_skillcape,
            objs.cooking_skillcape_t,
        )
        skillcape(
            stats.woodcutting,
            objs.woodcutting_hood,
            objs.woodcutting_skillcape,
            objs.woodcutting_skillcape_t,
        )
        skillcape(
            stats.fletching,
            objs.fletching_hood,
            objs.fletching_skillcape,
            objs.fletching_skillcape_t,
        )
        skillcape(
            stats.fishing,
            objs.fishing_hood,
            objs.fishing_skillcape,
            objs.fishing_skillcape_t,
        )
        skillcape(
            stats.firemaking,
            objs.firemaking_hood,
            objs.firemaking_skillcape,
            objs.firemaking_skillcape_t,
        )
        skillcape(
            stats.crafting,
            objs.crafting_hood,
            objs.crafting_skillcape,
            objs.crafting_skillcape_t,
        )
        skillcape(
            stats.smithing,
            objs.smithing_hood,
            objs.smithing_skillcape,
            objs.smithing_skillcape_t,
        )
        skillcape(stats.mining, objs.mining_hood, objs.mining_skillcape, objs.mining_skillcape_t)
        skillcape(
            stats.herblore,
            objs.herblore_hood,
            objs.herblore_skillcape,
            objs.herblore_skillcape_t,
        )
        skillcape(
            stats.agility,
            objs.agility_hood,
            objs.agility_skillcape,
            objs.agility_skillcape_t,
        )
        skillcape(
            stats.thieving,
            objs.thieving_hood,
            objs.thieving_skillcape,
            objs.thieving_skillcape_t,
        )
        skillcape(stats.slayer, objs.slayer_hood, objs.slayer_skillcape, objs.slayer_skillcape_t)
        skillcape(
            stats.farming,
            objs.farming_hood,
            objs.farming_skillcape,
            objs.farming_skillcape_t,
        )
        skillcape(
            stats.runecrafting,
            objs.runecrafting_hood,
            objs.runecrafting_skillcape,
            objs.runecrafting_skillcape_t,
        )
        skillcape(stats.hunter, objs.hunter_hood, objs.hunter_skillcape, objs.hunter_skillcape_t)
        skillcape(
            stats.construction,
            objs.construction_hood,
            objs.construction_skillcape,
            objs.construction_skillcape_t,
        )
    }

    private fun skillcape(stat: StatType, hood: ObjType, untrimmed: ObjType, trimmed: ObjType) {
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
        edit(invCape) {
            contentGroup = content.max_cape
            transformlink = wornCape
        }
        edit(wornCape) {
            contentGroup = content.max_cape
            transformlink = invCape
            transformtemplate = objs.template_for_transform
        }
        edit(hood) { contentGroup = content.max_hood }
    }

    private fun editGeneralStorage() {
        edit(objs.rcu_pouch_small) {
            param[params.bankside_extraop_bit] = 0
            param[params.bankside_extraop_varbit] = varbits.small_pouch_storage_count
        }

        edit(objs.rcu_pouch_medium) {
            param[params.bankside_extraop_bit] = 1
            param[params.bankside_extraop_varbit] = varbits.medium_pouch_storage_count
        }
        edit(objs.rcu_pouch_medium_degrade) {
            param[params.bankside_extraop_bit] = 1
            param[params.bankside_extraop_varbit] = varbits.medium_pouch_storage_count
        }

        edit(objs.rcu_pouch_large) {
            param[params.bankside_extraop_bit] = 2
            param[params.bankside_extraop_varbit] = varbits.large_pouch_storage_count
        }
        edit(objs.rcu_pouch_large_degrade) {
            param[params.bankside_extraop_bit] = 2
            param[params.bankside_extraop_varbit] = varbits.large_pouch_storage_count
        }

        edit(objs.rcu_pouch_giant) {
            param[params.bankside_extraop_bit] = 3
            param[params.bankside_extraop_varbit] = varbits.giant_pouch_storage_count
        }
        edit(objs.rcu_pouch_giant_degrade) {
            param[params.bankside_extraop_bit] = 3
            param[params.bankside_extraop_varbit] = varbits.giant_pouch_storage_count
        }

        edit(objs.rcu_pouch_colossal) {
            param[params.bankside_extraop_bit] = 4
            param[params.bankside_extraop_varbit] = varbits.colossal_pouch_storage_count
        }
        edit(objs.rcu_pouch_colossal_degrade) {
            param[params.bankside_extraop_bit] = 4
            param[params.bankside_extraop_varbit] = varbits.colossal_pouch_storage_count
        }

        edit(objs.coal_bag) {
            param[params.bankside_extraop_bit] = 5
            param[params.bankside_extraop_varbit] = varbits.coal_bag_storage_count
            param[params.bankside_extraop_flip] = true
        }
        edit(objs.coal_bag_open) {
            param[params.bankside_extraop_bit] = 5
            param[params.bankside_extraop_varbit] = varbits.coal_bag_storage_count
            param[params.bankside_extraop_flip] = true
        }
    }

    private fun editBoneWeapons() {
        boneWeapon(objs.bone_dagger)
        boneWeapon(objs.bone_dagger_poison)
        boneWeapon(objs.bone_dagger_poison_plus)
        boneWeapon(objs.bone_dagger_poison_plus_plus)
        boneWeapon(objs.bone_spear)
        boneWeapon(objs.bone_club)
        boneWeapon(objs.bone_crossbow)
        boneWeapon(objs.bone_bolts)
    }

    private fun boneWeapon(type: ObjType) {
        edit(type) { param[params.bone_weapon] = 1 }
    }
}
