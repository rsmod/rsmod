@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varp.VarpReferences

typealias varps = BaseVarps

object BaseVarps : VarpReferences() {
    val generic_261 = find("generic_varp_261", 4429288)
    val generic_262 = find("generic_varp_262", 4429289)
    val generic_263 = find("generic_varp_263", 4429290)

    val player_run = find("player_run", 6245048)
    val player_attack_option = find("player_attack_option", 8742773)
    val npc_attack_option = find("npc_attack_option", 9650896)
    val sound_effect_volume = find("sound_effect_volume", 4429196)

    val cooks_assistant_progress = find("cooks_assistant_progress", 4429056)
    val dorics_quest_progress = find("dorics_quest_progress", 4429058)
    val ernest_the_chicken_progress = find("ernest_the_chicken_progress", 4429059)
    val rune_mysteries_progress = find("rune_mysteries_progress", 4429090)
    val witchs_potion_progress = find("witchs_potion_progress", 4429094)
    val pirates_treasure_progress = find("pirates_treasure_progress", 4429098)
    val romeo_and_juliet_progress = find("romeo_and_juliet_progress", 4429171)
    val imp_catcher_progress = find("imp_catcher_progress", 4429187)
    val dragon_slayer_i_progress = find("dragon_slayer_i_progress", 4429203)
    val vampyre_slayer_progress = find("vampyre_slayer_progress", 4429205)
    val sheep_shearer_progress = find("sheep_shearer_progress", 4429206)

    val fortis_colosseum_glory_highscore = find("fortis_colosseum_glory_highscore", 4433157)

    /* Server-side only types */
    val inv_capacity_65530 = find("inv_capacity_65530")
    val generic_storage_65531 = find("generic_storage_65531")
}
