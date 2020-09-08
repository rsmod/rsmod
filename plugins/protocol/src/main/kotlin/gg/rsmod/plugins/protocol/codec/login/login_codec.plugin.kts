package gg.rsmod.plugins.protocol.codec.login

import gg.rsmod.game.cache.GameCache
import gg.rsmod.game.config.GameConfig
import gg.rsmod.game.config.RsaConfig
import gg.rsmod.net.handshake.HandshakeHandlerMap
import gg.rsmod.plugins.protocol.codec.HandshakeConstants
import gg.rsmod.plugins.protocol.codec.ResponseEncoder

val handshakes: HandshakeHandlerMap by inject()
val dispatcher: LoginDispatcher by inject()
val gameConfig: GameConfig by inject()
val rsaConfig: RsaConfig by inject()
val cache: GameCache by inject()

handshakes.register {
    opcode = HandshakeConstants.INIT_GAME_CONNECTION
    decoder {
        name = HandshakeConstants.DECODER_PIPELINE
        provider = {
            LoginDecoder(
                revision = gameConfig.revision,
                rsaConfig = rsaConfig,
                cacheCrcs = cache.store.indexes.map { it.crc }.toIntArray()
            )
        }
    }
    encoder {
        name = HandshakeConstants.ENCODER_PIPELINE
        provider = { LoginEncoder }
    }
    adapter {
        name = HandshakeConstants.ADAPTER_PIPELINE
        provider = { LoginHandler(dispatcher) }
    }
    response {
        name = HandshakeConstants.RESPONSE_PIPELINE
        provider = { ResponseEncoder }
    }
}
