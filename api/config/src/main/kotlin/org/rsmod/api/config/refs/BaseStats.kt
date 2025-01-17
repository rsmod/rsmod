@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.stat.StatReferences

typealias stats = BaseStats

object BaseStats : StatReferences() {
    val attack = find("attack")
    val defence = find("defence")
    val strength = find("strength")
    val hitpoints = find("hitpoints")
    val ranged = find("ranged")
    val prayer = find("prayer")
    val magic = find("magic")
    val cooking = find("cooking")
    val woodcutting = find("woodcutting")
    val fletching = find("fletching")
    val fishing = find("fishing")
    val firemaking = find("firemaking")
    val crafting = find("crafting")
    val smithing = find("smithing")
    val mining = find("mining")
    val herblore = find("herblore")
    val agility = find("agility")
    val thieving = find("thieving")
    val slayer = find("slayer")
    val farming = find("farming")
    val runecrafting = find("runecrafting")
    val hunter = find("hunter")
    val construction = find("construction")
}
