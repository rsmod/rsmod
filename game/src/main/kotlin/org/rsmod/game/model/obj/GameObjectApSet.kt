package org.rsmod.game.model.obj

class GameObjectApSet(
    private val objects: MutableSet<Int> = mutableSetOf()
) : MutableSet<Int> by objects
