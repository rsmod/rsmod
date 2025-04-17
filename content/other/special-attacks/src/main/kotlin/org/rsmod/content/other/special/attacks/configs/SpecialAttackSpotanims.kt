package org.rsmod.content.other.special.attacks.configs

import org.rsmod.api.type.refs.spot.SpotanimReferences

typealias special_spots = SpecialAttackSpotanims

object SpecialAttackSpotanims : SpotanimReferences() {
    val lumber_up_red = find("dragon_smallaxe_swoosh_spotanim", 37292951)
    val lumber_up_silver = find("crystal_smallaxe_swoosh_spotanim", 139193746)
    val fishstabber_silver = find("sp_attackglow_crystal", 8691321)
    val dragon_longsword = find("sp_attack_cleave_spotanim", 13013927)
}
