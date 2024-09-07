package org.rsmod.api.parsers.jackson.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import org.rsmod.api.parsers.jackson.JacksonCodec
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public object JacksonCoordGridCodec : JacksonCodec<CoordGrid>(CoordGrid::class.java) {
    override fun serialize(value: CoordGrid, gen: JsonGenerator, provider: SerializerProvider) {
        val local = MapSquareGrid.from(value)
        val mapSquare = MapSquareKey.from(value)
        gen.writeString("${value.level}_${mapSquare.x}_${mapSquare.z}_${local.x}_${local.z}")
    }

    override fun deserialize(parser: JsonParser, context: DeserializationContext): CoordGrid {
        val string = context.readValue(parser, String::class.java)
        val split = string.split('_')
        if (split.size != 5) {
            throw IOException("CoordGrid must contain 5 values separated by '_'. (ex: 0_50_50_0_0)")
        }
        val level = split[0].toInt()
        val mx = split[1].toInt()
        val mz = split[2].toInt()
        val lx = split[3].toInt()
        val lz = split[4].toInt()
        return CoordGrid(level, mx, mz, lx, lz)
    }
}
