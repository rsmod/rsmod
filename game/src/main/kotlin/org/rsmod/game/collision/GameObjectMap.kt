package org.rsmod.game.collision

import com.google.inject.Inject
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.obj.GameObject

private class GameObjectList(
    private val objects: MutableMap<Int, GameObject> = mutableMapOf()
) : MutableMap<Int, GameObject> by objects

class GameObjectMap private constructor(
    private val objects: MutableMap<Coordinates, GameObjectList>,
    private val dynamicSpawned: MutableMap<Coordinates, GameObjectList>,
    private val staticRemoved: MutableMap<Coordinates, GameObjectList>
) {

    @Inject
    constructor() : this(mutableMapOf(), mutableMapOf(), mutableMapOf())

    fun addStatic(obj: GameObject) {
        objects.put(obj)
    }

    fun addDynamic(obj: GameObject) {
        objects.put(obj)
        dynamicSpawned.put(obj)
    }

    fun remove(obj: GameObject) {
        val dynamicRemoved = dynamicSpawned.remove(obj.coords, obj.slot)
        val objectRemoved = objects.remove(obj.coords, obj.slot)

        /* if object is removed and it's not a dynamic object, assume it's a static object */
        if (objectRemoved != null && dynamicRemoved == null) {
            staticRemoved.put(obj)
        }
    }

    fun get(coords: Coordinates, slot: Int): GameObject? {
        return objects[coords]?.values?.firstOrNull { it.slot == slot }
    }

    operator fun get(coords: Coordinates): Collection<GameObject> {
        return objects[coords]?.values ?: emptyList()
    }

    private fun MutableMap<Coordinates, GameObjectList>.put(obj: GameObject) {
        val objects = getOrPut(obj.coords) { GameObjectList() }
        objects[obj.slot] = obj
    }

    private fun MutableMap<Coordinates, GameObjectList>.remove(coords: Coordinates, slot: Int): GameObject? {
        val objects = this[coords] ?: return null
        return objects.remove(slot)
    }
}
