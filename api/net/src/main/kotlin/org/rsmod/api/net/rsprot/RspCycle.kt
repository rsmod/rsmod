package org.rsmod.api.net.rsprot

import net.rsprot.protocol.api.Session
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcInfo
import net.rsprot.protocol.game.outgoing.info.npcinfo.SetNpcUpdateOrigin
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerAvatarExtendedInfo
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerInfo
import net.rsprot.protocol.game.outgoing.info.util.BuildArea
import net.rsprot.protocol.game.outgoing.map.RebuildLogin
import net.rsprot.protocol.game.outgoing.map.RebuildNormal
import net.rsprot.protocol.game.outgoing.map.RebuildRegion
import net.rsprot.protocol.game.outgoing.map.util.RebuildRegionZone
import net.rsprot.protocol.game.outgoing.map.util.XteaProvider
import net.rsprot.protocol.game.outgoing.misc.client.ServerTickEnd
import net.rsprot.protocol.game.outgoing.worldentity.SetActiveWorld
import org.rsmod.api.config.refs.baseanimsets
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.righthand
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.client.ClientCycle
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.region.Region
import org.rsmod.game.region.zone.RegionZoneCopy
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.spot.EntitySpotanim
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneKey

class RspCycle(
    val session: Session<Player>,
    private val playerInfo: PlayerInfo,
    private val npcInfo: NpcInfo,
    private val xteaProvider: XteaProvider,
    private val objTypes: ObjTypeList,
    private val regions: RegionRegistry,
) : ClientCycle {
    private var knownCoords: CoordGrid = CoordGrid.ZERO

    private var knownBuildArea: CoordGrid = CoordGrid.NULL

    private var knownCachedSpeed: MoveSpeed = MoveSpeed.Stationary

    private var knownFaceEntity: Int? = -1

    private var knownRegionUid: Int? = null

    private var cachedRebuildRegion: RebuildRegion? = null

    private val playerExtendedInfo: PlayerAvatarExtendedInfo
        get() = playerInfo.avatar.extendedInfo

    private val worldId: Int
        get() = -1

    fun init(player: Player) {
        player.updateCoords()
        player.queueRebuildLogin()
    }

    private fun Player.queueRebuildLogin() {
        val rebuild = RebuildLogin(x shr 3, z shr 3, worldId, xteaProvider, playerInfo)
        session.queue(rebuild)
    }

    override fun preCycle(player: Player) {
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

    override fun postCycle(player: Player) {
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

        if (!recalcBuildArea) {
            return
        }

        // Skip log-in rebuild as RebuildLogin is already sent.
        if (knownBuildArea == CoordGrid.NULL) {
            knownBuildArea = buildArea
            return
        }

        if (regionUid == null) {
            val rebuild = RebuildNormal(x shr 3, z shr 3, worldId, xteaProvider)
            session.queue(rebuild)
            knownBuildArea = buildArea
            knownRegionUid = null
            cachedRebuildRegion = null
            return
        }

        val region = regions[coords]

        // The player's region uid should be reassigned every cycle before calling this function,
        // as such we should expect the region to always be valid at this point.
        checkNotNull(region) { "Unexpected invalid region: uid=$regionUid, coords=$coords" }

        // TODO: When implementing `net` module properly, figure out what the best way would be to
        //  "invalidate" the `cachedRebuildRegion` if the region is somehow altered. This can
        //  happen in regions such as the Gauntlet. (If we decide to keep this as a cached value
        //  as opposed to reconstructing it every time)
        if (regionUid != knownRegionUid) {
            cachedRebuildRegion = createRebuildRegion(region)
            knownRegionUid = regionUid
        }

        val rebuild = cachedRebuildRegion ?: createRebuildRegion(region)
        session.queue(rebuild)
        knownBuildArea = buildArea
        cachedRebuildRegion = rebuild
    }

    private fun Player.createRebuildRegion(region: Region): RebuildRegion {
        val regionZones = region.toZoneList()
        val rebuildZones =
            regionZones.associateWith { zone ->
                val copyZone = regions[zone]
                if (copyZone == RegionZoneCopy.NULL) {
                    return@associateWith null
                }
                val mapSquare = MapSquareKey.from(copyZone.normalZone().toCoords())
                val xtea = xteaProvider.provide(mapSquare.id)
                RebuildRegionZone(
                    copyZone.normalX,
                    copyZone.normalZ,
                    copyZone.normalLevel,
                    copyZone.rotation,
                    xtea,
                )
            }
        val zoneProvider =
            object : RebuildRegion.RebuildRegionZoneProvider {
                override fun provide(zoneX: Int, zoneZ: Int, level: Int): RebuildRegionZone? {
                    val zoneKey = ZoneKey(zoneX, zoneZ, level)
                    return rebuildZones[zoneKey]
                }
            }
        return RebuildRegion(x shr 3, z shr 3, true, zoneProvider)
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
