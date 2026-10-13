package org.runerealm.content.generic.locs.doors

import jakarta.inject.Inject
import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.params
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.script.onOpLoc1
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.loc.LocAngle
import org.runerealm.game.loc.LocShape
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class DoorScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.closed_single_door) { openDoor(it.loc, it.type) }
        onOpLoc1(content.opened_single_door) { closeDoor(it.loc, it.type) }
    }

    private suspend fun ProtectedAccess.openDoor(closed: BoundLocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        val openedLoc = type.param(params.next_loc_stage)
        val openedAngle = closed.openAngle()
        val openedCoords = closed.openCoords()

        val stepAway = closed.openStepAway(openedCoords)
        if (coords == openedCoords && stepAway != CoordGrid.NULL) {
            teleport(stepAway)
            delay(2)
        }

        locRepo.del(closed, DoorConstants.DURATION)
        locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, closed.shape)
    }

    private fun BoundLocInfo.openAngle(): LocAngle = turnAngle(rotations = 1)

    private fun BoundLocInfo.openCoords(): CoordGrid =
        DoorTranslations.translateOpen(coords, shape, angle)

    private fun BoundLocInfo.openStepAway(openedCoords: CoordGrid): CoordGrid {
        if (shape != LocShape.WallDiagonal) {
            return CoordGrid.NULL
        }
        return when (angle) {
            LocAngle.West -> openedCoords.translateX(-1)
            LocAngle.North -> openedCoords.translateZ(1)
            LocAngle.East -> openedCoords.translateX(1)
            LocAngle.South -> openedCoords.translateZ(-1)
        }
    }

    private suspend fun ProtectedAccess.closeDoor(closed: BoundLocInfo, type: UnpackedLocType) {
        val sound = type.param(params.closesound)
        soundSynth(sound)

        val closedLoc = type.param(params.next_loc_stage)
        val closedAngle = closed.closeAngle()
        val closedCoords = closed.closeCoords()

        val stepAway = closed.closeStepAway(closedCoords)
        if (coords == closedCoords && stepAway != CoordGrid.NULL) {
            teleport(stepAway)
            delay(2)
        }

        locRepo.del(closed, DoorConstants.DURATION)
        locRepo.add(closedCoords, closedLoc, DoorConstants.DURATION, closedAngle, closed.shape)
    }

    private fun BoundLocInfo.closeAngle(): LocAngle = turnAngle(rotations = -1)

    private fun BoundLocInfo.closeCoords(): CoordGrid =
        DoorTranslations.translateClose(coords, shape, angle)

    private fun BoundLocInfo.closeStepAway(closedCoords: CoordGrid): CoordGrid {
        if (shape != LocShape.WallDiagonal) {
            return CoordGrid.NULL
        }
        return when (angle) {
            LocAngle.West -> closedCoords.translate(1, 1)
            LocAngle.North -> closedCoords.translate(1, -1)
            LocAngle.East -> closedCoords.translate(-1, -1)
            LocAngle.South -> closedCoords.translate(-1, 1)
        }
    }
}
