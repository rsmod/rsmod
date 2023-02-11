package org.rsmod.plugins.info.player.extended3

import io.netty.buffer.ByteBuf

@DslMarker
private annotation class DslBuilder

@DslBuilder
public class ExtendedInfoStructureBuilder(public var order: Int) {

    public var type: ExtendedInfo? = null
    public var mask: Int? = null
    private var static: ((playerIndex: Int) -> ByteBuf)? = null
    private var dynamic: ((playerIndex: Int, observerIndex: Int) -> ByteBuf)? = null

    public fun write(init: (playerIndex: Int) -> ByteBuf) {
        this.static = init
    }

    public fun write(init: (playerIndex: Int, observerIndex: Int) -> ByteBuf) {
        this.dynamic = init
    }

    public fun build(): ExtendedInfoStructure {
        val type = type ?: error("`type` must be set.")
        val mask = mask ?: error("`mask` must be set.")
        check(static != null || dynamic != null) { "`write` block must be set." }
        return ExtendedInfoStructure(
            type = type,
            mask = mask,
            order = order,
            writeStatic = static,
            writeDynamic = dynamic
        )
    }
}
