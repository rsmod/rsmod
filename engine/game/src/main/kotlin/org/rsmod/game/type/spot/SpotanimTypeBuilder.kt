package org.rsmod.game.type.spot

import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectShortArray

@DslMarker private annotation class SpotanimBuilderDsl

@SpotanimBuilderDsl
public class SpotanimTypeBuilder(public var internal: String? = null) {
    public var model: Int? = null
    public var anim: Int? = null
    public var ambient: Int? = null
    public var contrast: Int? = null
    public var rotation: Int? = null
    public var resizeH: Int? = null
    public var resizeV: Int? = null

    public var recolS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var recolD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)

    public fun build(id: Int): UnpackedSpotanimType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val model = checkNotNull(model) { "`model` must be set." }
        val anim = anim ?: DEFAULT_ANIM
        val ambient = ambient ?: 0
        val contrast = contrast ?: 0
        val rotation = rotation ?: 0
        val resizeH = resizeH ?: DEFAULT_RESIZE_H
        val resizeV = resizeV ?: DEFAULT_RESIZE_V
        val recolS = recolS.toShortArray()
        val recolD = recolD.toShortArray()
        val retexS = retexS.toShortArray()
        val retexD = retexD.toShortArray()
        return UnpackedSpotanimType(
            model = model,
            anim = anim,
            ambient = ambient,
            contrast = contrast,
            rotation = rotation,
            resizeH = resizeH,
            resizeV = resizeV,
            recolS = recolS,
            recolD = recolD,
            retexS = retexS,
            retexD = retexD,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_ANIM: Int = -1
        public const val DEFAULT_RESIZE_H: Int = 128
        public const val DEFAULT_RESIZE_V: Int = 128

        public const val RECOL_CAPACITY: Int = 9

        public fun merge(
            edit: UnpackedSpotanimType,
            base: UnpackedSpotanimType,
        ): UnpackedSpotanimType {
            val model = select(edit, base, default = 0) { model }
            val anim = select(edit, base, DEFAULT_ANIM) { anim }
            val ambient = select(edit, base, default = 0) { ambient }
            val contrast = select(edit, base, default = 0) { contrast }
            val rotation = select(edit, base, default = 0) { rotation }
            val resizeH = select(edit, base, DEFAULT_RESIZE_H) { resizeH }
            val resizeV = select(edit, base, DEFAULT_RESIZE_V) { resizeV }
            val recolS = selectShortArray(edit, base) { recolS }
            val recolD = selectShortArray(edit, base) { recolD }
            val retexS = selectShortArray(edit, base) { retexS }
            val retexD = selectShortArray(edit, base) { retexD }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedSpotanimType(
                model = model,
                anim = anim,
                ambient = ambient,
                contrast = contrast,
                rotation = rotation,
                resizeH = resizeH,
                resizeV = resizeV,
                recolS = recolS,
                recolD = recolD,
                retexS = retexS,
                retexD = retexD,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
