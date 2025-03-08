package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.hitmark.HitmarkReferences

typealias hitmarks = BaseHitmarks

object BaseHitmarks : HitmarkReferences() {
    val corruption = find("corruption")
    val ironman_blocked = find("ironman_blocked")
    val disease = find("disease")
    val venom = find("venom")
    val heal = find("heal")

    val alt_charge_lit = find("alt_charge_lit")
    val alt_charge_tint = find("alt_charge_tint")

    val zero_damage_lit = find("zero_damage_lit")
    val zero_damage_tint = find("zero_damage_tint")

    val alt_uncharge_lit = find("alt_uncharge_lit")
    val alt_uncharge_tint = find("alt_uncharge_tint")

    val regular_damage_lit = find("regular_damage_lit")
    val regular_damage_tint = find("regular_damage_tint")
    val regular_damage_max = find("regular_damage_max")

    val shield_damage_lit = find("shield_damage_lit")
    val shield_damage_tint = find("shield_damage_tint")
    val shield_damage_max = find("shield_damage_max")

    val zalcano_armour_damage_lit = find("zalcano_armour_damage_lit")
    val zalcano_armour_damage_tint = find("zalcano_armour_damage_tint")
    val zalcano_armour_damage_max = find("zalcano_armour_damage_max")

    val nightmare_totem_charge_lit = find("nightmare_totem_charge_lit")
    val nightmare_totem_charge_tint = find("nightmare_totem_charge_tint")
    val nightmare_totem_charge_max = find("nightmare_totem_charge_max")

    val nightmare_totem_uncharge_lit = find("nightmare_totem_uncharge_lit")
    val nightmare_totem_uncharge_tint = find("nightmare_totem_uncharge_tint")
    val nightmare_totem_uncharge_max = find("nightmare_totem_uncharge_max")

    val poise_damage_lit = find("poise_damage_lit")
    val poise_damage_tint = find("poise_damage_tint")
    val poise_damage_max = find("poise_damage_max")

    val prayer_drain_lit = find("prayer_drain_lit")
    val prayer_drain_tint = find("prayer_drain_tint")
    val prayer_drain_max = find("prayer_drain_max")

    val poison_damage_lit = find("poison_damage_lit")
    val poison_damage_tint = find("poison_damage_tint")

    val bleed = find("bleed")
    val sanity_drain = find("sanity_drain")
    val sanity_restore = find("sanity_restore")
    val doom = find("doom")
    val burn = find("burn")

    val wintertodt_drain_lit = find("wintertodt_drain_lit")
    val wintertodt_drain_tint = find("wintertodt_drain_tint")
}
