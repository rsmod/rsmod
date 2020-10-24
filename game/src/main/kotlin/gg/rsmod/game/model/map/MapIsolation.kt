package gg.rsmod.game.model.map

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject

private val logger = InlineLogger()

data class IsolatedMap(
    val hidden: Set<Int>
)

class MapIsolation(
    private val maps: MutableMap<Int, IsolatedMap>
) : Map<Int, IsolatedMap> by maps {

    @Inject
    constructor() : this(mutableMapOf())

    fun register(init: IsolatedMapBuilder.() -> Unit) {
        val builder = IsolatedMapBuilder().apply(init)
        val isolatedMap = builder.build()
        if (maps.containsKey(builder.mapSquare.id)) {
            logger.error {
                "Map square has already been registered and is being overwritten (mapSquare=${builder.mapSquare})"
            }
        }
        logger.debug { "Register isolated map (mapSquare=${builder.mapSquare})" }
        maps[builder.mapSquare.id] = isolatedMap
    }

    fun remove(mapSquare: MapSquare) {
        val removed = maps.remove(mapSquare.id)
        if (removed == null) {
            logger.error { "Map square could not be removed as it was not registered (mapSquare=$mapSquare)" }
        } else {
            logger.debug { "Map square removed from isolation (mapSquare=$mapSquare)" }
        }
    }
}

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class IsolatedMapBuilder {

    var mapSquare: MapSquare = MapSquare.ZERO

    private val hiddenMaps = mutableSetOf<Int>()

    fun hidden(init: HiddenMapBuilder.() -> Unit) {
        HiddenMapBuilder(hiddenMaps).apply(init)
    }

    fun build(): IsolatedMap {
        if (mapSquare == MapSquare.ZERO) {
            error("Map square has not been set.")
        } else if (hiddenMaps.isEmpty()) {
            error("Hidden map squares have not been set.")
        }
        return IsolatedMap(hiddenMaps)
    }
}

@BuilderDslMarker
class HiddenMapBuilder(private val hidden: MutableSet<Int>) {

    operator fun MapSquare.unaryMinus() {
        logger.debug { "Append hidden map (map=$this)" }
        hidden.add(id)
    }
}
