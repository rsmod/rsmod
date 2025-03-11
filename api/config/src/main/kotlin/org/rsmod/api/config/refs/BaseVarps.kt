@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varp.VarpReferences

typealias varps = BaseVarps

object BaseVarps : VarpReferences() {
    /*
     * These "generic" temporary-state varps are used across multiple features to track temporary
     * state. Unlike varps tied to a specific piece of content with child varbits, these are more
     * general-purpose.
     */
    val generic_temp_state_261 = find("generic_temp_state_261", 59242162)
    val generic_temp_state_262 = find("generic_temp_state_262", 59469143)
    val generic_temp_state_263 = find("generic_temp_state_263", 59696124)

    val temp_state_675 = find("temp_state_675", 153212296)

    val attackstyle = find("attackstyle", 9764025)
    // Note: This varp seems to only be transmitted while wielding melee weapons and correlates to
    // Controlled (0), Accurate (1), Aggressive (2), and Defensive (3).
    // Though it may be the case that it actually represents the current "XP" type being granted.
    val attackstyle_melee = find("attackstyle_melee", 10444968)
    val auto_retaliate = find("auto_retaliate", 39044574)
    val sa_energy = find("sa_energy", 68098142)
    val sa_type = find("sa_type", 68325123)
    val soulreaper_souls = find("soulreaper_axe_soul_stack", 858899946)

    val player_run = find("player_run", 39271563)
    val player_attack_option = find("player_attack_option", 251271828)
    val npc_attack_option = find("npc_attack_option", 296441051)
    val sound_effect_volume = find("sound_effect_volume", 38363631)

    val cooks_assistant_progress = find("cooks_assistant_progress", 6586291)
    val dorics_quest_progress = find("dorics_quest_progress", 7040253)
    val ernest_the_chicken_progress = find("ernest_the_chicken_progress", 7267234)
    val rune_mysteries_progress = find("rune_mysteries_progress", 14303645)
    val witchs_potion_progress = find("witchs_potion_progress", 15211569)
    val pirates_treasure_progress = find("pirates_treasure_progress", 16119493)
    val romeo_and_juliet_progress = find("romeo_and_juliet_progress", 32689106)
    val imp_catcher_progress = find("imp_catcher_progress", 36320802)
    val dragon_slayer_i_progress = find("dragon_slayer_i_progress", 39952498)
    val vampyre_slayer_progress = find("vampyre_slayer_progress", 40406460)
    val sheep_shearer_progress = find("sheep_shearer_progress", 40633441)

    val fortis_colosseum_glory_highscore = find("fortis_colosseum_glory_highscore", 937435372)

    /* Server-side only types */
    val com_maxhit = find("com_maxhit")
    val forinthry_surge_expiration = find("forinthry_surge_expiration")
    val saved_autocast_state_staff = find("saved_autocast_state_staff")
    val saved_autocast_state_bladed_staff = find("saved_autocast_state_bladed_staff")
    val lastcombat = find("lastcombat")
    val lastcombat_pvp = find("lastcombat_pvp")
    val aggressive_npc = find("aggressive_npc")
    val generic_temp_coords_65529 = find("generic_temp_coords_65529")
    val inv_capacity_65530 = find("inv_capacity_65530")
    val generic_storage_65531 = find("generic_storage_65531")

    /*
     * "Restore" varps serve as temporary storage for varps that are modified temporarily and need
     * to be restored later.
     */
    val temp_restore_65527 = find("temp_restore_65527")
}
