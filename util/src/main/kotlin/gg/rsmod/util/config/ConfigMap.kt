package gg.rsmod.util.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.file.Files
import java.nio.file.Path

class ConfigMap(
    private val mapper: ObjectMapper,
    private val values: MutableMap<String, Any> = mutableMapOf()
) {

    fun load(path: Path): ConfigMap {
        if (!Files.exists(path)) {
            error("File does not exist: ${path.toAbsolutePath()}")
        }
        Files.newBufferedReader(path).use { reader ->
            val values: Map<String, Any> = mapper.readValue(reader)
            concat(values)
        }
        return this
    }

    fun extract(key: String): ConfigMap {
        val config = ConfigMap(mapper)
        val values = get<Map<String, Any>>(key) ?: error("Config map could not be built from key (key=$key)")
        return config.concat(values)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String): T? = values[key] as? T

    private fun concat(values: Map<String, Any>): ConfigMap {
        val filtered = values.removeBlanks()
        this.values.putAll(filtered)
        return this
    }

    private fun Map<String, Any>.removeBlanks(): Map<String, Any> {
        return filterNot {
            val value = it.value
            value is String && value.isBlank()
        }
    }
}
