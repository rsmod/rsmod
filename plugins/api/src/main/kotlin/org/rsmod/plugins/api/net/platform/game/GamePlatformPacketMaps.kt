package org.rsmod.plugins.api.net.platform.game

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.plugins.api.net.builder.downstream.DownstreamPacketMap
import org.rsmod.plugins.api.net.builder.upstream.UpstreamPacketMap

@Singleton
public class GamePlatformPacketMaps @Inject constructor(
    @GameDesktopDownstream public val desktopDownstream: DownstreamPacketMap,
    @GameDesktopUpstream public val desktopUpstream: UpstreamPacketMap
) {

    public fun eagerInitialize() {
        desktopDownstream.getOrCreateProtocol()
        desktopUpstream.getOrCreateProtocol()
    }
}
