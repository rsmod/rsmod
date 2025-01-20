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
import org.rsmod.api.config.refs.baseanimsets
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.righthand
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.spot.EntitySpotanim
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos
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

    override fun prePlayerCycle(player: Player, objTypes: ObjTypeList) {
        // Temporary isInitialized check until code is thread-safe.
        if (::playerInfo.isInitialized && ::npcInfo.isInitialized) {
            player.updateMoveSpeed()
            player.updateCoords()
            player.rebuildArea()
            player.applyPublicMessage()
            player.applyFacePathingEntity()
            player.applyFaceAngle()
            player.applyAnim()
            player.applySpotanims()
            player.syncAppearance(objTypes)
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

    private fun Player.applySpotanims() {
        if (pendingSpotanims.isEmpty) {
            return
        }
        for (packed in pendingSpotanims.longIterator()) {
            val (id, delay, height, slot) = EntitySpotanim(packed)
            playerExtendedInfo.setSpotAnim(slot, id, delay, height)
        }
    }

    private fun Player.syncAppearance(objTypes: ObjTypeList) {
        if (!appearance.rebuild) {
            return
        }
        val info = playerExtendedInfo

        val colours = appearance.coloursSnapshot()
        for (i in colours.indices) {
            info.setColour(i, colours[i].toInt())
        }

        val identKit = appearance.identKitSnapshot()
        for (i in identKit.indices) {
            info.setIdentKit(i, identKit[i].toInt())
        }

        info.setName(displayName)
        info.setOverheadIcon(overheadIcon ?: -1)
        info.setSkullIcon(skullIcon ?: -1)
        info.setCombatLevel(combatLevel)
        info.setBodyType(appearance.bodyType)
        info.setPronoun(appearance.pronoun)
        info.setHidden(appearance.softHidden)

        info.setNameExtras(
            beforeName = appearance.namePrefix ?: "",
            afterName = appearance.nameSuffix ?: "",
            afterCombatLevel = appearance.combatLvlSuffix ?: "",
        )

        val bas = this.appearance.bas
        val weapon = this.righthand
        val transmog = this.transmog

        val readyAnim: Int
        val turnOnSpotAnim: Int
        val walkForwardAnim: Int
        val walkBackAnim: Int
        val walkLeftAnim: Int
        val walkRightAnim: Int
        val runningAnim: Int

        if (bas != null) {
            readyAnim = bas.readyAnim.id
            turnOnSpotAnim = bas.turnOnSpot.id
            walkForwardAnim = bas.walkForward.id
            walkBackAnim = bas.walkBack.id
            walkLeftAnim = bas.walkLeft.id
            walkRightAnim = bas.walkRight.id
            runningAnim = bas.running.id
        } else if (transmog != null) {
            readyAnim = transmog.readyAnim
            turnOnSpotAnim = transmog.turnBackAnim
            walkForwardAnim = transmog.walkAnim
            walkBackAnim = transmog.walkAnim
            walkLeftAnim = transmog.turnLeftAnim
            walkRightAnim = transmog.turnRightAnim
            runningAnim = transmog.runAnim
        } else if (weapon != null) {
            val type = objTypes[weapon]
            readyAnim = type.param(params.bas_readyanim).id
            turnOnSpotAnim = type.param(params.bas_turnonspot).id
            walkForwardAnim = type.param(params.bas_walk_f).id
            walkBackAnim = type.param(params.bas_walk_b).id
            walkLeftAnim = type.param(params.bas_walk_l).id
            walkRightAnim = type.param(params.bas_walk_r).id
            runningAnim = type.param(params.bas_running).id
        } else {
            val default = baseanimsets.human_default
            readyAnim = default.readyAnim.id
            turnOnSpotAnim = default.turnOnSpot.id
            walkForwardAnim = default.walkForward.id
            walkBackAnim = default.walkBack.id
            walkLeftAnim = default.walkLeft.id
            walkRightAnim = default.walkRight.id
            runningAnim = default.running.id
        }

        info.setTransmogrification(transmog?.id ?: -1)
        info.setBaseAnimationSet(
            readyAnim = readyAnim,
            turnAnim = turnOnSpotAnim,
            walkAnim = walkForwardAnim,
            walkAnimBack = walkBackAnim,
            walkAnimLeft = walkLeftAnim,
            walkAnimRight = walkRightAnim,
            runAnim = runningAnim,
        )

        for (wearpos in Wearpos.PLAYER_INFO_WEARPOS) {
            val obj = worn[wearpos.slot]
            if (obj == null) {
                info.setWornObj(wearpos.slot, -1, -1, -1)
                continue
            }
            val objType = objTypes[obj]
            info.setWornObj(wearpos.slot, obj.id, objType.wearpos2, objType.wearpos3)
        }
    }
}
