package gg.rsmod.plugins.core.protocol.structure.login

import gg.rsmod.game.cache.GameCache
import gg.rsmod.plugins.core.protocol.packet.login.AuthCode
import gg.rsmod.plugins.core.protocol.packet.login.CacheChecksum
import gg.rsmod.plugins.core.protocol.packet.login.LoginPacketMap
import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME

val packets: LoginPacketMap by inject()
val cache: GameCache by inject()

packets.register {
    val code = when (readByte().toInt()) {
        2 -> {
            skipBytes(Int.SIZE_BYTES)
            -1
        }
        3, 1 -> {
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
    crcs[4] = readInt()
    crcs[15] = readInt()
    crcs[3] = readInt()
    crcs[20] = readIntIME()
    crcs[10] = readInt()
    crcs[0] = readIntME()
    crcs[7] = readIntLE()
    crcs[11] = readIntME()
    crcs[14] = readIntLE()
    crcs[8] = readIntLE()
    crcs[12] = readIntIME()
    crcs[18] = readIntIME()
    crcs[13] = readIntLE()
    crcs[6] = readInt()
    crcs[19] = readInt()
    crcs[17] = readInt()
    crcs[1] = readIntME()
    crcs[16] = readIntLE()
    crcs[2] = readInt()
    crcs[9] = readIntLE()
    crcs[5] = readInt()
    CacheChecksum(crcs)
}
