package org.runerealm.content.other.special.attacks.configs

import org.runerealm.api.type.refs.obj.ObjReferences

typealias special_objs = SpecialAttackObjs

object SpecialAttackObjs : ObjReferences() {
    val dragon_dagger = find("dragon_dagger")
    val dragon_dagger_p = find("dragon_dagger_p")
    val dragon_dagger_p_plus = find("dragon_dagger_p+")
    val dragon_dagger_p_plus_plus = find("dragon_dagger_p++")

    val abyssal_whip = find("abyssal_whip")
    val volcanic_abyssal_whip = find("abyssal_whip_lava")
    val frozen_abyssal_whip = find("abyssal_whip_ice")

    val dragon_scimitar = find("dragon_scimitar")
    val dragon_scimitar_or = find("dragon_scimitar_ornament")

    val dragon_mace = find("dragon_mace")

    val armadyl_godsword = find("ags")
    val armadyl_godsword_or = find("agsg")
    val bandos_godsword = find("bgs")
    val bandos_godsword_or = find("bgsg")
    val saradomin_godsword = find("sgs")
    val saradomin_godsword_or = find("sgsg")
    val zamorak_godsword = find("zgs")
    val zamorak_godsword_or = find("zgsg")

    val dragon_warhammer = find("dragon_warhammer")
    val dragon_warhammer_cr = find("dragon_warhammer_ornament")

    val dragon_claws = find("dragon_claws")
    val dragon_claws_cr = find("dragon_claws_ornament")

    val magic_shortbow = find("magic_shortbow")
    val magic_shortbow_i = find("magic_shortbow_i")
}
