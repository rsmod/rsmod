package org.rsmod.game.model.stat

class StatMap(
    private val skills: MutableMap<StatKey, Stat> = mutableMapOf()
) : MutableMap<StatKey, Stat> by skills
