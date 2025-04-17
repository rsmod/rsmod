package org.rsmod.content.interfaces.emotes

import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.stats
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.builders.varbit.VarBitBuilder
import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.builders.walktrig.WalkTriggerBuilder
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.api.type.refs.varp.VarpReferences
import org.rsmod.api.type.refs.walktrig.WalkTriggerReferences
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType

typealias emote_components = EmoteComponents

typealias emote_enums = EmoteEnums

internal typealias emote_varps = EmoteVarps

internal typealias emote_varbits = EmoteVarBits

internal typealias emote_walktriggers = EmoteWalkTriggers

object EmoteComponents : ComponentReferences() {
    val emote_list = find("emote_tab:contents", 904485991309646478)
}

object EmoteEnums : EnumReferences() {
    val emote_names = find<Int, String>("emote_names")
    val skill_cape_anims = find<StatType, SeqType>("skill_cape_anims")
    val skill_cape_spots = find<StatType, SpotanimType>("skill_cape_spots")
}

internal object EmoteVarps : VarpReferences() {
    val emote_counters = find("emote_counters")
    val emote_clock_premier_shield = find("emote_clock_premier_shield")
}

internal object EmoteVarBits : VarBitReferences() {
    val emote_counters_crazy_dance = find("emote_counters_crazy_dance")
    val emote_counters_premier_shield = find("emote_counters_premier_shield")
}

internal object EmoteWalkTriggers : WalkTriggerReferences() {
    val cancelanim = find("emote_cancelanim")
}

internal object EmoteEnumBuilds : EnumBuilder() {
    init {
        build<StatType, SeqType>("skill_cape_anims") {
            this[stats.attack] = seqs.skill_cape_attack
            this[stats.defence] = seqs.skill_cape_defend
            this[stats.strength] = seqs.skillcapes_human_strength
            this[stats.hitpoints] = seqs.skillcapes_human_hitpoints
            this[stats.ranged] = seqs.skillcapes_human_range
            this[stats.prayer] = seqs.skillcapes_human_prayer
            this[stats.magic] = seqs.skillcapes_player_magic
            this[stats.cooking] = seqs.skillcapes_cooking_player_anim
            this[stats.woodcutting] = seqs.skillcapes_woodcutting_player_anim
            this[stats.fletching] = seqs.skillcapes_player_fletching_bow
            this[stats.fishing] = seqs.skillcapes_fishing_player_anim
            this[stats.firemaking] = seqs.skillcapes_human_firemaking
            this[stats.crafting] = seqs.skillcapes_crafting_player_anim
            this[stats.smithing] = seqs.skillcapes_player_smithing
            this[stats.mining] = seqs.skillcapes_player_mining
            this[stats.herblore] = seqs.skillcapes_human_herblore
            this[stats.agility] = seqs.skillcapes_human_agility
            this[stats.thieving] = seqs.skill_cape_thieving
            this[stats.slayer] = seqs.skill_cape_slayer
            this[stats.farming] = seqs.skillcape_farming
            this[stats.runecrafting] = seqs.skillcapes_player_runecrafting
            this[stats.hunter] = seqs.skillcapes_human_hunting
            this[stats.construction] = seqs.skillcapes_construction_player_anim
        }

        build<StatType, SpotanimType>("skill_cape_spots") {
            this[stats.attack] = spotanims.skillcape_attack_spotanim
            this[stats.defence] = spotanims.skillcape_defend_spotanim
            this[stats.strength] = spotanims.skillcapes_strength
            this[stats.hitpoints] = spotanims.skillcapes_hitpoints
            this[stats.ranged] = spotanims.skillcapes_range
            this[stats.prayer] = spotanims.skillcapes_prayer
            this[stats.magic] = spotanims.skillcapes_magic_spotanim
            this[stats.cooking] = spotanims.skillcapes_cooking_spotanim
            this[stats.woodcutting] = spotanims.skillcapes_woodcutting_spotanim
            this[stats.fletching] = spotanims.skillcapes_fletching_bow_spotanim
            this[stats.fishing] = spotanims.skillcapes_fishing_spotanim
            this[stats.firemaking] = spotanims.skillcapes_firemaking
            this[stats.crafting] = spotanims.skillcapes_crafting_spotanim
            this[stats.smithing] = spotanims.skillcapes_smithing_spotanim
            this[stats.mining] = spotanims.skillcapes_mining_spotanim
            this[stats.herblore] = spotanims.skillcapes_herblore
            this[stats.agility] = spotanims.skillcapes_agility
            this[stats.thieving] = spotanims.skillcape_thieving_spotanim
            this[stats.slayer] = spotanims.skillcape_slayer_spotanim
            this[stats.farming] = spotanims.skillcape_farming_spotanim
            this[stats.runecrafting] = spotanims.skillcapes_runecrafting_spotanim
            this[stats.hunter] = spotanims.skillcapes_hunting
            this[stats.construction] = spotanims.skillcapes_construction_spotanim
        }
    }
}

internal object EmoteVarpBuilds : VarpBuilder() {
    init {
        build("emote_counters")
        build("emote_clock_premier_shield")
    }
}

internal object EmoteVarBitBuilds : VarBitBuilder() {
    init {
        build("emote_counters_crazy_dance") {
            baseVar = emote_varps.emote_counters
            startBit = 0
            endBit = 0
        }

        build("emote_counters_premier_shield") {
            baseVar = emote_varps.emote_counters
            startBit = 1
            endBit = 2
        }
    }
}

internal object EmoteWalkTriggerBuilds : WalkTriggerBuilder() {
    init {
        build("emote_cancelanim")
    }
}
