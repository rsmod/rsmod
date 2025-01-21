package org.rsmod.content.interfaces.emotes

import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.stats
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType

internal typealias emote_components = EmoteComponents

internal typealias emote_enums = EmoteEnums

internal object EmoteComponents : ComponentReferences() {
    val emote_list = find("emote_tab_com2")
}

internal object EmoteEnums : EnumReferences() {
    val emote_names = find<Int, String>("emote_names")
    val skill_cape_anims = find<StatType, SeqType>("skill_cape_anims")
    val skill_cape_spots = find<StatType, SpotanimType>("skill_cape_spots")
}

internal object EmoteEnumBuilds : EnumBuilder() {
    init {
        build<StatType, SeqType>("skill_cape_anims") {
            this[stats.attack] = seqs.attack_skillcape
            this[stats.defence] = seqs.defence_skillcape
            this[stats.strength] = seqs.strength_skillcape
            this[stats.hitpoints] = seqs.hitpoints_skillcape
            this[stats.ranged] = seqs.ranged_skillcape
            this[stats.prayer] = seqs.prayer_skillcape
            this[stats.magic] = seqs.magic_skillcape
            this[stats.cooking] = seqs.cooking_skillcape
            this[stats.woodcutting] = seqs.woodcutting_skillcape
            this[stats.fletching] = seqs.fletching_skillcape
            this[stats.fishing] = seqs.fishing_skillcape
            this[stats.firemaking] = seqs.firemaking_skillcape
            this[stats.crafting] = seqs.crafting_skillcape
            this[stats.smithing] = seqs.smithing_skillcape
            this[stats.mining] = seqs.mining_skillcape
            this[stats.herblore] = seqs.herblore_skillcape
            this[stats.agility] = seqs.agility_skillcape
            this[stats.thieving] = seqs.thieving_skillcape
            this[stats.slayer] = seqs.slayer_skillcape
            this[stats.farming] = seqs.farming_skillcape
            this[stats.runecrafting] = seqs.runecrafting_skillcape
            this[stats.hunter] = seqs.hunter_skillcape
            this[stats.construction] = seqs.construction_skillcape
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
