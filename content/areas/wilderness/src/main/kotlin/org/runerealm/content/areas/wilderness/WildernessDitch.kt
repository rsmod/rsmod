package org.runerealm.content.areas.wilderness

import org.runerealm.api.config.constants
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.game.entity.Player
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The [Wilderness Ditch](https://oldschool.runescape.wiki/w/Wilderness_Ditch): "Cross" jumps the
 * player three tiles over the ditch. Jumping into the Wilderness first shows the Wilderness warning
 * screen (warning 7 of the cws warning system) unless the player has disabled it.
 */
class WildernessDitch : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(ditch_locs.ditch) { cross(it.loc) }
    }

    private suspend fun ProtectedAccess.cross(loc: BoundLocInfo) {
        val north = player.coords.z <= loc.coords.z
        if (north && player.ditchWarning < WARNING_DISABLED && !confirmWarning()) {
            return
        }
        jump(north)
    }

    /** Shows the warning screen; returns `true` if the player chose to enter the Wilderness. */
    private suspend fun ProtectedAccess.confirmWarning(): Boolean {
        player.warningId = DITCH_WARNING_ID
        ifOpenMainModal(ditch_interfaces.warning)
        ifSetEvents(ditch_components.universe, 0..1, IfEvent.PauseButton)
        ifSetEvents(ditch_components.toggle, 0..2, IfEvent.PauseButton)
        while (true) {
            val input = pauseButton()
            if (input.isComponentType(ditch_components.toggle)) {
                // "Don't ask me this again" / "Enable warning" toggle.
                player.ditchWarning =
                    if (player.ditchWarning >= WARNING_DISABLED) WARNING_TOGGLE
                    else WARNING_DISABLED
                continue
            }
            ifClose()
            val enter = input.subcomponent == 1
            if (enter && player.ditchWarning < WARNING_TOGGLE) {
                // The "Don't ask me this again" toggle appears after the warning has been accepted
                // a few times.
                player.ditchWarning++
            }
            return enter
        }
    }

    private suspend fun ProtectedAccess.jump(north: Boolean) {
        val start = player.coords
        val end = start.translateZ(if (north) JUMP_DISTANCE else -JUMP_DISTANCE)
        val face = if (north) constants.em_face_north else constants.em_face_south
        anim(ditch_seqs.jump)
        exactMove(start, end, delay1 = 33, delay2 = 60, dir = face)
        delay(2)
        telejump(end)
    }

    private companion object {
        const val DITCH_WARNING_ID = 7
        const val WARNING_TOGGLE = 6
        const val WARNING_DISABLED = 7
        const val JUMP_DISTANCE = 3
    }
}

private var Player.warningId by intVarBit(ditch_varbits.warning_id)
private var Player.ditchWarning by intVarBit(ditch_varbits.ditch_warning)

object ditch_locs : LocReferences() {
    val ditch = find("ditch_wilderness_cover")
}

object ditch_seqs : SeqReferences() {
    val jump = find("wild_ditch_jump")
}

object ditch_interfaces : InterfaceReferences() {
    val warning = find("wilderness_warningscreen")
}

object ditch_components : ComponentReferences() {
    val universe = find("wilderness_warningscreen:universe")
    val toggle = find("wilderness_warningscreen:toggle")
}

object ditch_varbits : VarBitReferences() {
    val warning_id = find("cws_warning_id")
    val ditch_warning = find("cws_warning_7")
}
