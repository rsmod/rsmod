package org.rsmod.plugins.api.protocol.codec.js5

import org.rsmod.game.config.GameConfig
import org.rsmod.game.net.handshake.HandshakeHandlerMap
import org.rsmod.plugins.api.protocol.codec.HandshakeConstants
import org.rsmod.plugins.api.protocol.codec.ResponseEncoder

val handshakes: HandshakeHandlerMap by inject()
val dispatcher: Js5Dispatcher by inject()
val gameConfig: GameConfig by inject()

dispatcher.cacheResponses()

handshakes.register {
    opcode = HandshakeConstants.INIT_JS5REMOTE_CONNECTION
    decoder {
        name = HandshakeConstants.DECODER_PIPELINE
        provider = { Js5Decoder(gameConfig.majorRevision) }
    }
    encoder {
        name = HandshakeConstants.ENCODER_PIPELINE
        provider = { Js5Encoder }
    }
    adapter {
        name = HandshakeConstants.ADAPTER_PIPELINE
        provider = { Js5Handler(dispatcher) }
    }
    response {
        name = HandshakeConstants.RESPONSE_PIPELINE
        provider = { ResponseEncoder }
    }
}
