package org.runerealm.content.generic.locs.signpost

import jakarta.inject.Inject
import org.runerealm.api.player.output.Camera.camReset
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onApLoc1
import org.runerealm.api.script.onIfClose
import org.runerealm.api.script.onOpLoc1
import org.runerealm.game.entity.Player
import org.runerealm.game.enums.EnumTypeMap
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.map.Direction
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class SignpostScript @Inject constructor(private val enums: EnumTypeMapResolver) : PluginScript() {
    private lateinit var signposts: EnumTypeMap<CoordGrid, String>

    override fun ScriptContext.startup() {
        signposts = enums[SignpostEnums.signpost_directions]
        onApLoc1(signpost_locs.signpost) { apReadSignpost(it.loc) }
        onOpLoc1(signpost_locs.signpost) { readSignpost(it.loc) }
        onIfClose(signpost_interfaces.signpost) { player.exitSignpost() }
    }

    private fun ProtectedAccess.apReadSignpost(loc: BoundLocInfo) {
        if (isWithinApRange(loc, distance = 5)) {
            readSignpost(loc)
        }
    }

    private fun ProtectedAccess.readSignpost(loc: BoundLocInfo) {
        camForceAngle(rate = 280, rate2 = 0)

        val camMoveTo = coords.translateZ(-6)
        camMoveTo(camMoveTo, height = 1500, rate = 2, rate2 = 10)

        val camLookAt = coords.translateZ(3)
        camLookAt(camLookAt, height = 450, rate = 2, rate2 = 10)

        val directions = signposts.getValue(loc.coords).split("|")
        val (west, south, north, east) = directions
        ifSetText(signpost_components.signpost_west, west)
        ifSetText(signpost_components.signpost_south, south)
        ifSetText(signpost_components.signpost_north, north)
        ifSetText(signpost_components.signpost_east, east)
        ifOpenMainModal(signpost_interfaces.signpost)

        faceDirection(Direction.North)
    }

    private fun Player.exitSignpost() {
        // TODO: Investigate when/how this gets sent sometimes.
        // faceDirection(Direction.North)
        camReset(this)
    }
}
