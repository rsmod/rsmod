package org.rsmod.plugins.info.player.model

public class InfoClient(playerCapacity: Int) {

    public var viewDistance: Byte = 15
    public var ringBufIndex: Int = 0
    public val highRes: BooleanArray = BooleanArray(playerCapacity)
    public val activityFlags: ByteArray = ByteArray(playerCapacity)
    public val extendedInfoRingBufIndexes: ShortArray = ShortArray(playerCapacity)
}
