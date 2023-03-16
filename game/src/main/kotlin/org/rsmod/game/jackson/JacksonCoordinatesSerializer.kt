package org.rsmod.game.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.rsmod.game.map.Coordinates

public object JacksonCoordinatesSerializer : StdSerializer<Coordinates>(Coordinates::class.java) {

    override fun serialize(value: Coordinates, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()
        gen.writeNumber(value.x)
        gen.writeNumber(value.z)
        gen.writeNumber(value.level)
        gen.writeEndArray()
    }
}
