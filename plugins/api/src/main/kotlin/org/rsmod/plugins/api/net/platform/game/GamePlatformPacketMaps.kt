package org.rsmod.plugins.api.net.platform.game

import org.rsmod.plugins.api.net.builder.downstream.DownstreamPacketMap
import org.rsmod.plugins.api.net.builder.upstream.UpstreamPacketMap
import com.google.inject.Inject
import com.google.inject.Singleton

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
