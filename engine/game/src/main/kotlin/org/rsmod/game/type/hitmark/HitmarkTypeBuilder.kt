package org.rsmod.game.type.hitmark

import org.rsmod.game.type.util.GenericPropertySelector.select

@DslMarker private annotation class HitmarkBuilderDsl

@HitmarkBuilderDsl
public class HitmarkTypeBuilder(public var internal: String? = null) {
    public var damageFont: Int? = null
    public var damageColour: Int? = null
    public var classGraphic: Int? = null
    public var leftGraphic: Int? = null
    public var middleGraphic: Int? = null
    public var rightGraphic: Int? = null
    public var scrollToOffsetX: Int? = null
    public var damageFormat: String? = null
    public var stickTime: Int? = null
    public var scrollToOffsetY: Int? = null
    public var fadeout: Int? = null
    public var replaceMode: Int? = null
    public var damageYOf: Int? = null
    public var multiVarBit: Int? = null
    public var multiVarp: Int? = null
    public var multiMark: ShortArray? = null
    public var multiMarkDefault: Int? = null

    public fun build(id: Int): UnpackedHitmarkType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val damageFont = damageFont ?: DEFAULT_DAMAGE_FONT
        val damageColour = damageColour ?: DEFAULT_DAMAGE_COLOUR
        val classGraphic = classGraphic ?: DEFAULT_CLASS_GRAPHIC
        val leftGraphic = leftGraphic ?: DEFAULT_LEFT_GRAPHIC
        val middleGraphic = middleGraphic ?: DEFAULT_MIDDLE_GRAPHIC
        val rightGraphic = rightGraphic ?: DEFAULT_RIGHT_GRAPHIC
        val scrollToOffsetX = scrollToOffsetX ?: 0
        val damageFormat = damageFormat ?: DEFAULT_DAMAGE_FORMAT
        val stickTime = stickTime ?: DEFAULT_STICK_TIME
        val scrollToOffsetY = scrollToOffsetY ?: 0
        val fadeout = fadeout ?: DEFAULT_FADEOUT
        val replaceMode = replaceMode ?: DEFAULT_REPLACE_MODE
        val damageYOf = damageYOf ?: 0
        return UnpackedHitmarkType(
            damageFont = damageFont,
            damageColour = damageColour,
            classGraphic = classGraphic,
            leftGraphic = leftGraphic,
            middleGraphic = middleGraphic,
            rightGraphic = rightGraphic,
            scrollToOffsetX = scrollToOffsetX,
            damageFormat = damageFormat,
            stickTime = stickTime,
            scrollToOffsetY = scrollToOffsetY,
            fadeout = fadeout,
            replaceMode = replaceMode,
            damageYOf = damageYOf,
            multiVarp = multiVarp,
            multiVarBit = multiVarBit,
            multiMarkDefault = multiMarkDefault,
            multiMark = multiMark,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_DAMAGE_FONT: Int = -1
        public const val DEFAULT_DAMAGE_COLOUR: Int = 16777215
        public const val DEFAULT_STICK_TIME: Int = 70
        public const val DEFAULT_CLASS_GRAPHIC: Int = -1
        public const val DEFAULT_MIDDLE_GRAPHIC: Int = -1
        public const val DEFAULT_LEFT_GRAPHIC: Int = -1
        public const val DEFAULT_RIGHT_GRAPHIC: Int = -1
        public const val DEFAULT_FADEOUT: Int = -1
        public const val DEFAULT_DAMAGE_FORMAT: String = ""
        public const val DEFAULT_REPLACE_MODE: Int = -1

        public fun merge(
            edit: UnpackedHitmarkType,
            base: UnpackedHitmarkType,
        ): UnpackedHitmarkType {
            val damageFont = select(edit, base, DEFAULT_DAMAGE_FONT) { damageFont }
            val damageColour = select(edit, base, DEFAULT_DAMAGE_COLOUR) { damageColour }
            val classGraphic = select(edit, base, DEFAULT_CLASS_GRAPHIC) { classGraphic }
            val leftGraphic = select(edit, base, DEFAULT_LEFT_GRAPHIC) { leftGraphic }
            val middleGraphic = select(edit, base, DEFAULT_MIDDLE_GRAPHIC) { middleGraphic }
            val rightGraphic = select(edit, base, DEFAULT_RIGHT_GRAPHIC) { rightGraphic }
            val scrollToOffsetX = select(edit, base, default = 0) { scrollToOffsetX }
            val damageFormat = select(edit, base, DEFAULT_DAMAGE_FORMAT) { damageFormat }
            val stickTime = select(edit, base, DEFAULT_STICK_TIME) { stickTime }
            val scrollToOffsetY = select(edit, base, default = 0) { scrollToOffsetY }
            val fadeout = select(edit, base, DEFAULT_FADEOUT) { fadeout }
            val replaceMode = select(edit, base, DEFAULT_REPLACE_MODE) { replaceMode }
            val damageYOf = select(edit, base, default = 0) { damageYOf }
            val multiVarp = select(edit, base, default = null) { multiVarp }
            val multiVarBit = select(edit, base, default = null) { multiVarBit }
            val multiMarkDefault = select(edit, base, default = null) { multiMarkDefault }
            val multiMark = select(edit, base, default = null) { multiMark }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedHitmarkType(
                damageFont = damageFont,
                damageColour = damageColour,
                classGraphic = classGraphic,
                leftGraphic = leftGraphic,
                middleGraphic = middleGraphic,
                rightGraphic = rightGraphic,
                scrollToOffsetX = scrollToOffsetX,
                damageFormat = damageFormat,
                stickTime = stickTime,
                scrollToOffsetY = scrollToOffsetY,
                fadeout = fadeout,
                replaceMode = replaceMode,
                damageYOf = damageYOf,
                multiVarp = multiVarp,
                multiVarBit = multiVarBit,
                multiMarkDefault = multiMarkDefault,
                multiMark = multiMark,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
