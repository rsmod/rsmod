package gg.rsmod.plugins.api.update.player.task

import com.github.michaelbull.logging.InlineLogger
import com.google.common.primitives.Ints.min
import com.google.inject.Inject
import gg.rsmod.game.coroutine.IoCoroutineScope
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.ClientDevice
import gg.rsmod.game.model.client.ClientList
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.update.mask.UpdateMask
import gg.rsmod.game.update.mask.UpdateMaskPacketMap
import gg.rsmod.game.update.record.UpdateRecord
import gg.rsmod.game.update.task.UpdateTask
import gg.rsmod.plugins.api.model.map.isWithinDistance
import gg.rsmod.plugins.api.protocol.Device
import gg.rsmod.plugins.api.protocol.packet.server.PlayerInfo
import gg.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import gg.rsmod.plugins.api.protocol.update.AppearanceMask
import gg.rsmod.plugins.api.protocol.update.BitMask
import gg.rsmod.plugins.api.protocol.update.DirectionMask
import gg.rsmod.plugins.api.update.player.mask.of
import io.guthix.buffer.BitBuf
import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val logger = InlineLogger()
private const val MAX_PLAYER_ADDITIONS_PER_CYCLE = 40
private const val MAX_LOCAL_PLAYERS = 255

private sealed class UpdateGroup {
    object Active : UpdateGroup()
    object Inactive : UpdateGroup()
}

class PlayerUpdateTask @Inject constructor(
    private val clientList: ClientList,
    private val playerList: PlayerList,
    private val ioCoroutine: IoCoroutineScope,
    private val devicePackets: DevicePacketStructureMap
) : UpdateTask {

    internal fun initClient(client: Client) {
        client.addPublicRecords()
    }

    override suspend fun execute() {
        val gpi = ioCoroutine.launch(block = ::launchGpi)
        gpi.join()
    }

    private fun launchGpi(scope: CoroutineScope) = scope.launch {
        clientList.forEach { client ->
            launch {
                val buf = client.gpiBuffer()
                val gpi = PlayerInfo(buf)
                client.player.write(gpi)
                client.updateRecords.forEach(::group)
            }
        }
    }

    private fun Client.gpiBuffer(): ByteBuf {
        val buf = bufAllocator.buffer()
        val maskBuf = bufAllocator.buffer()
        val maskPackets = device.maskPackets
        buf.getPlayerInfo(player, updateRecords, maskBuf, maskPackets)
        buf.writeBytes(maskBuf)
        return buf
    }

    private fun ByteBuf.getPlayerInfo(
        player: Player,
        records: MutableList<UpdateRecord>,
        maskBuf: ByteBuf,
        maskPackets: UpdateMaskPacketMap
    ) {
        var local = 0
        var added = 0
        local += records.localPlayerInfo(toBitMode(), player, maskBuf, maskPackets, UpdateGroup.Active)
        local += records.localPlayerInfo(toBitMode(), player, maskBuf, maskPackets, UpdateGroup.Inactive)
        added += records.worldPlayerInfo(toBitMode(), player, maskBuf, maskPackets, UpdateGroup.Inactive, local, added)
        added += records.worldPlayerInfo(toBitMode(), player, maskBuf, maskPackets, UpdateGroup.Active, local, added)
    }

    private fun List<UpdateRecord>.localPlayerInfo(
        bitBuf: BitBuf,
        player: Player,
        maskBuf: ByteBuf,
        maskPackets: UpdateMaskPacketMap,
        group: UpdateGroup
    ): Int {
        var skipCount = 0
        var localPlayers = 0
        forEach { record ->
            val (index, flag, local) = record
            val inGroup = local && (group.bit and 0x1) == flag
            if (!inGroup) {
                return@forEach
            }
            if (skipCount > 0) {
                skipCount--
                record.flag = (record.flag or 0x2)
                return@forEach
            }
            localPlayers++
            val localPlayer = playerList[index]
            if (localPlayer == null || localPlayer != player && !player.canView(localPlayer)) {
                record.reset = true
                bitBuf.removeLocalPlayer(localPlayer, record)
                return@forEach
            }
            val maskUpdate = localPlayer.isMaskUpdateRequired()
            val moveUpdate = localPlayer.isMoving()
            if (maskUpdate) {
                maskBuf.writeMaskUpdate(localPlayer.entity.updates, maskPackets)
            }
            when {
                moveUpdate -> bitBuf.writeLocalMovement(localPlayer, maskUpdate)
                maskUpdate -> bitBuf.writeMaskUpdateSignal()
                else -> {
                    record.flag = (record.flag or 0x2)
                    skipCount = localSkipCount(group, index + 1)
                    bitBuf.writeSkipCount(skipCount)
                }
            }
        }
        return localPlayers
    }

    private fun List<UpdateRecord>.worldPlayerInfo(
        bitBuf: BitBuf,
        player: Player,
        maskBuf: ByteBuf,
        maskPackets: UpdateMaskPacketMap,
        group: UpdateGroup,
        localCount: Int,
        previouslyAdded: Int
    ): Int {
        var added = 0
        var skipCount = 0
        forEach { record ->
            val (index, flag, local) = record
            val inGroup = !local && (group.bit and 0x1) == flag
            if (!inGroup) {
                return@forEach
            }
            if (skipCount > 0) {
                skipCount--
                record.flag = (record.flag or 0x2)
                return@forEach
            }
            val globalPlayer = if (index >= playerList.capacity) null else playerList[index]
            if (globalPlayer != null) {
                val capacityReached = added + previouslyAdded >= MAX_PLAYER_ADDITIONS_PER_CYCLE ||
                    localCount >= MAX_LOCAL_PLAYERS
                if (player.canView(globalPlayer) && !capacityReached) {
                    bitBuf.writePlayerAddition(globalPlayer, record)
                    maskBuf.writeNewPlayerMasks(globalPlayer, maskPackets)
                    record.flag = (record.flag or 0x2)
                    record.local = true
                    record.coordinates = globalPlayer.coords.packed18Bits
                    added++
                }
                return@forEach
            }
            record.flag = (record.flag or 0x2)
            skipCount = globalSkipCount(player, group, index + 1)
            bitBuf.writeSkipCount(skipCount)
        }
        return added
    }

    private fun ByteBuf.writeNewPlayerMasks(
        other: Player,
        handlers: UpdateMaskPacketMap
    ) {
        val updates = other.entity.updates
        if (!updates.contains(AppearanceMask::class)) {
            updates.add(AppearanceMask.of(other))
        }
        if (!updates.contains(DirectionMask::class)) {
            updates.add(DirectionMask.of(other, other.faceDirection))
        }
        writeMaskUpdate(updates, handlers)
    }

    private fun ByteBuf.writeMaskUpdate(
        masks: Set<UpdateMask>,
        handlers: UpdateMaskPacketMap
    ) {
        var bitmask = 0
        masks.forEach { mask ->
            val handler = handlers.getValue(mask)
            bitmask = bitmask or handler.mask
        }
        writeMaskBit(bitmask, handlers)
        handlers.order.forEach { ordered ->
            val mask = masks.firstOrNull { it::class == ordered } ?: return@forEach
            val handler = handlers.getValue(mask)
            handler.write(mask, this)
        }
    }

    private fun BitBuf.writePlayerAddition(other: Player, record: UpdateRecord) {
        val currMultiplier = other.coords.packed18Bits
        val lastMultiplier = record.coordinates
        val multiplierChange = currMultiplier != lastMultiplier
        writeBoolean(true)
        writeBits(value = 0, amount = 2)
        writeBoolean(multiplierChange)
        if (multiplierChange) {
            writeCoordinateMultiplier(lastMultiplier, currMultiplier)
        }
        writeBits(value = other.coords.x, amount = 13)
        writeBits(value = other.coords.y, amount = 13)
        writeBoolean(true)
    }

    private fun BitBuf.writeLocalMovement(local: Player, maskUpdate: Boolean) {
        val currCoords = local.coords
        val lastCoords = local.snapshot.coords
        val diffX = currCoords.x - lastCoords.x
        val diffY = currCoords.y - lastCoords.y
        val diffPlane = currCoords.plane - lastCoords.plane
        val largeChange = abs(diffX) > 15 && abs(diffY) > 15
        writeBoolean(true)
        writeBoolean(maskUpdate)
        writeBits(value = 3, amount = 2)
        writeBoolean(largeChange)
        writeBits(value = diffPlane and 0x3, amount = 2)
        if (largeChange) {
            writeBits(value = diffX and 0x3FFF, amount = 14)
            writeBits(value = diffY and 0x3FFF, amount = 14)
        } else {
            writeBits(value = diffX and 0x1F, amount = 5)
            writeBits(value = diffY and 0x1F, amount = 5)
        }
    }

    private fun BitBuf.writeMaskUpdateSignal() {
        writeBoolean(true)
        writeBits(value = 1, amount = 1)
        writeBits(value = 0, amount = 2)
    }

    private fun BitBuf.removeLocalPlayer(local: Player?, record: UpdateRecord) {
        val newCoordinates = local?.coords?.packed18Bits ?: 0
        val coordinateChange = newCoordinates != record.coordinates
        writeBoolean(true)
        writeBoolean(false)
        writeBits(value = 0, amount = 2)
        writeBoolean(coordinateChange)
        if (coordinateChange) {
            writeCoordinateMultiplier(record.coordinates, newCoordinates)
        }
    }

    private fun BitBuf.writeSkipCount(count: Int) {
        writeBits(value = 0, amount = 1)
        when {
            count == 0 -> writeBits(value = count, amount = 2)
            count < 32 -> {
                writeBits(value = 1, amount = 2)
                writeBits(value = count, amount = 5)
            }
            count < 256 -> {
                writeBits(value = 2, amount = 2)
                writeBits(value = count, amount = 8)
            }
            else -> {
                val cap = playerList.capacity
                if (count > cap) {
                    logger.error { "Skip count out-of-range (count=$count, cap=$cap)" }
                }
                writeBits(value = 3, amount = 2)
                writeBits(value = min(cap, count), amount = 11)
            }
        }
    }

    private fun BitBuf.writeCoordinateMultiplier(oldMultiplier: Int, newMultiplier: Int) {
        val currMultiplierY = newMultiplier and 0xFF
        val currMultiplierX = (newMultiplier shr 8) and 0xFF
        val currPlane = (newMultiplier shr 16) and 0x3

        val lastMultiplierY = oldMultiplier and 0xFF
        val lastMultiplierX = (oldMultiplier shr 8) and 0xFF
        val lastPlane = (oldMultiplier shr 16) and 0x3

        val diffX = currMultiplierX - lastMultiplierX
        val diffY = currMultiplierY - lastMultiplierY
        val diffPlane = currPlane - lastPlane

        val planeChange = diffPlane != 0
        val smallChange = abs(diffX) <= 1 && abs(diffY) <= 1
        when {
            planeChange -> {
                writeBits(value = 1, amount = 2)
                writeBits(value = diffPlane, amount = 2)
            }
            smallChange -> {
                val direction = when {
                    diffX == -1 && diffY == -1 -> 0
                    diffX == 1 && diffY == -1 -> 2
                    diffX == -1 && diffY == 1 -> 5
                    diffX == 1 && diffY == 1 -> 7
                    diffY == -1 -> 1
                    diffX == -1 -> 3
                    diffX == 1 -> 4
                    else -> 6
                }
                writeBits(value = 2, amount = 2)
                writeBits(value = diffPlane, amount = 2)
                writeBits(value = direction, amount = 3)
            }
            else -> {
                writeBits(value = 3, amount = 2)
                writeBits(value = diffPlane, amount = 2)
                writeBits(value = diffX and 0xFF, amount = 8)
                writeBits(value = diffY and 0xFF, amount = 8)
            }
        }
    }

    private fun List<UpdateRecord>.localSkipCount(
        group: UpdateGroup,
        offset: Int
    ): Int {
        var count = 0
        for (i in offset until indices.last) {
            val record = this[i]
            val (index, flag, local) = record
            val inGroup = local && (group.bit and 0x1) == flag
            if (!inGroup) {
                continue
            }
            val next = playerList[index] ?: break
            if (next.isUpdateRequired()) {
                break
            }
            count++
        }
        return count
    }

    private fun List<UpdateRecord>.globalSkipCount(
        player: Player,
        group: UpdateGroup,
        offset: Int
    ): Int {
        var count = 0
        for (i in offset until indices.last) {
            val record = this[i]
            val (index, flag, local, coordsMultiplier) = record
            val inGroup = !local && (group.bit and 0x1) == flag
            if (!inGroup) {
                continue
            }
            val next = playerList[index]
            if (next != null) {
                val withinViewDistance = player.canView(next)
                val multiplierChange = next.coords.packed18Bits != coordsMultiplier
                if (withinViewDistance || multiplierChange) {
                    break
                }
            }
            count++
        }
        return count
    }

    private fun ByteBuf.writeMaskBit(
        bitmask: Int,
        handlers: UpdateMaskPacketMap
    ) {
        val mask = BitMask(bitmask)
        val handler = handlers.getValue(mask)
        handler.write(mask, this)
    }

    private fun Player.canView(other: Player): Boolean {
        return !other.appearance.invisible && other.coords.isWithinView(coords)
    }

    private fun Player.isUpdateRequired(): Boolean {
        return isMoving() || isMaskUpdateRequired()
    }

    private fun Player.isMoving(): Boolean {
        return movement.isNotEmpty() || appendTeleport
    }

    private fun Player.isMaskUpdateRequired(): Boolean {
        return entity.updates.isNotEmpty()
    }

    private fun Coordinates.isWithinView(coords: Coordinates): Boolean {
        return isWithinDistance(coords, 15)
    }

    private fun Client.addPublicRecords() {
        for (i in 1..playerList.capacity) {
            if (i == player.index) {
                addUpdateRecord(i, true, player.coords.packed18Bits)
                continue
            }
            addUpdateRecord(i)
        }
    }

    private fun Client.addUpdateRecord(
        index: Int,
        local: Boolean = false,
        coordinates: Int = 0
    ) {
        val record = UpdateRecord(
            index = index,
            local = local,
            coordinates = coordinates
        )
        updateRecords.add(record)
    }

    private fun group(record: UpdateRecord) {
        record.flag = record.flag shr 1
        if (record.reset) {
            record.flag = 0
            record.coordinates = 0
            record.local = false
            record.reset = false
        }
    }

    private val UpdateGroup.bit: Int
        get() = when (this) {
            UpdateGroup.Active -> 0
            UpdateGroup.Inactive -> 1
        }

    private val ClientDevice.maskPackets: UpdateMaskPacketMap
        get() = when (this) {
            Device.Desktop -> devicePackets.update(Device.Desktop)
            Device.Ios -> devicePackets.update(Device.Ios)
            Device.Android -> devicePackets.update(Device.Android)
            else -> error("Invalid client device (type=${this::class.simpleName})")
        }
}
