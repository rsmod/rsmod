package org.rsmod.plugins.net.service.downstream

import org.rsmod.protocol.packet.Packet

sealed class ServiceResponse : Packet {

    data class ExchangeSessionKey(val key: Long) : ServiceResponse()

    object ClientOutOfDate : ServiceResponse()
    object BadSessionId : ServiceResponse()
    object ClientProtocolOutOfDate : ServiceResponse()
}
