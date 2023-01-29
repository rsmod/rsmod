package org.rsmod.plugins.api.net

import com.google.inject.AbstractModule
import org.rsmod.plugins.api.net.builder.downstream.DownstreamPacketMap
import org.rsmod.plugins.api.net.builder.login.LoginPacketDecoderMap
import org.rsmod.plugins.api.net.builder.upstream.UpstreamPacketMap
import org.rsmod.plugins.api.net.platform.GameDesktopDownstream
import org.rsmod.plugins.api.net.platform.GameDesktopUpstream
import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps
import org.rsmod.plugins.api.net.platform.LoginDesktopDecoder
import org.rsmod.plugins.api.net.platform.LoginPlatformPacketDecoders

internal object PacketModule : AbstractModule() {

    override fun configure() {
        bind(LoginPacketDecoderMap::class.java)
            .annotatedWith(LoginDesktopDecoder::class.java)
            .to(LoginPacketDecoderMap::class.java)

        bind(LoginPlatformPacketDecoders::class.java)

        bind(DownstreamPacketMap::class.java)
            .annotatedWith(GameDesktopDownstream::class.java)
            .to(DownstreamPacketMap::class.java)
        bind(UpstreamPacketMap::class.java)
            .annotatedWith(GameDesktopUpstream::class.java)
            .to(UpstreamPacketMap::class.java)

        bind(GamePlatformPacketMaps::class.java)
    }
}
