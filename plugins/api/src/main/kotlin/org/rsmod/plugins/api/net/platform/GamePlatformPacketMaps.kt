package org.rsmod.plugins.api.net.platform

import org.rsmod.plugins.api.net.builder.downstream.DownstreamPacketMap
import org.rsmod.plugins.api.net.builder.upstream.UpstreamPacketMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamePlatformPacketMaps @Inject constructor(
    @GameDesktopDownstream val desktopDownstream: DownstreamPacketMap,
    @GameDesktopUpstream val desktopUpstream: UpstreamPacketMap
) {

    fun eagerInitialize() {
        desktopDownstream.getOrCreateProtocol()
        desktopUpstream.getOrCreateProtocol()
    }
}
