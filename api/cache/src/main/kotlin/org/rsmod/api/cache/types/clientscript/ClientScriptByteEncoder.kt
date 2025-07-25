package org.rsmod.api.cache.types.clientscript

import io.netty.buffer.Unpooled
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.util.readOrNull

public object ClientScriptByteEncoder {
    public fun encodeAll(cache: Cache, definitions: Iterable<ClientScriptByteDefinition>) {
        val archive = Js5Archives.CLIENTSCRIPTS
        for (definition in definitions) {
            val oldBuf = cache.readOrNull(archive, definition.type, file = 0)
            val newBuf = Unpooled.wrappedBuffer(definition.data)
            if (newBuf != oldBuf) {
                cache.write(archive, definition.type, file = 0, newBuf)
            }
            oldBuf?.release()
        }
    }
}
