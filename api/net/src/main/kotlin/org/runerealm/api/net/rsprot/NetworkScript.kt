package org.runerealm.api.net.rsprot

import jakarta.inject.Inject
import net.rsprot.protocol.api.NetworkService
import net.rsprot.protocol.common.RSProtConstants
import net.rsprot.protocol.common.client.OldSchoolClientType
import org.runerealm.api.core.Build
import org.runerealm.api.game.process.GameLifecycle
import org.runerealm.api.net.rsprot.player.SessionStart
import org.runerealm.api.net.rsprot.provider.SimpleXteaProvider
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.api.script.onEvent
import org.runerealm.game.MapClock
import org.runerealm.game.client.Client
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.npc.NpcStateEvents
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
class NetworkScript
@Inject
constructor(
    private val mapClock: MapClock,
    private val xtea: SimpleXteaProvider,
    private val service: NetworkService<Player>,
    private val objTypes: ObjTypeList,
    private val regionReg: RegionRegistry,
) : PluginScript() {
    override fun ScriptContext.startup() {
        check(RSProtConstants.REVISION == Build.MAJOR) {
            "RSProt and RuneRealm have mismatching revision builds! " +
                "(runerealm=${Build.MAJOR}, rsprot=${RSProtConstants.REVISION})"
        }
        onEvent<GameLifecycle.Startup> { initService() }
        onEvent<GameLifecycle.UpdateInfo> { updateService() }
        onEvent<SessionStart> { startSession() }
        onEvent<SessionStateEvent.Delete> { closeSession() }
        onEvent<NpcStateEvents.Create> { createNpcAvatar(npc) }
        onEvent<NpcStateEvents.Delete> { deleteNpcAvatar(npc) }
    }

    private fun initService() {
        service.setCommunicationThread(Thread.currentThread())
    }

    private fun updateService() {
        service.playerInfoProtocol.update()
        service.npcInfoProtocol.update()
    }

    @Suppress("UNCHECKED_CAST")
    private fun SessionStart.startSession() {
        val slot = player.slotId

        val playerInfo = service.playerInfoProtocol.alloc(slot, OldSchoolClientType.DESKTOP)
        val npcInfo = service.npcInfoProtocol.alloc(slot, OldSchoolClientType.DESKTOP)

        val client = RspClient(session, playerInfo, npcInfo) as Client<Any, Any>
        val cycle = RspCycle(session, playerInfo, npcInfo, xtea, objTypes, regionReg)

        player.client = client
        player.clientCycle = cycle

        cycle.init(player)
    }

    private fun SessionStateEvent.Delete.closeSession() {
        val client = player.client as? RspClient ?: return
        client.unregister(service, player)
    }

    private fun createNpcAvatar(npc: Npc) {
        val rspAvatar =
            service.npcAvatarFactory.alloc(
                index = npc.slotId,
                id = npc.id,
                level = npc.level,
                x = npc.x,
                z = npc.z,
                spawnCycle = mapClock.cycle,
                direction = npc.respawnDir.id,
            )
        npc.infoProtocol = RspNpcInfo(rspAvatar)
    }

    private fun deleteNpcAvatar(npc: Npc) {
        val infoProtocol = npc.avatar.infoProtocol
        if (infoProtocol is RspNpcInfo) {
            service.npcAvatarFactory.release(infoProtocol.rspAvatar)
        }
    }
}
