package org.rsmod.plugins.info.player.extended3

import io.netty.buffer.ByteBuf

public class ExtendedInfoStructure(
    public val mask: Int,
    public val order: Int,
    public val type: ExtendedInfo,
    public val writeStatic: ((playerIndex: Int) -> ByteBuf)?,
    public val writeDynamic: ((playerIndex: Int, observerIndex: Int) -> ByteBuf)?
) {

    public val isDynamic: Boolean get() = writeDynamic != null
    public val isStatic: Boolean get() = writeStatic != null
}
