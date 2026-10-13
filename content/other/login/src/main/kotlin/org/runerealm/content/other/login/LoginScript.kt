package org.runerealm.content.other.login

import jakarta.inject.Inject
import net.rsprot.protocol.game.outgoing.misc.client.HideLocOps
import net.rsprot.protocol.game.outgoing.misc.client.HideNpcOps
import net.rsprot.protocol.game.outgoing.misc.client.HideObjOps
import net.rsprot.protocol.game.outgoing.misc.client.MinimapToggle
import net.rsprot.protocol.game.outgoing.misc.client.ResetAnims
import net.rsprot.protocol.game.outgoing.misc.player.ChatFilterSettings
import net.rsprot.protocol.game.outgoing.varp.VarpReset
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.inv.weight.InvWeight
import org.runerealm.api.player.output.Camera
import org.runerealm.api.player.output.ChatType
import org.runerealm.api.player.output.MiscOutput
import org.runerealm.api.player.output.UpdateRun
import org.runerealm.api.player.output.UpdateStat
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.output.runClientScript
import org.runerealm.api.player.startInvTransmit
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.resyncVar
import org.runerealm.api.realm.Realm
import org.runerealm.api.script.onEvent
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.game.type.varp.UnpackedVarpType
import org.runerealm.game.type.varp.VarpTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
    private val transmitVars by lazy { transmitVars() }

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
        for (varp in transmitVars) {
            // Varps with a client code (e.g. the player and npc attack option settings) must be
            // sent even when 0: the client only applies their setting when it receives the varp,
            // and `VarpReset` does not do so.
            if (varp in vars || varp.clientCode > 0) {
                resyncVar(varp)
            }
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

    private fun transmitVars(): List<UnpackedVarpType> {
        return varpTypes.filterTransmitKeys().sorted().map(varpTypes::getValue)
    }
}
