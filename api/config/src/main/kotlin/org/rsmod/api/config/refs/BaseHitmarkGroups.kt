package org.rsmod.api.config.refs

import org.rsmod.game.type.hitmark.HitmarkTypeGroup

typealias hitmark_groups = BaseHitmarkGroups

object BaseHitmarkGroups {
    val corruption = HitmarkTypeGroup(hitmarks.corruption)
    val ironman_blocked = HitmarkTypeGroup(hitmarks.ironman_blocked)
    val disease = HitmarkTypeGroup(hitmarks.disease)
    val venom = HitmarkTypeGroup(hitmarks.venom)
    val heal = HitmarkTypeGroup(hitmarks.heal)

    val alt_charge =
        HitmarkTypeGroup(lit = hitmarks.alt_charge_lit, tint = hitmarks.alt_charge_tint)

    val zero_damage =
        HitmarkTypeGroup(lit = hitmarks.zero_damage_lit, tint = hitmarks.zero_damage_tint)

    val alt_uncharge =
        HitmarkTypeGroup(lit = hitmarks.alt_uncharge_lit, tint = hitmarks.alt_uncharge_tint)

    val regular_damage =
        HitmarkTypeGroup(
            lit = hitmarks.regular_damage_lit,
            tint = hitmarks.regular_damage_tint,
            max = hitmarks.regular_damage_max,
        )

    val shield_damage =
        HitmarkTypeGroup(
            lit = hitmarks.shield_damage_lit,
            tint = hitmarks.shield_damage_tint,
            max = hitmarks.shield_damage_max,
        )

    val zalcano_armour_damage =
        HitmarkTypeGroup(
            lit = hitmarks.zalcano_armour_damage_lit,
            tint = hitmarks.zalcano_armour_damage_tint,
            max = hitmarks.zalcano_armour_damage_max,
        )

    val nightmare_totem_charge =
        HitmarkTypeGroup(
            lit = hitmarks.nightmare_totem_charge_lit,
            tint = hitmarks.nightmare_totem_charge_tint,
            max = hitmarks.nightmare_totem_charge_max,
        )

    val nightmare_totem_uncharge =
        HitmarkTypeGroup(
            lit = hitmarks.nightmare_totem_uncharge_lit,
            tint = hitmarks.nightmare_totem_uncharge_tint,
            max = hitmarks.nightmare_totem_uncharge_max,
        )

    val poise_damage =
        HitmarkTypeGroup(
            lit = hitmarks.poise_damage_lit,
            tint = hitmarks.poise_damage_tint,
            max = hitmarks.poise_damage_max,
        )

    val prayer_drain =
        HitmarkTypeGroup(
            lit = hitmarks.prayer_drain_lit,
            tint = hitmarks.prayer_drain_tint,
            max = hitmarks.prayer_drain_max,
        )

    val poison_damage =
        HitmarkTypeGroup(lit = hitmarks.poison_damage_lit, tint = hitmarks.poison_damage_tint)

    val bleed = HitmarkTypeGroup(hitmarks.bleed)
    val sanity_drain = HitmarkTypeGroup(hitmarks.sanity_drain)
    val sanity_restore = HitmarkTypeGroup(hitmarks.sanity_restore)
    val doom = HitmarkTypeGroup(hitmarks.doom)
    val burn = HitmarkTypeGroup(hitmarks.burn)

    val wintertodt_drain =
        HitmarkTypeGroup(lit = hitmarks.wintertodt_drain_lit, tint = hitmarks.wintertodt_drain_tint)
}
