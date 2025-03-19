@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.category.CategoryReferences

typealias categories = BaseCategories

object BaseCategories : CategoryReferences() {
    val throwing_weapon = find("throwing_weapon")
    val crossbow = find("crossbow")
    val rack_bolts = find("rack_bolts")
    val arrows = find("arrows")
    val crossbow_bolt = find("crossbow_bolt")
    val bow = find("bow")
    val ogre_arrows = find("ogre_arrows")
    val chargebow = find("chargebow")
    val arrows_training = find("arrows_training")
    val kebbit_bolts = find("kebbit_bolts")
    val dragon_arrow = find("dragon_arrow")
    val atlatl_dart = find("atlatl_dart")
}
