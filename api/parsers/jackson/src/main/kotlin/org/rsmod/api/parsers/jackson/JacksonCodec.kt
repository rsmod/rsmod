package org.rsmod.api.parsers.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public abstract class JacksonCodec<T>(public val type: Class<T>) {
    internal val serializer: StdSerializer<T> = createSerializer()

    internal val deserializer: StdDeserializer<T> = createDeserializer()

    public abstract fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider)

    public abstract fun deserialize(parser: JsonParser, context: DeserializationContext): T

    private fun createSerializer(): StdSerializer<T> =
        object : StdSerializer<T>(type) {
            override fun serialize(
                value: T,
                gen: JsonGenerator,
                provider: SerializerProvider,
            ): Unit = this@JacksonCodec.serialize(value, gen, provider)
        }

    private fun createDeserializer(): StdDeserializer<T> =
        object : StdDeserializer<T>(type) {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T =
                this@JacksonCodec.deserialize(p, ctxt)
        }
}
