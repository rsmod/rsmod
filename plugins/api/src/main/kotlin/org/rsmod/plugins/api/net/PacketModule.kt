package org.rsmod.plugins.api.net

import com.google.inject.AbstractModule
import org.rsmod.plugins.api.net.builder.downstream.DownstreamPacketMap
import org.rsmod.plugins.api.net.builder.info.ExtendedInfoEncoderMap
import org.rsmod.plugins.api.net.builder.login.LoginPacketDecoderMap
import org.rsmod.plugins.api.net.builder.upstream.UpstreamPacketMap
import org.rsmod.plugins.api.net.platform.game.GameDesktopDownstream
import org.rsmod.plugins.api.net.platform.game.GameDesktopUpstream
import org.rsmod.plugins.api.net.platform.game.GamePlatformPacketMaps
import org.rsmod.plugins.api.net.platform.info.InfoDesktopEncoder
import org.rsmod.plugins.api.net.platform.login.LoginDesktopDecoder
import org.rsmod.plugins.api.net.platform.login.LoginPlatformPacketDecoders

internal object PacketModule : AbstractModule() {

    override fun configure() {
        /* bind login decoders */
        bind(LoginPacketDecoderMap::class.java)
            .annotatedWith(LoginDesktopDecoder::class.java)
            .to(LoginPacketDecoderMap::class.java)
        bind(LoginPlatformPacketDecoders::class.java)

        /* bind game packet codec */
        bind(DownstreamPacketMap::class.java)
            .annotatedWith(GameDesktopDownstream::class.java)
            .to(DownstreamPacketMap::class.java)
        bind(UpstreamPacketMap::class.java)
            .annotatedWith(GameDesktopUpstream::class.java)
            .to(UpstreamPacketMap::class.java)
        bind(GamePlatformPacketMaps::class.java)

        /* bind extended info encoders */
        bind(ExtendedInfoEncoderMap::class.java)
            .annotatedWith(InfoDesktopEncoder::class.java)
            .to(ExtendedInfoEncoderMap::class.java)
    }
}
