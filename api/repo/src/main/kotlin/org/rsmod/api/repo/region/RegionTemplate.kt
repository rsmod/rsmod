package org.rsmod.api.repo.region

import org.rsmod.game.region.zone.RegionZoneCopy
import org.rsmod.game.region.zone.RegionZoneCopyMap
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

@DslMarker private annotation class RegionTemplateBuilderDsl

@RegionTemplateBuilderDsl
public class RegionStaticTemplate internal constructor(private val regionZoneLength: Int) :
    RegionTemplate() {
    internal val backing: RegionZoneCopyMap = RegionZoneCopyMap()
    internal var largeRegion: Boolean = false

    override fun build(southWest: ZoneKey): RegionZoneCopyMap = backing.translate(southWest)

    override fun isLargeRegion(): Boolean = largeRegion

    public operator fun set(
        regionZoneX: Int,
        regionZoneZ: Int,
        regionLevel: Int,
        normalZone: ZoneKey,
    ) {
        val copy = RegionZoneCopy(normalZone, rotation = 0)
        this[regionZoneX, regionZoneZ, regionLevel] = copy
    }

    public operator fun set(
        regionZoneX: Int,
        regionZoneZ: Int,
        regionLevel: Int,
        normalZoneCopy: RegionZoneCopy,
    ) {
        assertZone(regionZoneX, regionZoneZ, regionLevel)

        val regionZone = ZoneKey(regionZoneX, regionZoneZ, regionLevel)
        assertZoneNotInUse(regionZone)

        backing[regionZone] = normalZoneCopy
    }

    public fun ZoneKey.rotate90(): RegionZoneCopy = rotate(rotation = 1)

    public fun ZoneKey.rotate180(): RegionZoneCopy = rotate(rotation = 2)

    public fun ZoneKey.rotate270(): RegionZoneCopy = rotate(rotation = 3)

    private fun ZoneKey.rotate(rotation: Int): RegionZoneCopy {
        require(rotation in 0..3) { "Rotation must be within range [0..3]. (rotation=$rotation)" }
        return RegionZoneCopy(this, rotation)
    }

    /**
     * Copies a block of zones, starting from the given zone key ([copyZoneX], [copyZoneZ],
     * [copyLevel]), and extending by [BlockBuilder.zoneWidth] zones along the x-axis and
     * [BlockBuilder.zoneLength] zones along the z-axis.
     *
     * By default, the copied zones will be placed at `regionZoneX = 0` and `regionZoneZ = 0` within
     * the region, meaning they will start from the **south-west corner** of the region. These
     * values can be customized using [BlockBuilder.regionZoneX] and [BlockBuilder.regionZoneZ] to
     * control where the copied zones are positioned within the region.
     *
     * The valid ranges for `regionZoneX` and `regionZoneZ` depend on whether the template was
     * created using [RegionTemplate.create] or [RegionTemplate.createLarge]:
     * - **Small regions (`create`)**: `regionZoneX` and `regionZoneZ` can be `0`-`15`
     * - **Large regions (`createLarge`)**: `regionZoneX` and `regionZoneZ` can be `0`-`39`
     *
     * If needed, the copied zones can also be **rotated** clockwise in 90° increments using
     * [BlockBuilder.rotation] (valid values: `0`-`3`, where `0` means no rotation).
     *
     * ### Example Usage:
     * The following example copies a **2x3** block of zones, starting from `ZoneKey(226, 563, 0)`,
     * and places them in the region's south-west zone:
     * ```
     * copy(226, 563, 0) {
     *     zoneWidth = 2
     *     zoneLength = 3
     * }
     * ```
     *
     * _The above template replicates the "canoe" cutscene region._
     *
     * **Note:** If you need to copy a block of zones **across all levels** instead of just one,
     * consider using [copyAllLevels] to avoid manually iterating over levels.
     *
     * @param copyZoneX The [ZoneKey.x] coordinate of the starting zone.
     * @param copyZoneZ The [ZoneKey.z] coordinate of the starting zone.
     * @param copyLevel The [ZoneKey.level] coordinate of the starting zone.
     */
    public fun copy(copyZoneX: Int, copyZoneZ: Int, copyLevel: Int, init: BlockBuilder.() -> Unit) {
        copy(copyZoneX, copyZoneZ, copyLevel..copyLevel, init)
    }

    /**
     * Copies a block of zones **across all levels**, starting from the specified zone key
     * ([copyZoneX], [copyZoneZ]) and extending [BlockBuilder.zoneWidth] zones along the x-axis and
     * [BlockBuilder.zoneLength] zones along the z-axis for **each level**.
     *
     * This function behaves like [copy], but instead of copying a block of zones at a single level,
     * it copies them at **all levels**.
     *
     * By default, the copied zones will be placed at `regionZoneX = 0` and `regionZoneZ = 0` within
     * the region, meaning they will start from the **south-west corner** of the region. These
     * values can be customized using [BlockBuilder.regionZoneX] and [BlockBuilder.regionZoneZ] to
     * control where the copied zones are positioned within the region.
     *
     * The valid ranges for `regionZoneX` and `regionZoneZ` depend on whether the template was
     * created using [RegionTemplate.create] or [RegionTemplate.createLarge]:
     * - **Small regions (`create`)**: `regionZoneX` and `regionZoneZ` can be `0`-`15`
     * - **Large regions (`createLarge`)**: `regionZoneX` and `regionZoneZ` can be `0`-`39`
     *
     * If needed, the copied zones can also be **rotated** clockwise in 90° increments using
     * [BlockBuilder.rotation] (valid values: `0`-`3`, where `0` means no rotation).
     *
     * ### Example Usage:
     * The following example copies a **9x9** block of zones **on all levels**, starting from
     * `ZoneKey(165, 1278, 0)`, and places them in the region's south-west zone:
     * ```
     * copyAllLevels(165, 1278) {
     *     zoneWidth = 9
     *     zoneLength = 9
     * }
     * ```
     *
     * _The above template replicates the hydra boss region._
     *
     * **See also:** If you only need to copy a block of zones at a **single level**, use [copy]
     * instead.
     *
     * @param copyZoneX The [ZoneKey.x] coordinate of the starting zone.
     * @param copyZoneZ The [ZoneKey.z] coordinate of the starting zone.
     */
    public fun copyAllLevels(copyZoneX: Int, copyZoneZ: Int, init: BlockBuilder.() -> Unit) {
        copy(copyZoneX, copyZoneZ, 0..<CoordGrid.LEVEL_COUNT, init)
    }

    private fun copy(
        copyZoneX: Int,
        copyZoneZ: Int,
        levels: IntRange,
        init: BlockBuilder.() -> Unit,
    ) {
        val block = BlockBuilder().apply(init).validate()
        for (level in levels) {
            for (x in 0 until block.zoneWidth) {
                for (z in 0 until block.zoneLength) {
                    val zone = ZoneKey(copyZoneX + x, copyZoneZ + z, level)
                    val copy = zone.rotate(block.rotation)
                    this[block.regionZoneX + x, block.regionZoneZ + z, level] = copy
                }
            }
        }
    }

    @RegionTemplateBuilderDsl
    public class BlockBuilder {
        public var regionZoneX: Int = 0
        public var regionZoneZ: Int = 0
        public var zoneWidth: Int = 0
        public var zoneLength: Int = 0
        public var rotation: Int = 0

        internal fun validate(): BlockBuilder {
            check(zoneWidth > 0) {
                "`zoneWidth` must be set to amount of horizontal zones to copy."
            }
            check(zoneLength > 0) {
                "`zoneLength` must be set to amount of vertical zones to copy."
            }
            return this
        }
    }

    private fun assertZone(zoneX: Int, zoneZ: Int, level: Int) {
        require(zoneX in 0..<regionZoneLength) {
            "`zoneX` should be within range [0..${regionZoneLength - 1}]. (zoneX=$zoneX)"
        }

        require(zoneZ in 0..<regionZoneLength) {
            "`zoneZ` should be within range [0..${regionZoneLength - 1}]. (zoneZ=$zoneZ)"
        }

        require(level in 0..CoordGrid.LEVEL_BIT_MASK) {
            "`level` should be within range [0..${CoordGrid.LEVEL_BIT_MASK}]. (level=$level)"
        }
    }

    private fun assertZoneNotInUse(zone: ZoneKey) {
        check(zone !in backing) { "Zone has already been set: $zone" }
    }
}

public sealed class RegionTemplate {
    internal abstract fun build(southWest: ZoneKey): RegionZoneCopyMap

    internal abstract fun isLargeRegion(): Boolean

    public companion object {
        public fun create(init: RegionStaticTemplate.() -> Unit): RegionStaticTemplate {
            val template = RegionStaticTemplate(RegionRepository.SMALL_REGION_ZONE_LENGTH)
            return template.apply(init)
        }

        public fun createLarge(init: RegionStaticTemplate.() -> Unit): RegionStaticTemplate {
            val template = RegionStaticTemplate(RegionRepository.LARGE_REGION_ZONE_LENGTH)
            template.largeRegion = true
            return template.apply(init)
        }
    }
}
