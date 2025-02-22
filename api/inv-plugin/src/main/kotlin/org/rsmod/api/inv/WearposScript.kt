package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.output.MiscOutput.setPlayerOp
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.righthand
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class WearposScript @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {
    override fun ScriptContext.startUp() {
        onWearposChange { player.updateWearpos(objType) }
    }

    private fun Player.updateWearpos(type: UnpackedObjType) {
        val sound = type.paramOrNull(params.equipment_sound)
        sound?.let(::soundSynth)

        val righthand = this.righthand?.let(objTypes::get)
        val playerOp5 = righthand?.paramOrNull(params.player_op5_text)
        setPlayerOp(this, slot = 5, op = playerOp5, priority = playerOp5 != null)
    }
}
