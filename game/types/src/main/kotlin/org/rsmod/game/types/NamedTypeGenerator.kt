package org.rsmod.game.types

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import javax.inject.Inject

private val DEFAULT_OUTPUT_PATH = Path.of("types-generated/src/main/gen/org/rsmod/types/generated")
private const val DEFAULT_PACKAGE = "org.rsmod.types.generated"

public class NamedTypeGenerator @Inject constructor(private val names: NamedTypes) {

    public fun writeFiles(path: Path = DEFAULT_OUTPUT_PATH, packageName: String = DEFAULT_PACKAGE) {
        val generators = mapOf(
            "Interfaces" to ::generateInterfaces,
            "Components" to ::generateComponents,
            "Items" to ::generateItems,
            "Npcs" to ::generateNpcs,
            "Objs" to ::generateObjs
        )
        if (!Files.exists(path)) Files.createDirectories(path)
        generators.forEach { (typeName, generator) ->
            Files.writeString(path.resolve("$typeName.kt"), generator(names, typeName, packageName))
        }
    }

    public fun generateInterfaces(names: NamedTypes, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedInterface::class.java, names.interfaces.mapValues { it.value.id })
    }

    public fun generateComponents(names: NamedTypes, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedComponent::class.java, names.components.mapValues { it.value.id })
    }

    public fun generateItems(names: NamedTypes, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedItem::class.java, names.items.mapValues { it.value.id })
    }

    public fun generateNpcs(names: NamedTypes, fileName: String, packageName: String): String {
        return generate(fileName, packageName, NamedNpc::class.java, names.npcs.mapValues { it.value.id })
    }

    public fun generateObjs(names: NamedTypes, fileName: String, packageName: String): String {
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

    private fun FileSpec.removeDeadCode(): String {
        return toString()
            .replace("import kotlin.Boolean\n", "")
            .replace("import kotlin.Byte\n", "")
            .replace("import kotlin.ByteArray\n", "")
            .replace("import kotlin.Int\n", "")
            .replace("import kotlin.Short\n", "")
            .replace("import kotlin.Unit\n", "")
            .replace(": Unit {", " {")
            .replace("public ", "")
    }
}
