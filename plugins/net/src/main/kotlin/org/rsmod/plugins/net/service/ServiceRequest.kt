package org.rsmod.plugins.net.service

import org.rsmod.protocol.packet.Packet

sealed class ServiceRequest : Packet {

    data class InitJs5RemoteConnection(val build: Int) : ServiceRequest()
    object InitGameConnection : ServiceRequest()
}
