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
        3 -> {
            skipBytes(Int.SIZE_BYTES)
            -1
        }
        1, 2 -> {
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
    crcs[5] = readIntME()
    crcs[13] = readIntME()
    crcs[12] = readIntME()
    crcs[11] = readIntLE()
    crcs[9] = readInt()
    crcs[20] = readInt()
    crcs[10] = readIntME()
    crcs[18] = readIntLE()
    crcs[17] = readIntIME()
    crcs[15] = readIntIME()
    crcs[1] = readIntLE()
    crcs[14] = readIntME()
    crcs[19] = readIntLE()
    crcs[8] = readIntME()
    crcs[2] = readIntLE()
    crcs[3] = readIntME()
    crcs[0] = readIntLE()
    crcs[4] = readInt()
    crcs[16] = readIntLE()
    crcs[7] = readIntIME()
    crcs[6] = readIntME()
    CacheChecksum(crcs)
}
