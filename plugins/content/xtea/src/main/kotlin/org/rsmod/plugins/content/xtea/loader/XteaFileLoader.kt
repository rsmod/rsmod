package org.rsmod.plugins.content.xtea.loader

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import java.nio.file.Files
import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.domain.repo.XteaRepository

private const val FILE = "xteas.json"
private val logger = InlineLogger()

class XteaFileLoader @Inject constructor(
    private val config: GameConfig,
    private val mapper: ObjectMapper,
    private val repository: XteaRepository
) {

    fun load() {
        val file = config.cachePath.resolve(FILE)
        Files.newBufferedReader(file).use { reader ->
            val list = mapper.readValue(reader, Array<Xteas>::class.java)
            list.forEach { repository.insert(it.key, it.mapSquare) }
            logger.debug { "Loaded ${list.size} xteas keys" }
        }
    }
}

private data class Xteas(
    @JsonProperty("mapsquare")
    val mapSquare: Int,
    val key: IntArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Xteas

        if (mapSquare != other.mapSquare) return false
        if (!key.contentEquals(other.key)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mapSquare
        result = 31 * result + key.contentHashCode()
        return result
    }
}
