package org.rsmod.plugins.api.info.player

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.info.ExtendedInfoTypeSet
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.forEachNotNull
import org.rsmod.plugins.api.info.ReusableByteBufferMap
import org.rsmod.plugins.api.net.builder.info.ExtendedInfoEncoderMap
import org.rsmod.plugins.api.net.downstream.PlayerInfoPacket
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo
import org.rsmod.plugins.api.net.platform.info.InfoPlatformPacketEncoders
import org.rsmod.plugins.info.player.PlayerInfo
import org.rsmod.plugins.info.player.model.coord.HighResCoord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class PlayerInfoTask @Inject constructor(
    private val players: PlayerList,
    private val info: PlayerInfo,
    private val extended: InfoPlatformPacketEncoders
) {

    private var gameClock = 0

    private val logInInfo = ExtendedInfoTypeSet.of(
        ExtendedPlayerInfo.Appearance::class.java
    )

    private val cachedInfo = ExtendedInfoTypeSet.of(
        ExtendedPlayerInfo.Appearance::class.java
    )

    private val buffers = ReusableByteBufferMap(
        playerCapacity = players.capacity,
        singleBufferCapacity = 40_000
    )

    public fun initialize(player: Player): Unit = with(player) {
        info.register(index)
        player.initializeExtendedInfo()
    }

    public fun finalize(player: Player) {
        info.unregister(player.index)
    }

    public fun execute() {
        gameClock++
        players.forEachNotNull { it.prepareGpi() }
        players.forEachNotNull { it.executeGpi() }
        players.forEachNotNull { it.finalizeGpi() }
    }

    private fun Player.prepareGpi() {
        info.updateCoords(index, coords.toHighResCoords(), prevCoords.toHighResCoords())
        updateExtendedInfo()
    }

    private fun Player.executeGpi() {
        val buf = buffers[index]
        info.put(buf, index)
        downstream += PlayerInfoPacket(buf.array(), buf.limit())
    }

    private fun Player.finalizeGpi() {
        // should _not_ be handled in gpi task, but for testing purposes.
        prevCoords = coords
        extendedInfo.clear()
        // NOTE: might be worth checking if extended info has been sent
        // in previous tick. avoids info having to do array access ops.
        // any performance gains from this are more than likely negligible.
        info.updateExtendedInfo(index, EMPTY_BYTE_ARRAY)
    }

    private fun Player.updateExtendedInfo() {
        if (extendedInfo.isEmpty()) return
        val encoders = infoEncoders()
        val dataBuf = extendedInfoBuf()
        val pendingInfo = extendedInfo(extendedInfo.pendingTypes, dataBuf.clear(), encoders).array()
        info.updateExtendedInfo(index, pendingInfo)
        val updateCached = cachedInfo.filter { it in extendedInfo.pendingTypes }
        if (updateCached.isNotEmpty()) {
            val static = extendedInfo(cachedInfo, dataBuf.clear(), encoders).array()
            val dynamic = extendedInfo(updateCached, dataBuf.clear(), encoders).array()
            info.cacheStaticExtendedInfo(index, static)
            info.cacheDynamicExtendedInfo(index, gameClock, dynamic)
        }
    }

    private fun Player.initializeExtendedInfo() {
        val encoders = infoEncoders()
        val dataBuf = extendedInfoBuf()
        val logInInfo = extendedInfo(logInInfo, dataBuf.clear(), encoders).array()
        val cachedInfo = extendedInfo(cachedInfo, dataBuf.clear(), encoders).array()
        info.updateExtendedInfo(index, logInInfo)
        info.cacheStaticExtendedInfo(index, cachedInfo)
    }

    // Should _always_ return an array-backed buffer.
    private fun Player.extendedInfo(
        infoSet: ExtendedInfoTypeSet,
        dataBuf: ByteBuf,
        encoders: ExtendedInfoEncoderMap.EncoderMap<ExtendedPlayerInfo>
    ): ByteBuf {
        var bitmasks = 0
        encoders.order.forEach { ordered ->
            if (ordered !in infoSet) return@forEach
            val packet = ordered.toPacket(this) ?: error("No valid info found for extended-info type: $ordered.")
            val encoder = encoders[packet] ?: error("Encoder not found for extended-info type: $ordered.")
            bitmasks = bitmasks or encoder.bitmask
            encoder.encode(packet, dataBuf)
        }
        val headerLength = if (bitmasks >= 0xFF) Short.SIZE_BYTES else Byte.SIZE_BYTES
        val buf = Unpooled.wrappedBuffer(ByteArray(dataBuf.readableBytes() + headerLength))
        ExtendedPlayerInfo.ExtendedFlag(bitmasks).let { flags ->
            val encoder = encoders[flags] ?: error("Encoder not found for `ExtendedFlag`.")
            encoder.encode(flags, buf.clear())
        }
        dataBuf.readBytes(buf)
        return buf
    }

    private fun Player.extendedInfoBuf(): ByteBuf {
        // TODO: add reusable ByteBufMap and use it here.
        return Unpooled.buffer()
    }

    private fun Player.infoEncoders(): ExtendedInfoEncoderMap.EncoderMap<ExtendedPlayerInfo> = when {
        // TODO: select encoder map based on platform
        else -> extended.desktop.player
    }

    private fun Class<out ExtendedPlayerInfo>.toPacket(player: Player): ExtendedPlayerInfo? {
        return toStaticPacket(player) ?: toDynamicPacket(player)
    }

    private fun Class<out ExtendedPlayerInfo>.toStaticPacket(player: Player): ExtendedPlayerInfo? {
        return player.extendedInfo.pendingInfo[this] as? ExtendedPlayerInfo
    }

    private fun Class<out ExtendedPlayerInfo>.toDynamicPacket(
        player: Player
    ): ExtendedPlayerInfo? = when (this) {
        ExtendedPlayerInfo.Appearance::class.java -> player.appearance()
        else -> null
    }

    private fun Player.appearance(): ExtendedPlayerInfo.Appearance {
        return ExtendedPlayerInfo.Appearance(
            gender = 0,
            overheadSkull = null,
            overheadPrayer = null,
            transmogId = null,
            looks = looks(),
            colors = intArrayOf(0, 3, 2, 0, 0),
            bas = intArrayOf(808, 823, 819, 820, 821, 822, 824),
            displayName = displayName,
            combatLevel = 3,
            skillLevel = 0,
            invisible = false,
            unknownShortValue = 0,
            prefixes = arrayOf("", "", ""),
            unknownByteValue = 0
        )
    }

    private fun Player.looks(): ByteArray {
        var pos = 0
        val data = ByteArray(24)
        for (i in 0 until 12) {
            // TODO: equipment items
            val item: Int? = null
            @Suppress("KotlinConstantConditions")
            if (item != null) {
                val value = 0x200 or item
                data[pos++] = (value shr 8).toByte()
                data[pos++] = (value and 0xFF).toByte()
                continue
            }
            // TODO: player body part looks
            if (TRANSLATION_TABLE_BACK[i] == -1) {
                data[pos++] = 0
                continue
            }
            val value = 0x100 + DEFAULT_LOOKS[TRANSLATION_TABLE_BACK[i]]
            data[pos++] = (value shr 8).toByte()
            data[pos++] = (value and 0xFF).toByte()
        }
        return data.copyOfRange(0, pos)
    }

    private companion object {

        private val EMPTY_BYTE_ARRAY = byteArrayOf()

        private val TRANSLATION_TABLE_BACK = intArrayOf(-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1)
        private val DEFAULT_LOOKS = intArrayOf(9, 14, 109, 26, 33, 36, 42)

        private fun Coordinates.toHighResCoords(): HighResCoord {
            return HighResCoord(x, z, level)
        }
    }
}
