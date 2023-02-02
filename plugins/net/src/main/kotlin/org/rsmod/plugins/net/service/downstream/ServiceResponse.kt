package org.rsmod.plugins.net.service.downstream

import org.rsmod.protocol.game.packet.Packet

public sealed class ServiceResponse : Packet {

    public data class ExchangeSessionKey(val key: Long) : ServiceResponse()
}
