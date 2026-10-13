package org.runerealm.api.parsers.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import org.runerealm.api.parsers.jackson.codec.JacksonCoordGridCodec

internal class JacksonCodecs : SimpleModule() {
    init {
        registerCodec(JacksonCoordGridCodec)
    }
}

private fun <T> SimpleModule.registerCodec(codec: JacksonCodec<T>) {
    addSerializer(codec.type, codec.serializer)
    addDeserializer(codec.type, codec.deserializer)
}
