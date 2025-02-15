package org.rsmod.content.other.generic.locs.gate

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.other.generic.locs.gate.GateTranslations.leftGateClose
import org.rsmod.content.other.generic.locs.gate.GateTranslations.leftGateOpen
import org.rsmod.content.other.generic.locs.gate.GateTranslations.leftGateRightPair
import org.rsmod.content.other.generic.locs.gate.GateTranslations.rightGateClose
import org.rsmod.content.other.generic.locs.gate.GateTranslations.rightGateOpen
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PicketGate @Inject constructor(private val locRepo: LocRepository) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.closed_left_picketgate) { openLeftGate(it.loc, it.type) }
        onOpLoc1(content.closed_right_picketgate) { openRightGate(it.loc, it.type) }
        onOpLoc1(content.opened_left_picketgate) { closeLeftGate(it.loc, it.type) }
        onOpLoc1(content.opened_right_picketgate) { closeRightGate(it.loc, it.type) }
    }

    private fun ProtectedAccess.openLeftGate(left: LocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        val right =
            locRepo.findExact(
                coords = left.coords + leftGateRightPair(left.shape, left.angle),
                content = content.closed_right_picketgate,
                shape = left.shape,
            )

        left.let { locRepo.del(it, GateConstants.DURATION) }
        right?.let { locRepo.del(it, GateConstants.DURATION) }

        left.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedTranslation = leftGateOpen(it.shape, it.angle)
            val openedCoords = it.coords + openedTranslation
            val openedAngle = it.turnAngle(rotations = 3)
            locRepo.add(openedCoords, openedLoc, GateConstants.DURATION, openedAngle, it.shape)
        }
        right?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return
            val openedTranslation = rightGateOpen(it.shape, it.angle)
            val openedCoords = it.coords + openedTranslation
            val openedAngle = it.turnAngle(rotations = 3)
            locRepo.add(openedCoords, openedLoc, GateConstants.DURATION, openedAngle, it.shape)
        }
    }

    private fun ProtectedAccess.openRightGate(right: LocInfo, type: UnpackedLocType) {
        val sound = type.param(params.opensound)
        soundSynth(sound)

        val left =
            locRepo.findExact(
                coords = right.coords - leftGateRightPair(right.shape, right.angle),
                content = content.closed_left_picketgate,
                shape = right.shape,
            )

        left?.let { locRepo.del(it, GateConstants.DURATION) }
        right.let { locRepo.del(it, GateConstants.DURATION) }

        left?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return@let
            val openedTranslation = leftGateOpen(it.shape, it.angle)
            val openedCoords = it.coords + openedTranslation
            val openedAngle = it.turnAngle(rotations = 3)
            locRepo.add(openedCoords, openedLoc, GateConstants.DURATION, openedAngle, it.shape)
        }
        right.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedTranslation = rightGateOpen(it.shape, it.angle)
            val openedCoords = it.coords + openedTranslation
            val openedAngle = it.turnAngle(rotations = 3)
            locRepo.add(openedCoords, openedLoc, GateConstants.DURATION, openedAngle, it.shape)
        }
    }

    private fun ProtectedAccess.closeLeftGate(left: LocInfo, type: UnpackedLocType) {
        val sound = type.param(params.closesound)
        soundSynth(sound)

        val right =
            locRepo.findExact(
                coords = left.coords + leftGateRightPair(left.shape, left.angle),
                content = content.opened_right_picketgate,
                shape = left.shape,
            )

        left.let { locRepo.del(it, GateConstants.DURATION) }
        right?.let { locRepo.del(it, GateConstants.DURATION) }

        left.let {
            val closedLoc = type.param(params.next_loc_stage)
            val closedTranslation = leftGateClose(it.shape, it.angle)
            val closedCoords = it.coords + closedTranslation
            val closedAngle = it.turnAngle(rotations = -3)
            locRepo.add(closedCoords, closedLoc, GateConstants.DURATION, closedAngle, it.shape)
        }
        right?.let {
            val closedLoc = locParamOrNull(it, params.next_loc_stage) ?: return
            val closedTranslation = rightGateClose(it.shape, it.angle)
            val closedCoords = it.coords + closedTranslation
            val closedAngle = it.turnAngle(rotations = -3)
            locRepo.add(closedCoords, closedLoc, GateConstants.DURATION, closedAngle, it.shape)
        }
    }

    private fun ProtectedAccess.closeRightGate(right: LocInfo, type: UnpackedLocType) {
        val sound = type.param(params.closesound)
        soundSynth(sound)

        val left =
            locRepo.findExact(
                coords = right.coords - leftGateRightPair(right.shape, right.angle),
                content = content.opened_left_picketgate,
                shape = right.shape,
            )

        left?.let { locRepo.del(it, GateConstants.DURATION) }
        right.let { locRepo.del(it, GateConstants.DURATION) }

        left?.let {
            val openedLoc = locParamOrNull(it, params.next_loc_stage) ?: return@let
            val openedTranslation = leftGateClose(it.shape, it.angle)
            val openedCoords = it.coords + openedTranslation
            val openedAngle = it.turnAngle(rotations = -3)
            locRepo.add(openedCoords, openedLoc, GateConstants.DURATION, openedAngle, it.shape)
        }
        right.let {
            val openedLoc = type.param(params.next_loc_stage)
            val openedTranslation = rightGateClose(it.shape, it.angle)
            val openedCoords = it.coords + openedTranslation
            val openedAngle = it.turnAngle(rotations = -3)
            locRepo.add(openedCoords, openedLoc, GateConstants.DURATION, openedAngle, it.shape)
        }
    }
}
