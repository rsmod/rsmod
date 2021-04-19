package org.rsmod.plugins.api.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

interface ConfigFileLoader<T> {

    val mapper: ObjectMapper

    fun JsonNode.toType(): T

    fun loadAll(files: Iterable<Path>): Collection<T> {
        val configs = mutableListOf<T>()
        files.forEach { file ->
            val fileConfigs = Files.newInputStream(file).use { load(it) }
            configs.addAll(fileConfigs)
        }
        return configs
    }

    fun load(input: InputStream): Collection<T> {
        val nodes = input.use { mapper.readTree(input) }
        return nodes.map { it.toType() }
    }
}
