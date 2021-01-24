package org.rsmod.game.collision

import com.google.inject.Inject
import org.rsmod.game.model.map.Coordinates

class SceneCollision @Inject constructor(private val collision: CollisionMap) {

    fun get(center: Coordinates, size: Int): IntArray {
        val halfSize = size / 2
        val flags = IntArray(size * size)
        val rangeX = center.x - halfSize until center.x + halfSize
        val rangeY = center.y - halfSize until center.y + halfSize
        for (y in rangeY) {
            for (x in rangeX) {
                val coords = Coordinates(x, y, center.level)
                val flag = collision[coords] ?: 0
                val localX = x - (center.x - halfSize)
                val localY = y - (center.y - halfSize)
                flags[localX, localY, size] = flag
            }
        }
        return flags
    }

    private operator fun IntArray.set(x: Int, y: Int, size: Int, value: Int) {
        val index = (y * size) + x
        this[index] = value
    }
}
