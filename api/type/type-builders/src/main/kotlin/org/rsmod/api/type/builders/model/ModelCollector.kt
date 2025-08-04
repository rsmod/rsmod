package org.rsmod.api.type.builders.model

import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import org.rsmod.api.cache.types.model.ModelByteDefinition
import org.rsmod.api.type.builders.resource.TypeResourceFile
import org.rsmod.api.type.symbols.name.NameMapping

public class ModelCollector @Inject constructor(private val names: NameMapping) {
    public fun loadAndCollect(builders: Iterable<ModelBuilder>): List<ModelByteDefinition> {
        builders.forEach(ModelBuilder::onPackModelTask)
        return builders.toModelDefinitions()
    }

    private fun Iterable<ModelBuilder>.toModelDefinitions(): List<ModelByteDefinition> {
        val resources = flatMap(ModelBuilder::resources).resourceModelTypes()
        resources.assertDistinct()
        return resources
    }

    private fun Iterable<TypeResourceFile>.resourceModelTypes(): List<ModelByteDefinition> {
        return map { it.modelType() }
    }

    private fun TypeResourceFile.modelType(): ModelByteDefinition {
        val fileName = relativePath.substringAfterLast('/')
        if (fileName.contains('.')) {
            val message = "Model file must not have an extension: $relativePath"
            throw IOException(message)
        }

        val input = clazz.getResourceAsStream(relativePath)
        if (input == null) {
            val message = "Model resource file not found: path=$relativePath, class=$clazz"
            throw FileNotFoundException(message)
        }

        val id = names.models[fileName]
        if (id == null) {
            val message = "Model name not found in `model.sym`: $fileName"
            throw IllegalStateException(message)
        }

        val bytes = input.use(InputStream::readAllBytes)
        return ModelByteDefinition(id, bytes)
    }

    private fun List<ModelByteDefinition>.assertDistinct() {
        val grouped = groupBy(ModelByteDefinition::type)
        val duplicates = grouped.filterValues { it.size > 1 }
        if (duplicates.isNotEmpty()) {
            val lookup = names.models.entries.associate { it.value to it.key }
            val names = duplicates.keys.map(lookup::getValue)
            val duplicateKeys = names.joinToString(", ")
            val message = "Duplicate Model files found for symbol: $duplicateKeys"
            throw IllegalStateException(message)
        }
    }
}
