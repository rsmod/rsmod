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
    val emote_list = find("emote_tab_com2", 904485991309646478)
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
            this[stats.attack] = spotanims.attack_skillcape
            this[stats.defence] = spotanims.defence_skillcape
            this[stats.strength] = spotanims.strength_skillcape
            this[stats.hitpoints] = spotanims.hitpoints_skillcape
            this[stats.ranged] = spotanims.ranged_skillcape
            this[stats.prayer] = spotanims.prayer_skillcape
            this[stats.magic] = spotanims.magic_skillcape
            this[stats.cooking] = spotanims.cooking_skillcape
            this[stats.woodcutting] = spotanims.woodcutting_skillcape
            this[stats.fletching] = spotanims.fletching_skillcape
            this[stats.fishing] = spotanims.fishing_skillcape
            this[stats.firemaking] = spotanims.firemaking_skillcape
            this[stats.crafting] = spotanims.crafting_skillcape
            this[stats.smithing] = spotanims.smithing_skillcape
            this[stats.mining] = spotanims.mining_skillcape
            this[stats.herblore] = spotanims.herblore_skillcape
            this[stats.agility] = spotanims.agility_skillcape
            this[stats.thieving] = spotanims.thieving_skillcape
            this[stats.slayer] = spotanims.slayer_skillcape
            this[stats.farming] = spotanims.farming_skillcape
            this[stats.runecrafting] = spotanims.runecrafting_skillcape
            this[stats.hunter] = spotanims.hunter_skillcape
            this[stats.construction] = spotanims.construction_skillcape
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
