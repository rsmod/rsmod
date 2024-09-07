package org.rsmod.api.net.rsprot.provider

import jakarta.inject.Inject
import net.rsprot.compression.HuffmanCodec
import net.rsprot.compression.provider.DefaultHuffmanCodecProvider
import net.rsprot.compression.provider.HuffmanCodecProvider
import org.openrs2.cache.Cache
import org.rsmod.annotations.Js5Cache
import org.rsmod.api.cache.Js5Archives

class HuffmanProvider @Inject constructor(@Js5Cache private val cache: Cache) {
    fun provide(): HuffmanCodecProvider {
        val data = cache.read(Js5Archives.BINARY, "huffman", file = 0)
        val huffman = HuffmanCodec.create(data)
        return DefaultHuffmanCodecProvider(huffman)
    }
}
