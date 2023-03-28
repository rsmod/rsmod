package org.rsmod.plugins.types.gen

import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.rsmod.plugins.types.NamedAnimation
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedEnum
import org.rsmod.plugins.types.NamedGraphic
import org.rsmod.plugins.types.NamedInterface
import org.rsmod.plugins.types.NamedInventory
import org.rsmod.plugins.types.NamedItem
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedObject
import org.rsmod.plugins.types.NamedParameter
import org.rsmod.plugins.types.NamedScript
import org.rsmod.plugins.types.NamedStruct
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp
import org.rsmod.plugins.types.ScriptTypeList
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import kotlin.reflect.KClass

public class NamedTypeGenerator {

    public fun writeConstFiles(names: NamedTypeMapHolder, outputPath: Path, packageName: String) {
        if (!Files.exists(outputPath)) Files.createDirectories(outputPath)
        val generators = mapOf(
            "Interfaces" to ::generateInterfacesConst,
            "Components" to ::generateComponentsConst,
            "Items" to ::generateItemsConst,
            "Npcs" to ::generateNpcsConst,
            "Objs" to ::generateObjsConst,
            "Animations" to ::generateAnimsConst,
            "Graphics" to ::generateGfxConst,
            "Enums" to ::generateEnumsConst,
            "Structs" to ::generateStructsConst,
            "Params" to ::generateParamsConst,
            "Invs" to ::generateInvsConst,
            "Varps" to ::generateVarpsConst,
            "Varbits" to ::generateVarbitsConst,
            "Scripts" to ::generateScriptsConst
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
        writeConfigMapFile(outputPath.resolve("anims.rscm"), anims.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("graphics.rscm"), graphics.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("enums.rscm"), enums.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("structs.rscm"), structs.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("params.rscm"), parameters.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("invs.rscm"), inventories.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("varps.rscm"), varps.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("varbits.rscm"), varbits.mapValues { it.value.id }, mapper)
        writeConfigMapFile(outputPath.resolve("scripts.rscm"), scripts.mapValues { it.value.id }, mapper)
    }

    public fun writeConfigMapFile(output: Path, names: Map<String, Int>, mapper: ObjectMapper) {
        /* if there are no names we don't have to bother mapping */
        if (names.isEmpty()) {
            /* if there is an old file with same name - delete it to keep names in sync */
            if (Files.exists(output)) Files.delete(output)
            return
        }
        val map = mapper.writeValueAsString(names).replace(" = ", ":")
        Files.writeString(output, map)
    }

    public fun generateInterfacesConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedInterface::class, names.interfaces.mapValues { it.value.id })
    }

    public fun generateComponentsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedComponent::class, names.components.mapValues { it.value.id })
    }

    public fun generateItemsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedItem::class, names.items.mapValues { it.value.id })
    }

    public fun generateNpcsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedNpc::class, names.npcs.mapValues { it.value.id })
    }

    public fun generateObjsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedObject::class, names.objs.mapValues { it.value.id })
    }

    public fun generateAnimsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedAnimation::class, names.anims.mapValues { it.value.id })
    }

    public fun generateGfxConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedGraphic::class, names.graphics.mapValues { it.value.id })
    }

    public fun generateEnumsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedEnum::class, names.enums.mapValues { it.value.id })
    }

    public fun generateStructsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedStruct::class, names.structs.mapValues { it.value.id })
    }

    public fun generateParamsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedParameter::class, names.parameters.mapValues { it.value.id })
    }

    public fun generateInvsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedInventory::class, names.inventories.mapValues { it.value.id })
    }

    public fun generateVarpsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedVarp::class, names.varps.mapValues { it.value.id })
    }

    public fun generateVarbitsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedVarbit::class, names.varbits.mapValues { it.value.id })
    }

    public fun generateScriptsConst(names: NamedTypeMapHolder, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedScript::class, names.scripts.mapValues { it.value.id })
    }

    private fun <T : Any> generate(
        fileName: String,
        packageName: String,
        type: KClass<T>,
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

    private fun <T : Any> createObject(fileName: String, type: KClass<T>, names: Map<String, Int>): TypeSpec {
        val builder = TypeSpec.objectBuilder(fileName)
        val ordered = names.entries.sortedBy { it.value }
        ordered.forEach { (name, id) -> builder.addProperty(createProperty(name, type, id)) }
        return builder.build()
    }

    private fun <T : Any> createProperty(name: String, type: KClass<T>, id: Int): PropertySpec {
        return PropertySpec.builder(name, type.namedTypeName())
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return %T(%L)", type, id)
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

    private fun <T : Any> KClass<T>.namedTypeName(): TypeName = when (this) {
        NamedParameter::class -> asTypeName().parameterizedBy(Any::class.asTypeName())
        NamedEnum::class -> asTypeName().parameterizedBy(Any::class.asTypeName(), Any::class.asTypeName())
        NamedScript::class -> asTypeName().parameterizedBy(ScriptTypeList::class.asTypeName())
        else -> asTypeName()
    }
}
