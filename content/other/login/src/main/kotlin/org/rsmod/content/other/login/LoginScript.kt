package org.rsmod.content.other.login

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.misc.client.HideLocOps
import net.rsprot.protocol.game.outgoing.misc.client.HideNpcOps
import net.rsprot.protocol.game.outgoing.misc.client.HideObjOps
import net.rsprot.protocol.game.outgoing.misc.client.MinimapToggle
import net.rsprot.protocol.game.outgoing.misc.client.ResetAnims
import net.rsprot.protocol.game.outgoing.misc.player.ChatFilterSettings
import net.rsprot.protocol.game.outgoing.varp.VarpReset
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.inv.weight.InvWeight
import org.rsmod.api.player.output.Camera
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.MiscOutput
import org.rsmod.api.player.output.UpdateRun
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.startInvTransmit
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.api.realm.Realm
import org.rsmod.api.script.onEvent
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.player.SessionStateEvent
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LoginScript
@Inject
constructor(
    private val realm: Realm,
    private val mapClock: MapClock,
    private val objTypes: ObjTypeList,
    private val varpTypes: VarpTypeList,
    private val statTypes: StatTypeList,
    private val invisibleLevels: InvisibleLevels,
) : PluginScript() {
    private var Player.chatboxUnlocked: Boolean by boolVarBit(varbits.has_displayname_transmitter)
    private var Player.hideRoofs by boolVarBit(varbits.option_hide_rooftops)

    override fun ScriptContext.startup() {
        onEvent<SessionStateEvent.EngineLogin>(0L) { player.engineLogin() }
    }

    private fun Player.engineLogin() {
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
        val message = realm.config.loginMessage
        message?.let { mes(it, ChatType.Welcome) }

        val broadcast = realm.config.loginBroadcast
        broadcast?.let { mes(it, ChatType.Broadcast) }
    }

    private fun Player.sendVars() {
        client.write(VarpReset)
        chatboxUnlocked = displayName.isNotBlank()
        hideRoofs = true
        for ((varp, _) in vars) {
            val type = varpTypes[varp] ?: continue
            resyncVar(type)
        }
    }

    private fun Player.sendLowPriority() {
        sendInvs()
        runClientScript(2498, 1, 0, 0)
        resetCam()
        runClientScript(828, 1)
        runClientScript(5141)
        sendPlayerOps()
        runClientScript(876, mapClock.cycle, 0, displayName, "REGULAR")
        sendStats()
        sendRun()
        client.write(ResetAnims)
        client.write(MinimapToggle(0))
    }

    private fun Player.sendInvs() {
        startInvTransmit(inv)
        startInvTransmit(worn)
    }

    private fun Player.resetCam() {
        Camera.camReset(this)
    }

    private fun Player.sendStats() {
        for (stat in statTypes.values) {
            val currXp = statMap.getXP(stat)
            val currLvl = stat(stat)
            val hiddenLvl = currLvl + invisibleLevels.get(this, stat)
            UpdateStat.update(this, stat, currXp, currLvl, hiddenLvl)
        }
    }

    private fun Player.sendRun() {
        val weightInGrams = InvWeight.calculateWeightInGrams(this, objTypes)
        runWeight = weightInGrams
        UpdateRun.weight(this, kg = weightInGrams / 1000)
        UpdateRun.energy(this, runEnergy)
    }

    private fun Player.sendPlayerOps() {
        MiscOutput.setPlayerOp(this, slot = 2, op = null)
        MiscOutput.setPlayerOp(this, slot = 3, op = "Follow")
        MiscOutput.setPlayerOp(this, slot = 4, op = "Trade with")
        MiscOutput.setPlayerOp(this, slot = 5, op = null)
        MiscOutput.setPlayerOp(this, slot = 8, op = "Report")
    }
}
