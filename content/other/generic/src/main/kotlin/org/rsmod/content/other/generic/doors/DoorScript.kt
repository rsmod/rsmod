package org.rsmod.content.other.generic.doors

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.soundSynth
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DoorScript
@Inject
constructor(private val collision: CollisionFlagMap, private val locRepo: LocRepository) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.closed_single_door) { openDoor(it.loc, it.type) }
        onOpLoc1(content.opened_single_door) { closeDoor(it.loc, it.type) }
    }

    private suspend fun ProtectedAccess.openDoor(closed: LocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        player.soundSynth(sound)

        val openedLoc = type.param(params.next_loc_stage)
        val openedAngle = closed.openAngle()
        val openedCoords = closed.openCoords()

        val stepAway = closed.openStepAway(openedCoords)
        if (player.coords == openedCoords && stepAway != CoordGrid.NULL) {
            teleport(collision, stepAway)
            delay(2)
        }

        locRepo.del(closed, DoorConstants.DURATION)
        locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, closed.shape())
    }

    private fun LocInfo.openAngle(): LocAngle = turnAngle(rotations = 1)

    private fun LocInfo.openCoords(): CoordGrid =
        DoorTranslations.translateOpen(coords, shape(), angle())

    private fun LocInfo.openStepAway(openedCoords: CoordGrid): CoordGrid {
        if (shape() != LocShape.WallDiagonal) {
            return CoordGrid.NULL
        }
        return when (angle()) {
            LocAngle.West -> openedCoords.translateX(-1)
            LocAngle.North -> openedCoords.translateZ(1)
            LocAngle.East -> openedCoords.translateX(1)
            LocAngle.South -> openedCoords.translateZ(-1)
        }
    }

    private suspend fun ProtectedAccess.closeDoor(closed: LocInfo, type: UnpackedLocType) {
        val sound = type.param(params.closesound)
        player.soundSynth(sound)

        val closedLoc = type.param(params.next_loc_stage)
        val closedAngle = closed.closeAngle()
        val closedCoords = closed.closeCoords()

        val stepAway = closed.closeStepAway(closedCoords)
        if (player.coords == closedCoords && stepAway != CoordGrid.NULL) {
            teleport(collision, stepAway)
            delay(2)
        }

        locRepo.del(closed, DoorConstants.DURATION)
        locRepo.add(closedCoords, closedLoc, DoorConstants.DURATION, closedAngle, closed.shape())
    }

    private fun LocInfo.closeAngle(): LocAngle = turnAngle(rotations = -1)

    private fun LocInfo.closeCoords(): CoordGrid =
        DoorTranslations.translateClose(coords, shape(), angle())

    private fun LocInfo.closeStepAway(closedCoords: CoordGrid): CoordGrid {
        if (shape() != LocShape.WallDiagonal) {
            return CoordGrid.NULL
        }
        return when (angle()) {
            LocAngle.West -> closedCoords.translate(1, 1)
            LocAngle.North -> closedCoords.translate(1, -1)
            LocAngle.East -> closedCoords.translate(-1, -1)
            LocAngle.South -> closedCoords.translate(-1, 1)
        }
    }
}
