package org.rsmod.api.net.rsprot

import jakarta.inject.Inject
import net.rsprot.protocol.api.NetworkService
import net.rsprot.protocol.common.RSProtConstants
import net.rsprot.protocol.common.client.OldSchoolClientType
import org.rsmod.api.core.Build
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.net.rsprot.player.SessionEnd
import org.rsmod.api.net.rsprot.player.SessionStart
import org.rsmod.api.net.rsprot.provider.SimpleXteaProvider
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.MapClock
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcStateEvents
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
    override fun ScriptContext.startUp() {
        check(RSProtConstants.REVISION == Build.MAJOR) {
            "RSProt and RSMod have mismatching revision builds! " +
                "(rsmod=${Build.MAJOR}, rsprot=${RSProtConstants.REVISION})"
        }
        eventBus.subscribe<GameLifecycle.BootUp> { startService() }
        eventBus.subscribe<GameLifecycle.UpdateInfo> { updateService() }
        eventBus.subscribe<SessionStart> { startSession() }
        eventBus.subscribe<SessionEnd> { closeSession() }
        eventBus.subscribe<NpcStateEvents.Create> { createNpcAvatar(npc) }
        eventBus.subscribe<NpcStateEvents.Delete> { deleteNpcAvatar(npc) }
    }

    private fun startService() {
        service.start()
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

        client.open(service, player)
        cycle.init(player)
    }

    private fun SessionEnd.closeSession() {
        val client = player.client as RspClient
        client.close(service, player)
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
