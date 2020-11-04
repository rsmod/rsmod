package org.rsmod.plugins.api.model.world

import org.rsmod.game.model.obj.GameObject
import org.rsmod.game.model.world.World
import org.rsmod.plugins.api.cache.map.collision.addObject
import org.rsmod.plugins.api.cache.map.collision.removeObject

fun World.spawn(obj: GameObject) {
    collisionMap.addObject(obj)
    objectMap.addDynamic(obj)
}

fun World.remove(obj: GameObject) {
    collisionMap.removeObject(obj)
    objectMap.remove(obj)
}
