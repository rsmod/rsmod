package org.rsmod.api.obj.plugin

import jakarta.inject.Inject
import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.advanced.onDefaultOpObj3
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.objtx.TransactionResultList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class ObjTakePlugin @Inject constructor(private val repo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onDefaultOpObj3 { triggerTake(it.obj) }
    }

    private suspend fun ProtectedAccess.triggerTake(obj: Obj) {
        if (!player.hasInvSpace(obj)) {
            player.mes(Constants.dm_take_invspace)
            return
        }
        player.resetAnim()
        if (player.coords != obj.coords) {
            takeFar(obj)
        } else {
            soundSynth(synths.pick2)
            player.takeClose(obj)
        }
    }

    private suspend fun ProtectedAccess.takeFar(obj: Obj) {
        delay(1)
        anim(seqs.human_pickuptable)
        soundSynth(synths.pick2)
        player.takeClose(obj)
    }

    private fun Player.takeClose(obj: Obj) {
        val removed = repo.del(obj)
        if (!removed) {
            mes(Constants.dm_take_taken)
            return
        }
        val take = transaction(obj)
        if (take.failure) {
            mes(Constants.dm_take_invspace)
        } else {
            take.commitAll()
        }
    }

    private fun Player.hasInvSpace(obj: Obj): Boolean = transaction(obj).success

    private fun Player.transaction(obj: Obj): TransactionResultList<InvObj> =
        invAdd(inv, obj.type, obj.count, autoCommit = false)
}
