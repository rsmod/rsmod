package org.rsmod.content.other.generic.signpost

import jakarta.inject.Inject
import org.rsmod.api.player.output.ClientScripts.camForceAngle
import org.rsmod.api.player.output.camLookAt
import org.rsmod.api.player.output.camMoveTo
import org.rsmod.api.player.output.camReset
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onApLoc1
import org.rsmod.api.script.onIfClose
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.type.util.EnumTypeMap
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SignpostScript @Inject constructor(private val enums: EnumTypeMapResolver) : PluginScript() {
    private lateinit var signposts: EnumTypeMap<CoordGrid, String>

    override fun ScriptContext.startUp() {
        signposts = enums[SignpostEnums.signpost_directions]
        onApLoc1(SignpostLocs.signpost) { apReadSignpost(it.bound) }
        onOpLoc1(SignpostLocs.signpost) { readSignpost(it.bound) }
        onIfClose(SignpostInterfaces.signpost) { player.exitSignpost() }
    }

    private fun ProtectedAccess.apReadSignpost(loc: BoundLocInfo) {
        if (!player.isWithinDistance(loc, 5)) {
            apRange(5)
            return
        }
        readSignpost(loc)
    }

    private fun ProtectedAccess.readSignpost(loc: BoundLocInfo) {
        player.camForceAngle(rate = 280, rate2 = 0)

        val camMoveTo = player.coords.translateZ(-6)
        player.camMoveTo(camMoveTo, camHeight = 1500, camRate = 2, camRate2 = 10)

        val camLookAt = player.coords.translateZ(3)
        player.camLookAt(camLookAt, camHeight = 450, camRate = 2, camRate2 = 10)

        val directions = signposts.getValue(loc.coords).split("|")
        val (west, south, north, east) = directions
        ifSetText(SignpostComponents.signpost_west, west)
        ifSetText(SignpostComponents.signpost_south, south)
        ifSetText(SignpostComponents.signpost_north, north)
        ifSetText(SignpostComponents.signpost_east, east)
        ifOpenMainModal(SignpostInterfaces.signpost)

        player.faceDirection(Direction.North)
    }

    private fun Player.exitSignpost() {
        // TODO: Investigate when/how this gets sent sometimes.
        // faceDirection(Direction.North)
        camReset()
    }
}
