package org.rsmod.plugins.api.cache.name.ui

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.model.ui.Component
import org.rsmod.game.name.NamedTypeLoader
import org.rsmod.plugins.api.config.file.DefaultExtensions
import org.rsmod.plugins.api.config.file.NamedConfigFileMap
import org.rsmod.plugins.api.cache.type.ui.ComponentTypeList
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

private val logger = InlineLogger()

class ComponentNameLoader @Inject constructor(
    private val mapper: ObjectMapper,
    private val files: NamedConfigFileMap,
    private val names: ComponentNameMap,
    private val types: ComponentTypeList,
    private val interfaces: UserInterfaceNameMap
) : NamedTypeLoader {

    override fun load(directory: Path) {
        val files = files.getValue(DefaultExtensions.COMPONENT_NAMES)
        files.forEach(::loadAliasFile)
        logger.info { "Loaded ${names.size} component type name${if (names.size != 1) "s" else ""}" }
    }

    private fun loadAliasFile(file: Path) {
        Files.newInputStream(file).use { input ->
            val nodes = mapper.readValue(input, Array<NamedComponent>::class.java)
            nodes.forEach { node ->
                val (name, parentName, child) = node
                val parent = interfaces[parentName] ?: error("Interface with name \"$parentName\" not found.")
                val component = Component(parent.id, child)
                val type = types.getOrNull(component.packed) ?: error(
                    "Component type does not exist " +
                        "(component=$parent:$child, file=${file.fileName}, path=${file.toAbsolutePath()})"
                )
                names[name] = type
            }
        }
    }
}

private data class NamedComponent(
    val name: String,
    val parent: String,
    val child: Int
)
