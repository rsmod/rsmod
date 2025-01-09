package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varp.VarpReferences
import org.rsmod.game.type.varp.VarpType

public typealias varps = BaseVarps

public object BaseVarps : VarpReferences() {
    public val player_run: VarpType = find("player_run", 6245048)
    public val player_attack_option: VarpType = find("player_attack_option", 8742773)
    public val npc_attack_option: VarpType = find("npc_attack_option", 9650896)

    public val cooks_assistant_progress: VarpType = find("cooks_assistant_progress", 4429056)
    public val dorics_quest_progress: VarpType = find("dorics_quest_progress", 4429058)
    public val ernest_the_chicken_progress: VarpType = find("ernest_the_chicken_progress", 4429059)
    public val rune_mysteries_progress: VarpType = find("rune_mysteries_progress", 4429090)
    public val witchs_potion_progress: VarpType = find("witchs_potion_progress", 4429094)
    public val pirates_treasure_progress: VarpType = find("pirates_treasure_progress", 4429098)
    public val romeo_and_juliet_progress: VarpType = find("romeo_and_juliet_progress", 4429171)
    public val imp_catcher_progress: VarpType = find("imp_catcher_progress", 4429187)
    public val dragon_slayer_i_progress: VarpType = find("dragon_slayer_i_progress", 4429203)
    public val vampyre_slayer_progress: VarpType = find("vampyre_slayer_progress", 4429205)
    public val sheep_shearer_progress: VarpType = find("sheep_shearer_progress", 4429206)
}
