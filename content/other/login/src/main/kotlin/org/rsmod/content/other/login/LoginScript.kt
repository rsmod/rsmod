package org.rsmod.content.other.login

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.misc.client.HideLocOps
import net.rsprot.protocol.game.outgoing.misc.client.HideNpcOps
import net.rsprot.protocol.game.outgoing.misc.client.HideObjOps
import net.rsprot.protocol.game.outgoing.misc.client.MinimapToggle
import net.rsprot.protocol.game.outgoing.misc.client.ResetAnims
import net.rsprot.protocol.game.outgoing.misc.player.ChatFilterSettings
import net.rsprot.protocol.game.outgoing.misc.player.UpdateRunEnergy
import net.rsprot.protocol.game.outgoing.misc.player.UpdateRunWeight
import net.rsprot.protocol.game.outgoing.varp.VarpReset
import net.rsprot.protocol.game.outgoing.worldentity.ClearEntities
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.output.Camera
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.output.updateInvFull
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.chatboxUnlocked
import org.rsmod.api.script.onPlayerLogIn
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LoginScript
@Inject
constructor(private val mapClock: MapClock, private val statTypes: StatTypeList) : PluginScript() {
    private var Player.hideRoofs by boolVarp(varbits.hide_roofs)

    override fun ScriptContext.startUp() {
        onPlayerLogIn { player.defaultLogIn() }
    }

    private fun Player.defaultLogIn() {
        sendHighPriority()
        sendLowPriority()
    }

    private fun Player.sendHighPriority() {
        sendChatFilters()
        sendOpVisibility()
        sendWelcomeMessage()
        sendVars()
    }

    private fun Player.sendChatFilters() {
        client.write(ChatFilterSettings(0, 0))
    }

    private fun Player.sendOpVisibility() {
        client.write(HideNpcOps(false))
        client.write(HideLocOps(false))
        client.write(HideObjOps(false))
    }

    private fun Player.sendWelcomeMessage() {
        mes("Welcome to RS Mod.", ChatType.Welcome)
    }

    private fun Player.sendVars() {
        client.write(VarpReset)
        chatboxUnlocked = true
        hideRoofs = true
    }

    private fun Player.sendLowPriority() {
        client.write(ClearEntities)
        sendInvs()
        runClientScript(2498, 1, 0, 0)
        resetCam()
        runClientScript(828, 1)
        runClientScript(5141)
        runClientScript(876, mapClock.cycle, 0, displayName, "REGULAR")
        sendStats()
        sendRun()
        client.write(ResetAnims)
        client.write(MinimapToggle(0))
    }

    private fun Player.sendInvs() {
        updateInvFull(inv)
        updateInvFull(worn)
    }

    private fun Player.resetCam() {
        Camera.camReset(this)
    }

    private fun Player.sendStats() {
        for (stat in statTypes.values) {
            val currXp = statMap.getXP(stat)
            val currLvl = statMap.getCurrentLevel(stat).toInt()
            UpdateStat.update(this, stat, currXp, currLvl, currLvl)
        }
    }

    private fun Player.sendRun() {
        client.write(UpdateRunWeight(0))
        client.write(UpdateRunEnergy(10_000))
    }
}
