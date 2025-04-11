package org.rsmod.api.parsers.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import org.rsmod.api.parsers.jackson.codec.JacksonCoordGridCodec
import org.rsmod.api.parsers.jackson.codec.JacksonRealmCodec

internal class JacksonCodecs : SimpleModule() {
    init {
        registerCodec(JacksonCoordGridCodec)
        registerCodec(JacksonRealmCodec)
    }
}

private fun <T> SimpleModule.registerCodec(codec: JacksonCodec<T>) {
    addSerializer(codec.type, codec.serializer)
    addDeserializer(codec.type, codec.deserializer)
}
