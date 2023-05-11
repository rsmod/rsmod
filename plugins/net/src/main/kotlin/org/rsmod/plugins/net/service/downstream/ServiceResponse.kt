package org.rsmod.plugins.net.service.downstream

import org.rsmod.game.protocol.packet.Packet

public sealed class ServiceResponse : Packet {

    public data class ExchangeSessionKey(val key: Long) : ServiceResponse()
}
