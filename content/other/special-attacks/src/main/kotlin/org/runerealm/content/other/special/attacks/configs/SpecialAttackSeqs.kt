package org.runerealm.content.other.special.attacks.configs

import org.runerealm.api.type.refs.seq.SeqReferences

typealias special_seqs = SpecialAttackSeqs

object SpecialAttackSeqs : SeqReferences() {
    val lumber_up = find("dragon_smallaxe_anim", 912284910659953726)

    val fishstabber_dragon_harpoon = find("fishstabber", 1350668019423100939)
    val fishstabber_infernal_harpoon = find("fishstabber_infernal", 1350668173984224023)
    val fishstabber_crystal_harpoon = find("fishstabber_crystal", 1350808876126604853)
    val fishstabber_infernal_harpoon_or = find("fishstabber_trailblazer", 1350875698052154560)

    val rock_knocker_dragon_pickaxe = find("rockknocker", 1137620852524073858)
    val rock_knocker_dragon_pickaxe_or_zalcano = find("rockknocker_zalcano", 1138221956779074556)
    val rock_knocker_dragon_pickaxe_or_trailblazer =
        find("rockknocker_trailblazer", 1138296294767106863)
    val rock_knocker_dragon_pickaxe_upgraded = find("rockknocker_pretty", 1137666037581574253)
    val rock_knocker_infernal_pickaxe = find("rockknocker_infernal", 1137687323942151432)
    val rock_knocker_3rd_age_pickaxe = find("rockknocker_3a", 1137546472306230121)
    val rock_knocker_crystal_pickaxe = find("rockknocker_crystal", 1138222214380946360)

    val dragon_longsword = find("cleave", 5532192131862460952)
    val dragon_dagger = find("puncture")
    val abyssal_whip = find("slayer_abyssal_whip_attack")
    val dragon_scimitar = find("sp_attack_dragon_scimitar")
    val dragon_mace = find("shatter")
    val dragon_warhammer = find("dragon_warhammer_sa_player")
    val dragon_claws = find("human_dragon_claws_spec")
    val magic_shortbow = find("snapshot")

    val armadyl_godsword = find("ags_special_player")
    val armadyl_godsword_or = find("ags_special_ornate_player")
    val bandos_godsword = find("bgs_special_player")
    val bandos_godsword_or = find("bgs_special_ornate_player")
    val saradomin_godsword = find("sgs_special_player")
    val saradomin_godsword_or = find("sgs_special_ornate_player")
    val zamorak_godsword = find("zgs_special_player")
    val zamorak_godsword_or = find("zgs_special_ornate_player")
}
