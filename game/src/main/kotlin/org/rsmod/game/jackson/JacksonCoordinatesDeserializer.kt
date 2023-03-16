package org.rsmod.game.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.rsmod.game.map.Coordinates
import java.io.IOException

public object JacksonCoordinatesDeserializer : StdDeserializer<Coordinates>(Coordinates::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Coordinates {
        val array = ctxt.readValue(p, IntArray::class.java)
        if (array.size !in 2..3) throw IOException("Coordinates must contain 2 or 3 values. (received=$array)")
        return Coordinates(array[0], array[1], if (array.size < 3) 0 else array[2])
    }
}
