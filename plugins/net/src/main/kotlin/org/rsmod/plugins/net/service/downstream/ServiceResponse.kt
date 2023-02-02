package org.rsmod.plugins.net.service.downstream

import org.rsmod.protocol.game.packet.Packet

sealed class ServiceResponse : Packet {

    data class ExchangeSessionKey(val key: Long) : ServiceResponse()
}
