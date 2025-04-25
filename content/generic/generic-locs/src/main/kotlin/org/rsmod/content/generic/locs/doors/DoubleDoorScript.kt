package org.rsmod.content.generic.locs.doors

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DoubleDoorScript @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.closed_left_door) { openLeftDoor(it.loc, it.type) }
        onOpLoc1(content.closed_right_door) { openRightDoor(it.loc, it.type) }
        onOpLoc1(content.opened_left_door) { closeLeftDoor(it.loc, it.type) }
        onOpLoc1(content.opened_right_door) { closeRightDoor(it.loc, it.type) }
    }

    private fun ProtectedAccess.openLeftDoor(left: BoundLocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        left.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedAngle = it.turnAngle(rotations = 3)
            val openedCoords = it.openCoords()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }

        val right =
            locRepo.findExact(
                coords = left.closeCoords(),
                content = content.closed_right_door,
                shape = left.shape,
            )
        right?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return
            val openedAngle = it.turnAngle(rotations = 1)
            val openedCoords = it.openCoords()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }
    }

    private fun ProtectedAccess.openRightDoor(right: BoundLocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        right.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedAngle = it.turnAngle(rotations = 1)
            val openedCoords = it.openCoords()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }

        val left =
            locRepo.findExact(
                coords = right.closeCoordsOpposite(),
                content = content.closed_left_door,
                shape = right.shape,
            )
        left?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return
            val openedAngle = it.turnAngle(rotations = 3)
            val openedCoords = it.openCoords()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }
    }

    private fun ProtectedAccess.closeLeftDoor(left: BoundLocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        left.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedAngle = it.turnAngle(rotations = 1)
            val openedCoords = it.closeCoordsOpposite()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }

        val right =
            locRepo.findExact(
                coords = left.openCoordsOpposite(),
                content = content.opened_right_door,
                shape = left.shape,
            )
        right?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return
            val openedAngle = it.turnAngle(rotations = 3)
            val openedCoords = it.closeCoords()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }
    }

    private fun ProtectedAccess.closeRightDoor(right: BoundLocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        right.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedAngle = it.turnAngle(rotations = 3)
            val openedCoords = it.closeCoords()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }

        val left =
            locRepo.findExact(
                coords = right.openCoordsOpposite(),
                content = content.opened_left_door,
                shape = right.shape,
            )
        left?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return
            val openedAngle = it.turnAngle(rotations = 1)
            val openedCoords = it.closeCoordsOpposite()
            locRepo.del(it, DoorConstants.DURATION)
            locRepo.add(openedCoords, openedLoc, DoorConstants.DURATION, openedAngle, it.shape)
        }
    }

    private fun BoundLocInfo.openCoords(): CoordGrid =
        DoorTranslations.translateOpen(coords, shape, angle)

    private fun BoundLocInfo.openCoordsOpposite(): CoordGrid =
        DoorTranslations.translateOpenOpposite(coords, shape, angle)

    private fun BoundLocInfo.closeCoords(): CoordGrid =
        DoorTranslations.translateClose(coords, shape, angle)

    private fun BoundLocInfo.closeCoordsOpposite(): CoordGrid =
        DoorTranslations.translateCloseOpposite(coords, shape, angle)

    private fun LocInfo.openCoords(): CoordGrid =
        DoorTranslations.translateOpen(coords, shape, angle)

    private fun LocInfo.openCoordsOpposite(): CoordGrid =
        DoorTranslations.translateOpenOpposite(coords, shape, angle)

    private fun LocInfo.closeCoords(): CoordGrid =
        DoorTranslations.translateClose(coords, shape, angle)

    private fun LocInfo.closeCoordsOpposite(): CoordGrid =
        DoorTranslations.translateCloseOpposite(coords, shape, angle)
}
