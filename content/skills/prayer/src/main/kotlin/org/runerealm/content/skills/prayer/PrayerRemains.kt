@file:Suppress("SpellCheckingInspection")

package org.runerealm.content.skills.prayer

/**
 * Prayer experience for burying bones and scattering ashes, keyed by obj internal name. Values are
 * from the OSRS Wiki ([Prayer training](https://oldschool.runescape.wiki/w/Prayer_training) and the
 * individual item pages).
 */
internal val BONES_XP: Map<String, Double> =
    mapOf(
        "bones" to 4.5,
        "bones_burnt" to 4.5,
        "wolf_bones" to 4.5,
        "mm_normal_monkey_bones" to 5.0,
        "bat_bones" to 5.3,
        "big_bones" to 15.0,
        "tbwt_jogre_bones" to 15.0,
        "babywyrm_bones" to 21.0,
        "zogre_bones" to 22.5,
        "tbwt_beast_bones" to 25.0,
        "babydragon_bones" to 30.0,
        "wyrm_bones" to 50.0,
        "dragon_bones" to 72.0,
        "wyvern_bones" to 72.0,
        "drake_bones" to 80.0,
        "zogre_ancestral_bones_fayg" to 84.0,
        "lava_dragon_bones" to 85.0,
        "zogre_ancestral_bones_raurg" to 96.0,
        "hydra_bones" to 110.0,
        "dagannoth_king_bones" to 125.0,
        "zogre_ancestral_bones_ourg" to 140.0,
        "dragon_bones_superior" to 150.0,
    )

internal val ASHES_XP: Map<String, Double> =
    mapOf(
        "fiendish_ashes" to 10.0,
        "vile_ashes" to 25.0,
        "malicious_ashes" to 65.0,
        "abyssal_ashes" to 85.0,
        "infernal_ashes" to 110.0,
    )
