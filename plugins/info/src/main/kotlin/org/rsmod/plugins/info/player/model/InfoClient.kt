package org.rsmod.plugins.info.player.model

public class InfoClient(playerCapacity: Int) {

    public var logout: Boolean = false
    public var viewDistance: Byte = 15
    public var ringBufIndex: Int = 0
    public val highRes: BooleanArray = BooleanArray(playerCapacity)
    public val activityFlags: ByteArray = ByteArray(playerCapacity)
    private val extendedInfoRingBufIndexes: ShortArray = ShortArray(playerCapacity)

    internal fun setExtendedInfoRingBufIndex(index: Int, ringBufIndex: Int, appearanceOnly: Boolean) {
        val specificIndex = if (appearanceOnly) {
            APPEARANCE_ONLY_START_INDEX + ringBufIndex
        } else {
            ringBufIndex
        }
        extendedInfoRingBufIndexes[index] = specificIndex.toShort()
    }

    internal fun getExtendedInfoRingBufIndex(index: Int): Int {
        val ringBufIndex = extendedInfoRingBufIndexes[index]
        if (ringBufIndex >= APPEARANCE_ONLY_START_INDEX) {
            return ringBufIndex - APPEARANCE_ONLY_START_INDEX
        }
        return ringBufIndex.toInt()
    }

    internal fun isExtendedInfoRingBufIndexAppearanceOnly(index: Int): Boolean {
        return extendedInfoRingBufIndexes[index] >= APPEARANCE_ONLY_START_INDEX
    }

    internal companion object {

        internal const val APPEARANCE_ONLY_START_INDEX: Short = 8192
    }
}
