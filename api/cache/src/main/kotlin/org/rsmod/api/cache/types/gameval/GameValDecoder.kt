package org.rsmod.api.cache.types.gameval

import java.nio.charset.StandardCharsets
import kotlin.collections.iterator
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.openrs2.cache.Js5Index
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5GameValGroup
import org.rsmod.game.type.gameval.GameValNameMap
import org.rsmod.game.type.gameval.GameValNameMapBuilder

public object GameValDecoder {
    public fun decodeAll(cache: Cache): GameValNameMap {
        val builder = GameValNameMapBuilder()
        val groups = cache.list(Js5Archives.GAMEVALS)
        for (group in groups) {
            val files = cache.list(Js5Archives.GAMEVALS, group.id)
            when (group.id) {
                Js5GameValGroup.TABLETYPES -> decodeTables(cache, builder, group.id, files)
                Js5GameValGroup.IFTYPES_V2 -> decodeInterfaces(cache, builder, group.id, files)
                else -> decodeFiles(cache, builder, group.id, files)
            }
        }
        return builder.build()
    }

    private fun decodeTables(
        cache: Cache,
        builder: GameValNameMapBuilder,
        group: Int,
        files: Iterator<Js5Index.File>,
    ) {
        val tableNames = mutableMapOf<Int, String>()
        for (file in files) {
            val data = cache.read(Js5Archives.GAMEVALS, group, file.id)
            data.use {
                it.readUnsignedByte() // Always 1.

                val tableName = it.readString()
                tableNames[file.id] = tableName

                var columnId = 0
                while (it.isReadable) {
                    val check = it.readUnsignedByte().toInt()
                    if (check == 0) {
                        break
                    }
                    val columnName = it.readString()
                    builder.putDbColumn(file.id, columnId++, columnName)
                }
            }
        }
        builder.putNames(group, tableNames)
    }

    private fun decodeInterfaces(
        cache: Cache,
        builder: GameValNameMapBuilder,
        group: Int,
        files: Iterator<Js5Index.File>,
    ) {
        val interfaceNames = mutableMapOf<Int, String>()
        for (file in files) {
            val data = cache.read(Js5Archives.GAMEVALS, group, file.id)
            data.use {
                val interfaceName = it.readString()
                interfaceNames[file.id] = interfaceName

                while (it.isReadable) {
                    val componentId = it.readUnsignedShort()
                    if (componentId == 0xFFFF) {
                        break
                    }
                    val componentName = it.readString()
                    builder.putComponent(file.id, componentId, componentName)
                }
            }
        }
        builder.putNames(group, interfaceNames)
    }

    private fun decodeFiles(
        cache: Cache,
        builder: GameValNameMapBuilder,
        group: Int,
        files: Iterator<Js5Index.File>,
    ) {
        val names = mutableMapOf<Int, String>()
        for (file in files) {
            val data = cache.read(Js5Archives.GAMEVALS, group, file.id)
            val name = data.use { it.toString(StandardCharsets.UTF_8) }
            names[file.id] = name
        }
        builder.putNames(group, names)
    }
}
