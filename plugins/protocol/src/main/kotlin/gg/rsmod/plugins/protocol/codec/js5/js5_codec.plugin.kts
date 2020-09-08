package gg.rsmod.plugins.protocol.codec.js5

import gg.rsmod.game.config.GameConfig
import gg.rsmod.net.handshake.HandshakeHandlerMap
import gg.rsmod.plugins.protocol.codec.HandshakeConstants
import gg.rsmod.plugins.protocol.codec.ResponseEncoder

val handshakes: HandshakeHandlerMap by inject()
val dispatcher: Js5Dispatcher by inject()
val gameConfig: GameConfig by inject()

handshakes.register {
    opcode = HandshakeConstants.INIT_JS5REMOTE_CONNECTION
    decoder {
        name = HandshakeConstants.DECODER_PIPELINE
        provider = { Js5Decoder(gameConfig.revision) }
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
