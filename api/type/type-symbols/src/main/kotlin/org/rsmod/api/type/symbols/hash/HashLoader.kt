package org.rsmod.api.type.symbols.hash

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

public object HashLoader {
    private const val SEPARATOR = '\t'
    private const val COMMENT = '#'

    public fun read(file: Path): Map<Long, String> {
        val lines = Files.readAllLines(file)
        return read(lines)
    }

    public fun read(lines: Iterable<String>): Map<Long, String> {
        val map = Long2ObjectOpenHashMap<String>()
        val names = hashSetOf<String>()
        for (line in lines) {
            if (line.startsWith(COMMENT)) {
                continue
            }
            val split = line.split(SEPARATOR, limit = 2)
            if (split.size != 2) {
                continue
            }
            val hash =
                split[0].toLongOrNull()
                    ?: throw HashSymbolError(
                        "`${split[0]}` could not be converted to a hash value (Long)."
                    )
            val name = split[1]
            if (!names.add(name)) {
                val previous = map.long2ObjectEntrySet().first { it.value == name }
                throw NamedHashError("Hash for `$name` is already defined: $previous")
            } else if (map.containsKey(hash)) {
                val previous = map.long2ObjectEntrySet().first { it.longKey == hash }
                throw HashOverlapError("`$hash` hash is already taken: $previous")
            }
            map[hash] = name
            names += name
        }
        return map
    }
}

public class HashSymbolError(message: String) : IOException(message)

public class NamedHashError(message: String) : IOException(message)

public class HashOverlapError(message: String) : IOException(message)
