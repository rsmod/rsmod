package org.rsmod.plugins.api.cache.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.InputStream

interface ConfigFileLoader<T> {

    val mapper: ObjectMapper

    fun JsonNode.toConfigType(): T

    fun loadAll(files: Iterable<File>): Collection<T> {
        val configs = mutableListOf<T>()
        files.forEach { file ->
            val fileConfigs = file.inputStream().use { load(it) }
            configs.addAll(fileConfigs)
        }
        return configs
    }

    fun load(input: InputStream): Collection<T> {
        val nodes = input.use { mapper.readTree(input) }
        return nodes.map { it.toConfigType() }
    }
}
