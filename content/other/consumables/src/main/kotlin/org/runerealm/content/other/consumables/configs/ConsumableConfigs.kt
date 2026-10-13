@file:Suppress("SpellCheckingInspection")

package org.runerealm.content.other.consumables.configs

import org.runerealm.api.type.builders.varp.VarpBuilder
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.api.type.refs.varp.VarpReferences

typealias consumable_objs = ConsumableObjs

typealias consumable_varps = ConsumableVarps

typealias consumable_seqs = ConsumableSeqs

object ConsumableSeqs : SeqReferences() {
    val eat = find("human_eat")
}

object ConsumableVarps : VarpReferences() {
    /** Map clock until which regular food cannot be eaten. */
    val food_delay = find("consumable_food_delay")
    /** Map clock until which potions cannot be drunk. */
    val potion_delay = find("consumable_potion_delay")
    /** Map clock until which combo food (karambwan) cannot be eaten. */
    val combo_delay = find("consumable_combo_delay")
}

internal object ConsumableVarpBuilder : VarpBuilder() {
    init {
        build("consumable_food_delay") { temporary = true }
        build("consumable_potion_delay") { temporary = true }
        build("consumable_combo_delay") { temporary = true }
    }
}

object ConsumableObjs : ObjReferences() {
    val shrimps = find("shrimp")
    val karambwan = find("tbwt_cooked_karambwan")
    val pie_dish = find("piedish")
    val vial_empty = find("vial_empty")

    val cake = find("cake")
    val partial_cake = find("partial_cake")
    val cake_slice = find("cake_slice")
    val chocolate_cake = find("chocolate_cake")
    val partial_chocolate_cake = find("partial_chocolate_cake")
    val chocolate_slice = find("chocolate_slice")

    val plain_pizza = find("plain_pizza")
    val half_plain_pizza = find("half_plain_pizza")
    val meat_pizza = find("meat_pizza")
    val half_meat_pizza = find("half_meat_pizza")
    val anchovy_pizza = find("anchovie_pizza")
    val half_anchovy_pizza = find("half_anchovie_pizza")
    val pineapple_pizza = find("pineapple_pizza")
    val half_pineapple_pizza = find("half_pineapple_pizza")

    val apple_pie = find("apple_pie")
    val half_apple_pie = find("half_an_apple_pie")
    val redberry_pie = find("redberry_pie")
    val half_redberry_pie = find("half_a_redberry_pie")
    val meat_pie = find("meat_pie")
    val half_meat_pie = find("half_a_meat_pie")
    val garden_pie = find("garden_pie")
    val half_garden_pie = find("half_garden_pie")
    val fish_pie = find("fish_pie")
    val half_fish_pie = find("half_fish_pie")
    val admiral_pie = find("admiral_pie")
    val half_admiral_pie = find("half_admiral_pie")
    val wild_pie = find("wild_pie")
    val half_wild_pie = find("half_wild_pie")
    val summer_pie = find("summer_pie")
    val half_summer_pie = find("half_summer_pie")
    val botanical_pie = find("botanical_pie")
    val half_botanical_pie = find("half_botanical_pie")
    val mushroom_pie = find("mushroom_pie")
    val half_mushroom_pie = find("half_mushroom_pie")
    val dragonfruit_pie = find("dragonfruit_pie")
    val half_dragonfruit_pie = find("half_dragonfruit_pie")

    val attack_potion4 = find("4dose1attack")
    val attack_potion3 = find("3dose1attack")
    val attack_potion2 = find("2dose1attack")
    val attack_potion1 = find("1dose1attack")
    val strength_potion4 = find("strength4")
    val strength_potion3 = find("3dose1strength")
    val strength_potion2 = find("2dose1strength")
    val strength_potion1 = find("1dose1strength")
    val defence_potion4 = find("4dose1defense")
    val defence_potion3 = find("3dose1defense")
    val defence_potion2 = find("2dose1defense")
    val defence_potion1 = find("1dose1defense")
    val super_attack4 = find("4dose2attack")
    val super_attack3 = find("3dose2attack")
    val super_attack2 = find("2dose2attack")
    val super_attack1 = find("1dose2attack")
    val super_strength4 = find("4dose2strength")
    val super_strength3 = find("3dose2strength")
    val super_strength2 = find("2dose2strength")
    val super_strength1 = find("1dose2strength")
    val super_defence4 = find("4dose2defense")
    val super_defence3 = find("3dose2defense")
    val super_defence2 = find("2dose2defense")
    val super_defence1 = find("1dose2defense")
    val ranging_potion4 = find("4doserangerspotion")
    val ranging_potion3 = find("3doserangerspotion")
    val ranging_potion2 = find("2doserangerspotion")
    val ranging_potion1 = find("1doserangerspotion")
    val magic_potion4 = find("4dose1magic")
    val magic_potion3 = find("3dose1magic")
    val magic_potion2 = find("2dose1magic")
    val magic_potion1 = find("1dose1magic")
    val combat_potion4 = find("4dosecombat")
    val combat_potion3 = find("3dosecombat")
    val combat_potion2 = find("2dosecombat")
    val combat_potion1 = find("1dosecombat")
    val prayer_potion4 = find("4doseprayerrestore")
    val prayer_potion3 = find("3doseprayerrestore")
    val prayer_potion2 = find("2doseprayerrestore")
    val prayer_potion1 = find("1doseprayerrestore")
    val restore_potion4 = find("4dosestatrestore")
    val restore_potion3 = find("3dosestatrestore")
    val restore_potion2 = find("2dosestatrestore")
    val restore_potion1 = find("1dosestatrestore")
    val super_restore4 = find("4dose2restore")
    val super_restore3 = find("3dose2restore")
    val super_restore2 = find("2dose2restore")
    val super_restore1 = find("1dose2restore")
    val energy_potion4 = find("4dose1energy")
    val energy_potion3 = find("3dose1energy")
    val energy_potion2 = find("2dose1energy")
    val energy_potion1 = find("1dose1energy")
    val super_energy4 = find("4dose2energy")
    val super_energy3 = find("3dose2energy")
    val super_energy2 = find("2dose2energy")
    val super_energy1 = find("1dose2energy")
    val saradomin_brew4 = find("4dosepotionofsaradomin")
    val saradomin_brew3 = find("3dosepotionofsaradomin")
    val saradomin_brew2 = find("2dosepotionofsaradomin")
    val saradomin_brew1 = find("1dosepotionofsaradomin")
}
