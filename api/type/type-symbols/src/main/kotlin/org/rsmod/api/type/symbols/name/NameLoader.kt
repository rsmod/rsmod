package org.rsmod.api.type.symbols.name

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.nio.file.Files
import java.nio.file.Path

public object NameLoader {
    private const val SEPARATOR = '\t'
    private const val COMMENT = '#'

    public fun read(file: Path): Map<String, Int> {
        val lines = Files.readAllLines(file)
        return read(lines)
    }

    public fun readComponents(file: Path, interfaces: Map<String, Int>): Map<String, Int> {
        val lines = Files.readAllLines(file)
        val transformer = interfaces.componentNameTransformer()
        return read(lines, transformer)
    }

    public fun read(lines: Iterable<String>, transform: NameTransform? = null): Map<String, Int> {
        val map = Object2IntOpenHashMap<String>()
        val ids = IntOpenHashSet()
        for (line in lines) {
            if (line.startsWith(COMMENT)) {
                continue
            }
            val split = line.split(SEPARATOR, limit = 2)
            if (split.size != 2) {
                continue
            }
            val id =
                transform?.id?.invoke(split[0])
                    ?: split[0].toIntOrNull()
                    ?: throw NameError("Invalid mapping id: ${split[0]}.")
            val name = transform?.name?.invoke(split[1]) ?: split[1]
            if (!ids.add(id)) {
                val previous = map.object2IntEntrySet().first { it.intValue == id }.key
                throw NameIdOverlap("`id` $id for `$name` already taken by `$previous`.")
            }
            map[name] = id
        }
        return map
    }

    private fun Map<String, Int>.componentNameTransformer(): NameTransform {
        val id: (String) -> Int = { name ->
            val split = name.split(":", limit = 2)
            if (split.size != 2) {
                error(
                    "Component id must be lead by its interface parent name such as " +
                        "`toplevel_interface:1`. (name=$name)"
                )
            }
            val parentName = split[0]
            val childId = split[1].toComponentChild()
            val parent = parentName.toInterfaceId(this, name)
            (parent shl 16) or childId
        }
        return NameTransform(id)
    }
}

public data class NameTransform(
    val id: ((String) -> Int)? = null,
    val name: ((String) -> String)? = null,
)

public class NameIdOverlap(message: String) : IllegalArgumentException(message)

public class NameError(message: String) : IllegalArgumentException(message)

private fun String.toComponentChild(): Int =
    toIntOrNull()
        ?: error(
            "Component id must be suffixed after interface parent name " +
                "such as `toplevel_interface:1`"
        )

private fun String.toInterfaceId(interfaces: Map<String, Int>, component: String): Int =
    interfaces[this]
        ?: error(
            "Interface `$this` does not exist. " +
                "Cannot reference it as a parent for component `$component`."
        )
