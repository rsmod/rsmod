package org.rsmod.plugins.net

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.PrivateModule
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import org.rsmod.plugins.net.js5.Js5Service
import org.rsmod.plugins.net.js5.downstream.Js5ClientOutOfDateCodec
import org.rsmod.plugins.net.js5.downstream.Js5OkCodec
import org.rsmod.plugins.net.js5.downstream.Js5RemoteDownstream
import org.rsmod.plugins.net.service.downstream.BadSessionIdCodec
import org.rsmod.plugins.net.service.downstream.ClientOutOfDateCodec
import org.rsmod.plugins.net.service.downstream.ClientProtocolOutOfDateCodec
import org.rsmod.plugins.net.service.downstream.ExchangeSessionKeyCodec
import org.rsmod.plugins.net.service.downstream.ServiceDownstream
import org.rsmod.plugins.net.service.upstream.GameLoginCodec
import org.rsmod.plugins.net.service.upstream.InitGameConnectionCodec
import org.rsmod.plugins.net.service.upstream.InitJs5RemoteConnectionCodec
import org.rsmod.plugins.net.service.upstream.ServiceUpstream
import org.rsmod.protocol.Protocol
import org.rsmod.protocol.packet.PacketCodec

object NetworkModule : AbstractModule() {

    private val PACKET_CODEC_TYPE_LITERAL = object : TypeLiteral<PacketCodec<*>>() {}

    override fun configure() {
        bindServices()

        bindProtocol(
            ServiceUpstream::class.java,
            InitJs5RemoteConnectionCodec::class.java,
            InitGameConnectionCodec::class.java,
            GameLoginCodec::class.java
        )

        bindProtocol(
            ServiceDownstream::class.java,
            ExchangeSessionKeyCodec::class.java,
            ClientOutOfDateCodec::class.java,
            BadSessionIdCodec::class.java,
            ClientProtocolOutOfDateCodec::class.java
        )

        bindProtocol(
            Js5RemoteDownstream::class.java,
            Js5OkCodec::class.java,
            Js5ClientOutOfDateCodec::class.java
        )
    }

    private fun bindServices() {
        val binder = Multibinder.newSetBinder(binder(), Service::class.java)
        binder.addBinding().to(Js5Service::class.java)
    }

    private fun bindProtocol(
        annotation: Class<out Annotation>,
        vararg codecs: Class<out PacketCodec<*>>
    ) {
        install(object : PrivateModule() {
            override fun configure() {
                val binder = Multibinder.newSetBinder(binder(), PACKET_CODEC_TYPE_LITERAL)
                codecs.forEach { binder.addBinding().to(it) }

                bind(Protocol::class.java)
                    .annotatedWith(annotation)
                    .to(Protocol::class.java)

                expose(Protocol::class.java)
                    .annotatedWith(annotation)
            }
        })
    }
}
