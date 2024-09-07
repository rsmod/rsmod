package org.rsmod.api.net.rsprot.provider

import io.netty.buffer.ByteBuf
import net.rsprot.protocol.api.js5.Js5GroupProvider

class Js5GroupResponseProvider(private val store: Js5Store) : Js5GroupProvider {
    override fun provide(archive: Int, group: Int): ByteBuf = store.response(archive, group)
}
