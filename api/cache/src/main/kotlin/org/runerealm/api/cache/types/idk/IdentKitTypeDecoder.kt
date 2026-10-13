package org.runerealm.api.cache.types.idk

import io.netty.buffer.ByteBuf
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.runerealm.api.cache.Js5Archives
import org.runerealm.api.cache.Js5Configs
import org.runerealm.game.type.idk.IdentKitType
import org.runerealm.game.type.idk.IdentKitTypeList

public object IdentKitTypeDecoder {
    public fun decodeAll(cache: Cache): IdentKitTypeList {
        val types = HashMap<Int, IdentKitType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.IDENTIKIT)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.IDENTIKIT, file.id)
            types[file.id] = data.use { decode(file.id, it) }
        }
        return IdentKitTypeList(types)
    }

    public fun decode(id: Int, data: ByteBuf): IdentKitType {
        var bodyPart = -1
        var disabled = false
        while (data.isReadable) {
            when (val code = data.readUnsignedByte().toInt()) {
                0 -> break
                1 -> bodyPart = data.readUnsignedByte().toInt()
                2 -> data.skipBytes(data.readUnsignedByte().toInt() * 2)
                3 -> disabled = true
                40,
                41 -> data.skipBytes(data.readUnsignedByte().toInt() * 4)
                in 60..69 -> data.skipBytes(2)
                else -> throw IOException("Error unrecognised .idk config code: $code")
            }
        }
        return IdentKitType(id, bodyPart, disabled)
    }
}
