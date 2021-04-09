package org.rsmod.plugins.api.cache.config.file

import org.rsmod.game.name.TypeNamedMap
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class NamedConfigFileMap(
    private val extensions: MutableSet<String> = mutableSetOf()
) : TypeNamedMap<MutableList<File>>() {

    fun loadDirectory(path: Path) {
        if (!Files.isDirectory(path)) {
            path.loadFile()
            return
        }
        Files.list(path).forEach { sub ->
            if (Files.isDirectory(sub)) {
                loadDirectory(sub)
            } else {
                sub.loadFile()
            }
        }
    }

    operator fun plusAssign(extension: String) {
        extensions += extension
        if (!containsKey(extension)) {
            this[extension] = mutableListOf()
        }
    }

    private fun Path.loadFile() {
        val extension = fileName.toString().midExtension() ?: return
        if (!extensions.contains(extension)) return
        val files = getValue(extension)
        files.add(toFile())
    }

    private fun String.midExtension(): String? {
        val split = split('.')
        if (split.size < 3) return null
        return split[1]
    }
}
