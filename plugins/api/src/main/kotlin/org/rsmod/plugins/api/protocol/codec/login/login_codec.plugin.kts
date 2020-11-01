package org.rsmod.plugins.api.protocol.codec.login

import org.rsmod.game.cache.GameCache
import org.rsmod.game.config.GameConfig
import org.rsmod.game.config.RsaConfig
import org.rsmod.net.handshake.HandshakeHandlerMap
import org.rsmod.plugins.api.protocol.codec.HandshakeConstants
import org.rsmod.plugins.api.protocol.codec.ResponseEncoder
import org.rsmod.plugins.api.protocol.codec.account.AccountDispatcher
import org.rsmod.plugins.api.protocol.packet.login.LoginPacketMap

val dispatcher: AccountDispatcher by inject()
val handshakes: HandshakeHandlerMap by inject()
val loginPackets: LoginPacketMap by inject()
val gameConfig: GameConfig by inject()
val rsaConfig: RsaConfig by inject()
val cache: GameCache by inject()

dispatcher.start()

handshakes.register {
    opcode = HandshakeConstants.INIT_GAME_CONNECTION
    decoder {
        name = HandshakeConstants.DECODER_PIPELINE
        provider = {
            LoginDecoder(
                majorRevision = gameConfig.majorRevision,
                minorRevision = gameConfig.minorRevision,
                rsaConfig = rsaConfig,
                cacheCrcs = cache.archiveCrcs,
                loginPackets = loginPackets
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
