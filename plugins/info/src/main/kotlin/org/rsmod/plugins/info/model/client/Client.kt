package org.rsmod.plugins.info.model.client

import org.rsmod.plugins.info.model.ExtendedInfoBlock

public class Client(
    playerCapacity: Int,
    public var viewDistance: Int = 0,
    public val isHighResolution: BooleanArray = BooleanArray(playerCapacity),
    public val pendingResolutionChange: BooleanArray = BooleanArray(playerCapacity),
    public val activityFlags: ByteArray = ByteArray(playerCapacity),
    public val extendedInfoIndexes: ShortArray = ShortArray(playerCapacity),
    public val extendedInfoClocks: IntArray = IntArray(playerCapacity)
) {

    public companion object {

        private const val INIT_STATIC_EXT_INFO_START_INDEX = 4096
        private const val INIT_DYNAMIC_EXT_INFO_START_INDEX = 8192

        internal fun extendedInfoBlockIndex(playerIndex: Int, block: ExtendedInfoBlock): Short {
            val offset = when (block) {
                ExtendedInfoBlock.InitStatic -> INIT_STATIC_EXT_INFO_START_INDEX
                ExtendedInfoBlock.InitDynamic -> INIT_DYNAMIC_EXT_INFO_START_INDEX
            }
            return (offset + playerIndex).toShort()
        }

        internal fun trimExtendedInfoIndex(extendedInfoIndex: Int): Int {
            val trim = if (isInitDynamicExtInfoIndex(extendedInfoIndex)) {
                INIT_DYNAMIC_EXT_INFO_START_INDEX
            } else if (isInitStaticExtInfoIndex(extendedInfoIndex)) {
                INIT_STATIC_EXT_INFO_START_INDEX
            } else {
                0
            }
            return extendedInfoIndex - trim
        }

        internal fun isInitDynamicExtInfoIndex(index: Int): Boolean {
            return index >= INIT_DYNAMIC_EXT_INFO_START_INDEX
        }

        internal fun isInitStaticExtInfoIndex(index: Int): Boolean {
            return index >= INIT_STATIC_EXT_INFO_START_INDEX
        }
    }
}

public fun Client.clean() {
    viewDistance = 0
    isHighResolution.fill(false)
    pendingResolutionChange.fill(false)
    activityFlags.fill(0)
    extendedInfoIndexes.fill(0)
    extendedInfoClocks.fill(0)
}
