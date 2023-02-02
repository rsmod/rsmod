package org.rsmod.plugins.api.cache.map.xtea

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.openrs2.crypto.XteaKey
import org.rsmod.game.cache.CachePath
import org.rsmod.json.Json
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

private const val FILE_NAME = "xteas.json"

public class XteaFileLoader @Inject constructor(
    @CachePath private val cachePath: Path,
    @Json private val mapper: ObjectMapper,
    private val repository: XteaRepository
) {

    public fun load() {
        /* xtea file should be one path level above packed cache */
        val file = cachePath.parent.resolve(FILE_NAME)
        Files.newBufferedReader(file).use {
            val fileXtea = mapper.readValue(it, Array<XteaFile>::class.java)
            fileXtea.forEach { xtea ->
                repository[xtea.mapSquare] = XteaKey.fromIntArray(xtea.key)
            }
            logger.info { "Loaded ${fileXtea.size} map XTEA keys." }
        }
    }
}
