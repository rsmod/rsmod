package org.rsmod.plugins.api.protocol.structure.login

import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME
import org.rsmod.game.cache.GameCache
import org.rsmod.plugins.api.protocol.packet.login.AuthCode
import org.rsmod.plugins.api.protocol.packet.login.CacheChecksum
import org.rsmod.plugins.api.protocol.packet.login.LoginPacketMap

val packets: LoginPacketMap by inject()
val cache: GameCache by inject()

packets.register {
    val code = when (readByte().toInt()) {
        2 -> {
            skipBytes(Int.SIZE_BYTES)
            -1
        }
        1, 3 -> {
            val auth = readUnsignedMedium()
            skipBytes(Byte.SIZE_BYTES)
            auth
        }
        else -> readInt()
    }
    AuthCode(code)
}

packets.register {
    val crcs = IntArray(cache.archiveCount)
    crcs[5] = readIntIME()
    crcs[6] = readIntLE()
    crcs[7] = readIntLE()
    crcs[11] = readIntME()
    crcs[8] = readIntME()
    crcs[12] = readIntME()
    crcs[3] = readIntME()
    crcs[13] = readIntLE()
    crcs[20] = readInt()
    crcs[18] = readInt()
    crcs[9] = readInt()
    crcs[14] = readInt()
    crcs[10] = readInt()
    crcs[19] = readIntLE()
    crcs[2] = readIntIME()
    crcs[17] = readInt()
    crcs[0] = readIntLE()
    crcs[16] = readIntLE()
    crcs[4] = readInt()
    crcs[15] = readIntIME()
    crcs[1] = readIntIME()
    CacheChecksum(crcs)
}
