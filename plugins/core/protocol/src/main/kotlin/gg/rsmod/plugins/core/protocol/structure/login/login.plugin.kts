package gg.rsmod.plugins.core.protocol.structure.login

import gg.rsmod.game.cache.GameCache
import gg.rsmod.plugins.core.protocol.packet.login.AuthCode
import gg.rsmod.plugins.core.protocol.packet.login.CacheChecksum
import gg.rsmod.plugins.core.protocol.packet.login.LoginPacketMap
import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME

val packets: LoginPacketMap by inject()
val cache: GameCache by inject()

packets.register<AuthCode> {
    val type = readByte().toInt()
    val code = when (type) {
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

packets.register<CacheChecksum> {
    val crcs = IntArray(cache.archiveCount)
    crcs[19] = readIntIME()
    crcs[14] = readIntIME()
    crcs[10] = readIntME()
    crcs[8] = readIntME()
    crcs[20] = readInt()
    crcs[18] = readIntIME()
    crcs[4] = readIntME()
    crcs[15] = readIntME()
    crcs[11] = readIntME()
    crcs[3] = readIntIME()
    crcs[13] = readIntIME()
    crcs[7] = readIntIME()
    crcs[17] = readIntME()
    crcs[1] = readInt()
    crcs[16] = readIntIME()
    crcs[12] = readInt()
    crcs[9] = readIntIME()
    crcs[0] = readIntIME()
    crcs[2] = readInt()
    crcs[6] = readInt()
    crcs[5] = readIntIME()
    CacheChecksum(crcs)
}
