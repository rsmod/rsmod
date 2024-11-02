package org.rsmod.api.net.rsprot

import jakarta.inject.Inject
import net.rsprot.protocol.api.NetworkService
import net.rsprot.protocol.common.RSProtConstants
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerAvatarExtendedInfo
import org.rsmod.api.core.Build
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.net.rsprot.event.SessionEnd
import org.rsmod.api.net.rsprot.event.SessionStart
import org.rsmod.api.net.rsprot.provider.XTEAProvider
import org.rsmod.api.npc.events.NpcEvents
import org.rsmod.api.player.events.SessionStateEvent
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
class NetworkScript
@Inject
constructor(
    private val service: NetworkService<Player>,
    private val players: PlayerList,
    private val mapClock: MapClock,
    private val xteaProvider: XTEAProvider,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        check(RSProtConstants.REVISION == Build.MAJOR) {
            "RSProt and RSMod have mismatching revision builds! " +
                "(rsmod=${Build.MAJOR}, rsprot=${RSProtConstants.REVISION})"
        }
        eventBus.subscribe<GameLifecycle.BootUp> { startService() }
        eventBus.subscribe<GameLifecycle.PlayersProcessed> { updateService() }
        eventBus.subscribe<SessionStart> { startSession(eventBus) }
        eventBus.subscribe<SessionEnd> { closeSession() }
        eventBus.subscribe<NpcEvents.Spawn> { createNpcAvatar(npc) }
        eventBus.subscribe<NpcEvents.Delete> { deleteNpcAvatar(npc) }
    }

    private fun startService() {
        service.start()
    }

    private fun updateService() {
        service.playerInfoProtocol.update()
        service.npcInfoProtocol.update()
    }

    @Suppress("UNCHECKED_CAST")
    private fun SessionStart.startSession(eventBus: EventBus) {
        val client = RspClient(session, xteaProvider)
        player.client = client as Client<Any, Any>
        client.open(service, player)
        setAppearance(client.playerInfo.avatar.extendedInfo)
        eventBus.publish(SessionStateEvent.Initialize(player))
        eventBus.publish(SessionStateEvent.LogIn(player))
    }

    private fun SessionEnd.closeSession(): Unit =
        with(player) {
            val client = client as RspClient
            client.close(service, this)
            // TODO: thread-safety for below
            players.remove(slotId)
        }

    private fun createNpcAvatar(npc: Npc) {
        npc.rspAvatar =
            service.npcAvatarFactory.alloc(
                index = npc.slotId,
                id = npc.id,
                level = npc.level,
                x = npc.x,
                z = npc.z,
                spawnCycle = mapClock.cycle,
                direction = npc.respawnDir.id,
            )
    }

    private fun deleteNpcAvatar(npc: Npc) {
        service.npcAvatarFactory.release(npc.rspAvatar)
    }

    private fun SessionStart.setAppearance(extendedInfo: PlayerAvatarExtendedInfo) {
        extendedInfo.setIdentKit(0, 9)
        extendedInfo.setIdentKit(1, 14)
        extendedInfo.setIdentKit(2, 109)
        extendedInfo.setIdentKit(3, 26)
        extendedInfo.setIdentKit(4, 33)
        extendedInfo.setIdentKit(5, 36)
        extendedInfo.setIdentKit(6, 42)
        extendedInfo.setColour(0, 0)
        extendedInfo.setColour(1, 3)
        extendedInfo.setColour(2, 2)
        extendedInfo.setColour(3, 0)
        extendedInfo.setColour(4, 0)
        extendedInfo.setBaseAnimationSet(
            readyAnim = 808,
            turnAnim = 823,
            walkAnim = 819,
            walkAnimBack = 820,
            walkAnimLeft = 821,
            walkAnimRight = 822,
            runAnim = 824,
        )
        extendedInfo.setName(player.avatar.name)
        extendedInfo.setCombatLevel(126)
        extendedInfo.setMale(true)
        extendedInfo.setOverheadIcon(-1)
        extendedInfo.setSkullIcon(-1)
    }
}
