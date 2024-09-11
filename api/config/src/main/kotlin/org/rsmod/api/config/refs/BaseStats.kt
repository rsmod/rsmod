package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.stat.StatReferences
import org.rsmod.game.type.stat.StatType

public typealias stats = BaseStats

public object BaseStats : StatReferences() {
    public val attack: StatType = find("attack")
    public val defence: StatType = find("defence")
    public val strength: StatType = find("strength")
    public val hitpoints: StatType = find("hitpoints")
    public val ranged: StatType = find("ranged")
    public val prayer: StatType = find("prayer")
    public val magic: StatType = find("magic")
    public val cooking: StatType = find("cooking")
    public val woodcutting: StatType = find("woodcutting")
    public val fletching: StatType = find("fletching")
    public val fishing: StatType = find("fishing")
    public val firemaking: StatType = find("firemaking")
    public val crafting: StatType = find("crafting")
    public val smithing: StatType = find("smithing")
    public val mining: StatType = find("mining")
    public val herblore: StatType = find("herblore")
    public val agility: StatType = find("agility")
    public val thieving: StatType = find("thieving")
    public val slayer: StatType = find("slayer")
    public val farming: StatType = find("farming")
    public val runecrafting: StatType = find("runecrafting")
    public val hunter: StatType = find("hunter")
    public val construction: StatType = find("construction")
}
