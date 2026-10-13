package org.runerealm.api.obj.plugin

import jakarta.inject.Inject
import org.runerealm.api.config.Constants
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.synths
import org.runerealm.api.invtx.invAdd
import org.runerealm.api.player.ironman.IRONMAN_TAKE_MESSAGE
import org.runerealm.api.player.ironman.isIronman
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.advanced.onDefaultOpObj3
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.obj.Obj
import org.runerealm.objtx.TransactionResultList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class ObjTakePlugin @Inject constructor(private val repo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onDefaultOpObj3 { triggerTake(it.obj) }
    }

    private suspend fun ProtectedAccess.triggerTake(obj: Obj) {
        if (player.isIronman && obj.nullableOwnerId != null && !obj.isOriginalOwner(player)) {
            player.mes(IRONMAN_TAKE_MESSAGE)
            return
        }
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
