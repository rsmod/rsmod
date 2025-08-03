package org.rsmod.api.type.builders.clientscript

import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.types.clientscript.ClientScriptByteDefinition
import org.rsmod.api.type.builders.resource.TypeResourceFile
import org.rsmod.api.type.symbols.name.NameMapping

public class ClientScriptCollector @Inject constructor(private val names: NameMapping) {
    public fun loadAndCollect(
        builders: Iterable<ClientScriptBuilder>
    ): List<ClientScriptByteDefinition> {
        builders.forEach(ClientScriptBuilder::onPackCs2Task)
        return builders.toClientScriptDefinitions()
    }

    private fun Iterable<ClientScriptBuilder>.toClientScriptDefinitions():
        List<ClientScriptByteDefinition> {
        val resources = flatMap(ClientScriptBuilder::resources).resourceClientScriptTypes()
        resources.assertDistinct()
        return resources
    }

    private fun Iterable<TypeResourceFile>.resourceClientScriptTypes():
        List<ClientScriptByteDefinition> {
        return map { it.clientScriptType() }
    }

    private fun TypeResourceFile.clientScriptType(): ClientScriptByteDefinition {
        val fileName = relativePath.substringAfterLast('/')
        if (fileName.contains('.')) {
            val message = "ClientScript file must not have an extension: $relativePath"
            throw IOException(message)
        }

        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "ClientScript resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }

        val id = names.clientscripts[fileName]
        if (id == null) {
            val message = "ClientScript with name not found in `clientscript.sym`: $fileName"
            throw IllegalStateException(message)
        }

        if (!fileName.startsWith("[clientscript,") && !fileName.startsWith("[proc,")) {
            val message =
                "ClientScript file name must start with: `[clientscript,` or `[proc,`: $fileName"
            throw IOException(message)
        }

        if (!fileName.endsWith(']')) {
            val message = "ClientScript file name must end with `]`: $fileName"
            throw IOException(message)
        }

        val bytes = input.use(InputStream::readAllBytes)
        return ClientScriptByteDefinition(id, bytes)
    }

    private fun List<ClientScriptByteDefinition>.assertDistinct() {
        val grouped = groupBy(ClientScriptByteDefinition::type)
        val duplicates = grouped.filterValues { it.size > 1 }
        if (duplicates.isNotEmpty()) {
            val lookup = names.clientscripts.entries.associate { it.value to it.key }
            val names = duplicates.keys.map(lookup::getValue)
            val duplicateKeys = names.joinToString(", ")
            val message = "Duplicate ClientScript files found for symbol: $duplicateKeys"
            throw IllegalStateException(message)
        }
    }
}
