package org.rsmod.plugins.api.cache.map.xtea

import com.fasterxml.jackson.databind.ObjectMapper
import org.openrs2.crypto.XteaKey
import org.rsmod.game.config.GameConfig
import org.rsmod.json.Json
import org.rsmod.plugins.api.cachePath
import java.nio.file.Files
import javax.inject.Inject

private const val FILE_NAME = "xteas.json"

public class XteaRepositoryLoader @Inject constructor(
    @Json private val mapper: ObjectMapper,
    private val config: GameConfig
) {

    public fun load(): XteaRepository {
        val repo = XteaRepository()
        /* xtea file should be one path level above packed cache */
        val file = config.cachePath.resolve(FILE_NAME)
        Files.newBufferedReader(file).use {
            val fileXtea = mapper.readValue(it, Array<XteaFile>::class.java)
            fileXtea.forEach { xtea ->
                repo[xtea.mapSquare] = XteaKey.fromIntArray(xtea.key)
            }
        }
        return repo
    }
}
