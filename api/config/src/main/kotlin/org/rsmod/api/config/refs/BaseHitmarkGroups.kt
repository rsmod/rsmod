package org.rsmod.api.config.refs

import org.rsmod.game.type.hitmark.HitmarkTypeGroup

typealias hitmarks = BaseHitmarkGroups

private typealias basehits = BaseHitmarks

object BaseHitmarkGroups {
    val corruption = HitmarkTypeGroup(basehits.corruption)
    val ironman_blocked = HitmarkTypeGroup(basehits.ironman_blocked)
    val disease = HitmarkTypeGroup(basehits.disease)
    val venom = HitmarkTypeGroup(basehits.venom)
    val heal = HitmarkTypeGroup(basehits.heal)

    val alt_charge =
        HitmarkTypeGroup(lit = basehits.alt_charge_lit, tint = basehits.alt_charge_tint)

    val zero_damage =
        HitmarkTypeGroup(lit = basehits.zero_damage_lit, tint = basehits.zero_damage_tint)

    val alt_uncharge =
        HitmarkTypeGroup(lit = basehits.alt_uncharge_lit, tint = basehits.alt_uncharge_tint)

    val regular_damage =
        HitmarkTypeGroup(
            lit = basehits.regular_damage_lit,
            tint = basehits.regular_damage_tint,
            max = basehits.regular_damage_max,
        )

    val shield_damage =
        HitmarkTypeGroup(
            lit = basehits.shield_damage_lit,
            tint = basehits.shield_damage_tint,
            max = basehits.shield_damage_max,
        )

    val zalcano_armour_damage =
        HitmarkTypeGroup(
            lit = basehits.zalcano_armour_damage_lit,
            tint = basehits.zalcano_armour_damage_tint,
            max = basehits.zalcano_armour_damage_max,
        )

    val nightmare_totem_charge =
        HitmarkTypeGroup(
            lit = basehits.nightmare_totem_charge_lit,
            tint = basehits.nightmare_totem_charge_tint,
            max = basehits.nightmare_totem_charge_max,
        )

    val nightmare_totem_uncharge =
        HitmarkTypeGroup(
            lit = basehits.nightmare_totem_uncharge_lit,
            tint = basehits.nightmare_totem_uncharge_tint,
            max = basehits.nightmare_totem_uncharge_max,
        )

    val poise_damage =
        HitmarkTypeGroup(
            lit = basehits.poise_damage_lit,
            tint = basehits.poise_damage_tint,
            max = basehits.poise_damage_max,
        )

    val prayer_drain =
        HitmarkTypeGroup(
            lit = basehits.prayer_drain_lit,
            tint = basehits.prayer_drain_tint,
            max = basehits.prayer_drain_max,
        )

    val poison_damage =
        HitmarkTypeGroup(lit = basehits.poison_damage_lit, tint = basehits.poison_damage_tint)

    val bleed = HitmarkTypeGroup(basehits.bleed)
    val sanity_drain = HitmarkTypeGroup(basehits.sanity_drain)
    val sanity_restore = HitmarkTypeGroup(basehits.sanity_restore)
    val doom = HitmarkTypeGroup(basehits.doom)
    val burn = HitmarkTypeGroup(basehits.burn)

    val wintertodt_drain =
        HitmarkTypeGroup(lit = basehits.wintertodt_drain_lit, tint = basehits.wintertodt_drain_tint)
}
