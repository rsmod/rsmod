package org.rsmod.api.net.rsprot

import net.rsprot.protocol.api.NetworkService
import net.rsprot.protocol.api.Session
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcInfo
import net.rsprot.protocol.game.outgoing.info.playerinfo.PlayerInfo
import net.rsprot.protocol.message.OutgoingGameMessage
import org.rsmod.game.client.Client
import org.rsmod.game.entity.Player

@ExperimentalUnsignedTypes
private typealias Service = NetworkService<Player>

@OptIn(ExperimentalUnsignedTypes::class)
class RspClient(
    private val session: Session<Player>,
    private val playerInfo: PlayerInfo,
    private val npcInfo: NpcInfo,
) : Client<Service, OutgoingGameMessage> {
    override fun close() {
        session.requestClose()
    }

    override fun write(message: OutgoingGameMessage) {
        session.queue(message)
    }

    override fun read(player: Player) {
        session.processIncomingPackets(player)
    }

    override fun flush() {
        session.flush()
    }

    override fun flushLowPriority() {
        session.discardLowPriorityCategoryPackets()
        session.flush()
    }

    override fun unregister(service: Service, player: Player) {
        service.playerInfoProtocol.dealloc(playerInfo)
        service.npcInfoProtocol.dealloc(npcInfo)
    }
}
