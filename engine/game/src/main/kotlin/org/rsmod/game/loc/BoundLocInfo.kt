package org.rsmod.game.loc

import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds
import org.rsmod.pathfinder.util.Rotations

/**
 * [BoundLocInfo] is an enhanced version of [LocInfo] that provides additional details and utilities
 * for locations in the game world. While [LocInfo] is used to gather and manage the general data of
 * a loc, [BoundLocInfo] provides a more comprehensive view, including the loc's dimensions,
 * interaction metadata, and utility functions.
 *
 * This class is optimized for scenarios where detailed information about a loc is required, such as
 * determining its exact boundaries, interaction restrictions, etc.
 */
public data class BoundLocInfo(
    public val coords: CoordGrid,
    public val entity: LocEntity,
    public val layer: Int,
    public val width: Int,
    public val length: Int,
    public val forceApproachFlags: Int,
) {
    public constructor(
        info: LocInfo,
        width: Int,
        length: Int,
        forceApproachFlags: Int,
    ) : this(info.coords, info.entity, info.layer, width, length, forceApproachFlags)

    public constructor(
        info: LocInfo,
        type: UnpackedLocType,
    ) : this(info.coords, info.entity, info.layer, type.width, type.length, type.forceApproachFlags)

    public val id: Int
        get() = entity.id

    public val shape: Int
        get() = entity.shape

    public val angle: Int
        get() = entity.angle

    public val x: Int
        get() = coords.x

    public val z: Int
        get() = coords.z

    public val level: Int
        get() = coords.level

    /**
     * The width of the loc, adjusted based on its [angle] and original dimensions ([width] and
     * [length]).
     *
     * This property calculates the effective width of the loc in the game world after applying the
     * loc's angle. The angle can alter the width and length depending on the loc's orientation. For
     * example, if the loc is rotated by 90 degrees, the width and length might be swapped.
     *
     * @return The adjusted width of the loc, taking into account its angle.
     */
    public val adjustedWidth: Int
        get() = Rotations.rotate(angle, width, length)

    /**
     * The length of the loc, adjusted based on its [angle] and original dimensions ([length] and
     * [width]).
     *
     * This property calculates the effective length of the loc in the game world after applying the
     * loc's angle. Similar to [adjustedWidth], the angle can affect the loc's dimensions,
     * potentially swapping width and length depending on the loc's orientation.
     *
     * @return The adjusted length of the loc, taking into account its angle.
     */
    public val adjustedLength: Int
        get() = Rotations.rotate(angle, length, width)

    /**
     * Calculates the bounding area of the loc in the game world, based on its current [coords],
     * [adjustedWidth], and [adjustedLength].
     *
     * The bounding area represents the rectangular space that the loc occupies in the game world.
     * This is useful for determining the area that the loc covers and its distance to other
     * bounding areas. The dimensions of the bounding area are affected by the loc's angle, which
     * may alter its width and length.
     *
     * @return A [Bounds] object representing the area occupied by the loc, including its position
     *   and dimensions.
     */
    public fun bounds(): Bounds = Bounds(coords, adjustedWidth, adjustedLength)

    public fun shape(): LocShape = LocShape[shape]

    public fun angle(): LocAngle = LocAngle[angle]

    public fun turnAngle(rotations: Int = 1): LocAngle = angle().turn(rotations)
}
