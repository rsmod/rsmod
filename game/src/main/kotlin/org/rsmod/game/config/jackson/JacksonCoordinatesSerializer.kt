package org.rsmod.game.config.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.rsmod.game.model.map.Coordinates

public object JacksonCoordinatesSerializer : StdSerializer<Coordinates>(Coordinates::class.java) {

    override fun serialize(value: Coordinates, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()
        gen.writeNumber(value.x)
        gen.writeNumber(value.y)
        gen.writeNumber(value.level)
        gen.writeEndArray()
    }
}
