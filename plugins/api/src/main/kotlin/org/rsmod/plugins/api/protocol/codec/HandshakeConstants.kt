package org.rsmod.plugins.api.protocol.codec

object HandshakeConstants {

    const val INIT_GAME_CONNECTION = 14
    const val INIT_JS5REMOTE_CONNECTION = 15

    const val DECODER_PIPELINE = "channel_decoder"
    const val ADAPTER_PIPELINE = "channel_adapter"
    const val ENCODER_PIPELINE = "channel_encoder"
    const val RESPONSE_PIPELINE = "channel_response"
}
