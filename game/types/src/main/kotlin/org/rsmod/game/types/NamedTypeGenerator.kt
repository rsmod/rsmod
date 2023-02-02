package org.rsmod.game.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

public class NamedTypeGenerator {

    public fun writeConstFiles(names: NamedTypeMapHolder, outputPath: Path, packageName: String) {
        if (!Files.exists(outputPath)) Files.createDirectories(outputPath)
        val generators = mapOf(
            "Interfaces" to ::generateInterfacesConst,
            "Components" to ::generateComponentsConst,
            "Items" to ::generateItemsConst,
            "Npcs" to ::generateNpcsConst,
            "Objs" to ::generateObjsConst
        )
        generators.forEach { (typeName, generator) ->
            Files.writeString(outputPath.resolve("$typeName.kt"), generator(names, typeName, packageName))
        }
    }

    public fun writeConfigMapFiles(
        names: NamedTypeMapHolder,
        outputPath: Path,
        mapper: ObjectMapper
    ): Unit = with(names) {
        if (!Files.exists(outputPath)) Files.createDirectories(outputPath)
        writeConfigMapFile(outputPath.resolve("interfaces.rscm"), interfaces.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("components.rscm"), components.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("items.rscm"), items.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("npcs.rscm"), npcs.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("objs.rscm"), objs.mapValues { it.value.id }, mapper)
    }

    public fun writeConfigMapFile(output: Path, names: Map<String, Int>, mapper: ObjectMapper) {
        if (names.isEmpty()) return
        Files.writeString(output, mapper.generateConfigMap(names))
    }

    public fun generateInterfacesConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedInterface::class.java, names.interfaces.mapValues { it.value.id })
    }

    public fun generateComponentsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedComponent::class.java, names.components.mapValues { it.value.id })
    }

    public fun generateItemsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedItem::class.java, names.items.mapValues { it.value.id })
    }

    public fun generateNpcsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedNpc::class.java, names.npcs.mapValues { it.value.id })
    }

    public fun generateObjsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedObject::class.java, names.objs.mapValues { it.value.id })
    }

    private fun <T> generate(
        fileName: String,
        packageName: String,
        type: Class<T>,
        names: Map<String, Int>
    ): String {
        val date = SimpleDateFormat().format(Date.from(Instant.now()))
        return FileSpec.builder(packageName, fileName)
            .indent("    ")
            .addFileComment("Auto-generated file using ${javaClass.simpleName} - DO NOT EDIT.")
            .addFileComment("\nCreated on $date")
            .addType(createObject(fileName, type, names))
            .build()
            .removeDeadCode()
    }

    private fun <T> createObject(fileName: String, type: Class<T>, names: Map<String, Int>): TypeSpec {
        val builder = TypeSpec.objectBuilder(fileName)
        val ordered = names.entries.sortedBy { it.value }
        ordered.forEach { (name, id) -> builder.addProperty(createProperty(name, type, id)) }
        return builder.build()
    }

    private fun <T> createProperty(name: String, type: Class<T>, id: Int): PropertySpec {
        val typeName = type.simpleName
        return PropertySpec.builder(name, type)
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return $typeName(%L)", id)
                    .build()
            ).build()
    }

    private fun FileSpec.removeDeadCode(): String = toString()
        .replace("import kotlin.Boolean\n", "")
        .replace("import kotlin.Byte\n", "")
        .replace("import kotlin.ByteArray\n", "")
        .replace("import kotlin.Int\n", "")
        .replace("import kotlin.Short\n", "")
        .replace("import kotlin.Unit\n", "")
        .replace(": Unit {", " {")
        .replace("public ", "")

    private fun ObjectMapper.generateConfigMap(names: Map<String, Int>): String {
        return writeValueAsString(names).replace(" = ", ":")
    }
}
