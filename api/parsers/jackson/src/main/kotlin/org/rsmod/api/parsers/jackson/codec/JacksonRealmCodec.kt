package org.rsmod.api.parsers.jackson.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import org.rsmod.api.parsers.jackson.JacksonCodec
import org.rsmod.api.realm.Realm

public object JacksonRealmCodec : JacksonCodec<Realm>(Realm::class.java) {
    override fun serialize(value: Realm, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.dbName)
    }

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Realm {
        val string = context.readValue(parser, String::class.java)
        val realm = Realm.entries.firstOrNull { it.dbName == string }
        if (realm == null) {
            val available = Realm.entries.joinToString(",", transform = Realm::dbName)
            throw IOException("Unknown realm name '$string'. Available: $available")
        }
        return realm
    }
}
