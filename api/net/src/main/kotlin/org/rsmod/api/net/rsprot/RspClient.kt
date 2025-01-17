package org.rsmod.api.net.rsprot

import net.rsprot.protocol.api.NetworkService
import net.rsprot.protocol.api.Session
import net.rsprot.protocol.common.client.OldSchoolClientType
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcInfo
import net.rsprot.protocol.game.outgoing.info.npcinfo.SetNpcUpdateOrigin
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerAvatarExtendedInfo
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerInfo
import net.rsprot.protocol.game.outgoing.info.util.BuildArea
import net.rsprot.protocol.game.outgoing.map.RebuildLogin
import net.rsprot.protocol.game.outgoing.map.RebuildNormal
import net.rsprot.protocol.game.outgoing.map.util.XteaProvider
import net.rsprot.protocol.game.outgoing.misc.client.ServerTickEnd
import net.rsprot.protocol.game.outgoing.worldentity.SetActiveWorld
import net.rsprot.protocol.message.OutgoingGameMessage
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

@ExperimentalUnsignedTypes
private typealias Service = NetworkService<Player>

@OptIn(ExperimentalUnsignedTypes::class)
class RspClient(val session: Session<Player>, val xteaProvider: XteaProvider) :
    Client<Service, OutgoingGameMessage> {
    lateinit var playerInfo: PlayerInfo

    lateinit var npcInfo: NpcInfo

    private var knownCoords: CoordGrid = CoordGrid.ZERO

    private var knownBuildArea: CoordGrid = CoordGrid.NULL

    private var knownCachedSpeed: MoveSpeed = MoveSpeed.Stationary

    private var knownFaceEntity: Int? = -1

    private val worldId: Int
        get() = -1

    private val playerExtendedInfo: PlayerAvatarExtendedInfo
        get() = playerInfo.avatar.extendedInfo

    override fun open(service: Service, player: Player) {
        playerInfo = service.playerInfoProtocol.alloc(player.slotId, OldSchoolClientType.DESKTOP)
        npcInfo = service.npcInfoProtocol.alloc(player.slotId, OldSchoolClientType.DESKTOP)
        player.updateCoords()
        player.queueRebuildLogin()
    }

    override fun close(service: Service, player: Player) {
        service.playerInfoProtocol.dealloc(playerInfo)
        service.npcInfoProtocol.dealloc(npcInfo)
    }

    override fun write(message: OutgoingGameMessage) {
        session.queue(message)
    }

    override fun read(player: Player) {
        session.processIncomingPackets(player)
    }

    override fun flush() {
        session.flush()
    }

    override fun prePlayerCycle(player: Player) {
        // Temporary isInitialized check until code is thread-safe.
        if (::playerInfo.isInitialized && ::npcInfo.isInitialized) {
            player.updateMoveSpeed()
            player.updateCoords()
            player.rebuildArea()
            player.applyPublicMessage()
            player.applyFacePathingEntity()
            player.applyFaceAngle()
            player.applyAnim()
            player.syncAppearance()
        }
    }

    override fun postPlayerCycle(player: Player) {
        // Temporary isInitialized check until code is thread-safe.
        if (::playerInfo.isInitialized && ::npcInfo.isInitialized) {
            val origin =
                SetNpcUpdateOrigin(
                    player.coords.x - player.buildArea.x,
                    player.coords.z - player.buildArea.z,
                )
            session.queue(SetActiveWorld(SetActiveWorld.RootWorldType(player.level)))
            session.queue(playerInfo.toPacket())
            session.queue(origin)
            session.queue(npcInfo.toPacket(worldId))
            session.queue(ServerTickEnd)
        }
    }

    private fun Player.queueRebuildLogin() {
        val rebuild = RebuildLogin(x shr 3, z shr 3, worldId, xteaProvider, playerInfo)
        session.queue(rebuild)
    }

    private fun Player.updateMoveSpeed() {
        if (knownCachedSpeed != cachedMoveSpeed) {
            val extendedInfo = playerInfo.avatar.extendedInfo
            extendedInfo.setMoveSpeed(cachedMoveSpeed.steps)
            knownCachedSpeed = cachedMoveSpeed
        }
        if (moveSpeed != cachedMoveSpeed && coords != knownCoords) {
            val extendedInfo = playerInfo.avatar.extendedInfo
            extendedInfo.setTempMoveSpeed(moveSpeed.steps)
        }
    }

    private fun Player.updateCoords() {
        npcInfo.updateCoord(worldId, level, x, z)
        playerInfo.updateCoord(level, x, z)
        playerInfo.updateRenderCoord(worldId, level, x, z)
        knownCoords = coords
    }

    private fun Player.rebuildArea() {
        val recalcBuildArea = knownBuildArea != buildArea && buildArea != CoordGrid.NULL
        if (recalcBuildArea) {
            val zone = ZoneKey.from(buildArea)
            val area = BuildArea(zone.x, zone.z)
            playerInfo.updateBuildArea(worldId, area)
            npcInfo.updateBuildArea(worldId, area)
        }
        // Skip log-in rebuild as RebuildLogin is already sent.
        if (recalcBuildArea && knownBuildArea != CoordGrid.NULL) {
            val rebuild = RebuildNormal(x shr 3, z shr 3, worldId, xteaProvider)
            session.queue(rebuild)
            knownBuildArea = buildArea
        } else if (recalcBuildArea) {
            knownBuildArea = buildArea
        }
    }

    private fun Player.applyPublicMessage() {
        val message = publicMessage ?: return
        playerExtendedInfo.setChat(
            colour = message.colour,
            effects = message.effect,
            modicon = message.modIcon,
            autotyper = message.autoTyper,
            text = message.text,
            pattern = message.pattern,
        )
        publicMessage = null
    }

    private fun Player.applyFacePathingEntity() {
        val slot = faceEntitySlot
        if (knownFaceEntity != slot) {
            playerExtendedInfo.setFacePathingEntity(slot)
            knownFaceEntity = slot
        }
    }

    private fun Player.applyFaceAngle() {
        if (faceAngle != -1) {
            playerExtendedInfo.setFaceAngle(faceAngle)
        }
    }

    private fun Player.applyAnim() {
        when (pendingSequence) {
            EntitySeq.NULL -> return
            EntitySeq.ZERO -> playerExtendedInfo.setSequence(-1, 0)
            else -> playerExtendedInfo.setSequence(pendingSequence.id, pendingSequence.delay)
        }
    }

    private fun Player.syncAppearance() {}
}
