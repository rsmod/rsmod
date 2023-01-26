package org.rsmod.plugins.net.rev.platform

import org.rsmod.plugins.api.prot.builder.downstream.DownstreamPacketMap
import org.rsmod.plugins.api.prot.builder.upstream.UpstreamPacketMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamePlatformPacketMaps @Inject constructor(
    @GameDesktopDownstream val desktopDownstream: DownstreamPacketMap,
    @GameDesktopUpstream val desktopUpstream: UpstreamPacketMap
) {

    /* TODO: subscribe on server boot-up event (AFTER all scripts have been initialized) */
    fun eagerInitialize() {
        desktopDownstream.getOrCreateProtocol()
        desktopUpstream.getOrCreateProtocol()
    }
}
