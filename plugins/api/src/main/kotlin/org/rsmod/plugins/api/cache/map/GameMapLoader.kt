package org.rsmod.plugins.api.cache.map

import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.map.square.MapSquareKey
import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.map.MapDefinitionLoader.Companion.MAPS_ARCHIVE
import org.rsmod.plugins.api.cache.map.MapDefinitionLoader.Companion.readLocDefinition
import org.rsmod.plugins.api.cache.map.MapDefinitionLoader.Companion.readMapDefinition
import org.rsmod.plugins.api.cache.map.loc.MapLoc
import org.rsmod.plugins.api.cache.map.loc.MapLocDefinition
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.cache.type.obj.ObjectType
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeList
import org.rsmod.plugins.api.map.GameMap
import org.rsmod.plugins.api.map.GameObject
import org.rsmod.plugins.api.map.builder.GameMapBuilder
import org.rsmod.plugins.api.map.builder.ZoneBuilder
import org.rsmod.plugins.api.map.collision.addObject
import javax.inject.Inject

public class GameMapLoader @Inject constructor(
    @GameCache private val cache: Cache,
    private val xteas: XteaRepository,
    private val objectTypes: ObjectTypeList
) {

    /**
     * @param loadVisualLinkBelowObjects if set to true, any object below
     * a tile that has the [LINK_BELOW_BIT_FLAG] set will be added to our
     * game map in its respective zone and coordinates.
     * By default, this is false as the game does not make use of these
     * objects at any point, and they will consume memory.
     */
    public fun load(loadVisualLinkBelowObjects: Boolean = false): GameMap {
        val builder = GameMapBuilder()
        xteas.forEach { (mapSquare, key) ->
            val name = "${mapSquare.x}_${mapSquare.z}"
            val map = cache.read(MAPS_ARCHIVE, "m$name", file = 0).use { readMapDefinition(it) }
            val loc = cache.read(MAPS_ARCHIVE, "l$name", file = 0, key).use { readLocDefinition(it) }
            builder.putAll(mapSquare, map, loc, loadVisualLinkBelowObjects)
        }
        return builder.build()
    }

    private fun GameMapBuilder.putAll(
        key: MapSquareKey,
        mapDef: MapDefinition,
        locDef: MapLocDefinition,
        loadVisualLinkBelowObjects: Boolean = false
    ) {
        val layeredCoords = mapDef.overlays.keys + mapDef.underlays.keys
        // Allocate zones for all tiles with any underlays/overlays
        layeredCoords.forEach { flags.allocateIfAbsent(it.x, it.z, it.level) }

        mapDef.rules.forEach { (local, ruleByte) ->
            val rule = rule(local, ruleByte.toInt()) { local.ruleAbove(mapDef.rules) }
            if ((rule and BLOCKED_BIT_FLAG) != 0) {
                val coords = key.toCoords(local.level).translate(local.x, local.z)
                flags.allocateIfAbsent(coords.x, coords.z, coords.level)
                flags.add(coords.x, coords.z, coords.level, CollisionFlag.FLOOR)
            }
            if ((rule and REMOVE_ROOF_BIT_FLAG) != 0) {
                val coords = key.toCoords(local.level).translate(local.x, local.z)
                flags.allocateIfAbsent(coords.x, coords.z, coords.level)
                flags.add(coords.x, coords.z, coords.level, CollisionFlag.ROOF)
            }
        }

        locDef.forEach { loc ->
            val local = Coordinates(loc.localX, loc.localZ, loc.level)
            val rule = rule(local, mapDef.rules[local]?.toInt() ?: 0) { local.ruleAbove(mapDef.rules) }
            // Take into account that any tile that has this bit flag will
            // cause objects below it to "visually" go one level down.
            val visualLevel = if ((rule and LINK_BELOW_BIT_FLAG) != 0) {
                loc.level - 1
            } else {
                loc.level
            }
            if (!loadVisualLinkBelowObjects && visualLevel < 0) return@forEach
            val coords = key.toCoords(0.coerceAtLeast(visualLevel)).translate(loc.localX, loc.localZ)
            val zone = computeIfAbsent(ZoneKey.from(coords)) { ZoneBuilder() }
            val obj = GameObject(loc.objectType(), coords, ObjectEntity(loc.id, loc.shape, loc.rot))
            val slot = obj.slot() ?: error("Invalid object slot. (obj=$obj)")
            /*
             * "Link-below" associated objects do _not_ add clipping flags for
             * collision, nor do they get placed in the normal zone map.
             * We add them to a separate collection in our zone builder with
             * their original coordinates.
             */
            if (visualLevel !in 0 until Coordinates.LEVEL_COUNT) {
                val originalCoords = key.toCoords(loc.level).translate(loc.localX, loc.localZ)
                zone.addLinkBelow(originalCoords, slot.id, obj.entity)
                return@forEach
            }
            zone.add(coords, slot.id, obj.entity)
            flags.allocateIfAbsent(coords.x, coords.z, coords.level)
            flags.addObject(obj)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun rule(local: Coordinates, rule: Int, ruleAbove: () -> Int): Int {
        if (local.level >= Coordinates.LEVEL_COUNT - 1) return rule
        val aboveRule = ruleAbove()
        return if ((aboveRule and LINK_BELOW_BIT_FLAG) != 0) {
            aboveRule
        } else {
            rule
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Coordinates.ruleAbove(rules: Map<Coordinates, Byte>): Int {
        val above = Coordinates(x, z, level + 1)
        return rules[above]?.toInt() ?: 0
    }

    private fun MapLoc.objectType(): ObjectType {
        return objectTypes.getValue(id)
    }

    public companion object {

        public const val BLOCKED_BIT_FLAG: Int = 0x1
        public const val LINK_BELOW_BIT_FLAG: Int = 0x2
        public const val REMOVE_ROOF_BIT_FLAG: Int = 0x4
        public const val VISIBLE_BELOW_BIT_FLAG: Int = 0x8
        public const val FORCE_HIGH_DETAIL_BIT_FLAG: Int = 0x10
    }
}
