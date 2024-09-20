package org.rsmod.server.shared.module

import com.google.inject.Provider
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.PathWalkOption
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.walk
import kotlin.streams.toList
import org.rsmod.api.type.symbols.hash.HashLoader
import org.rsmod.api.type.symbols.hash.HashMapping
import org.rsmod.api.type.symbols.name.NameLoader
import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.module.ExtendedModule
import org.rsmod.server.shared.DirectoryConstants

object SymbolModule : ExtendedModule() {
    override fun bind() {
        bindProvider(NameMappingProvider::class.java)
        bindProvider(HashMappingProvider::class.java)
    }
}

private class NameMappingProvider : Provider<NameMapping> {
    override fun get(): NameMapping {
        val dirs = shallowSymbolDirectories()
        val objs = dirs.readSymbols("obj")
        val interfaces = dirs.readSymbols("interface")
        val components = dirs.readComps("component", interfaces)
        val varps = dirs.readSymbols("varp")
        val varbits = dirs.readSymbols("varbit")
        val params = dirs.readSymbols("param")
        val npcs = dirs.readSymbols("npc")
        val locs = dirs.readSymbols("loc")
        val varobjbits = dirs.readSymbols("varobj")
        val categories = dirs.readSymbols("category")
        val content = dirs.readSymbols("content")
        val enums = dirs.readSymbols("enum")
        val invs = dirs.readSymbols("inv")
        val modLevels = dirs.readSymbols("mod", fileName = "names.level")
        val modGroups = dirs.readSymbols("mod", fileName = "names.group")
        val seqs = dirs.readSymbols("seq")
        val mesanims = dirs.readSymbols("mesanim")
        val synths = dirs.readSymbols("synth")
        val fonts = dirs.readSymbols("font")
        val stats = dirs.readSymbols("stat")
        val currencies = dirs.readSymbols("currency")
        val timers = dirs.readSymbols("timer")
        return NameMapping(
            categories = categories,
            objs = objs,
            interfaces = interfaces,
            components = components,
            varps = varps,
            varbits = varbits,
            params = params,
            npcs = npcs,
            locs = locs,
            varobjbits = varobjbits,
            enums = enums,
            invs = invs,
            modLevels = modLevels,
            modGroups = modGroups,
            seqs = seqs,
            mesanims = mesanims,
            content = content,
            synths = synths,
            fonts = fonts,
            stats = stats,
            currencies = currencies,
            timers = timers,
        )
    }

    private fun ShallowDirectoryMap.readSymbols(
        parent: String,
        fileName: String,
    ): Map<String, Int> {
        val file = find(parent, fileName) ?: return emptyMap()
        return NameLoader.read(file)
    }

    private fun ShallowDirectoryMap.readComps(
        parent: String,
        interfaces: Map<String, Int>,
        fileName: String,
    ): Map<String, Int> {
        val file = find(parent, fileName) ?: return emptyMap()
        return NameLoader.readComponents(file, interfaces)
    }

    private fun List<ShallowDirectoryMap>.readSymbols(
        parent: String,
        fileName: String = DEFAULT_FILE_NAME,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        for (entry in this) {
            merged += entry.readSymbols(parent, fileName)
        }
        return merged
    }

    private fun List<ShallowDirectoryMap>.readComps(
        parent: String,
        interfaces: Map<String, Int>,
        fileName: String = DEFAULT_FILE_NAME,
    ): Map<String, Int> {
        val merged = mutableMapOf<String, Int>()
        for (entry in this) {
            merged += entry.readComps(parent, interfaces, fileName)
        }
        return merged
    }

    private companion object {
        private const val DEFAULT_FILE_NAME = "names"
    }
}

private class HashMappingProvider : Provider<HashMapping> {
    override fun get(): HashMapping {
        val dirs = shallowSymbolDirectories()
        val objs = dirs.readHashes("obj")
        val interfaces = dirs.readHashes("interface")
        val components = dirs.readHashes("component")
        val varps = dirs.readHashes("varp")
        val varbits = dirs.readHashes("varbit")
        val locs = dirs.readHashes("loc")
        val params = dirs.readHashes("param")
        val npcs = dirs.readHashes("npc")
        val enums = dirs.readHashes("enum")
        val invs = dirs.readHashes("inv")
        val seqs = dirs.readHashes("seq")
        val fonts = dirs.readHashes("font")
        return HashMapping(
            objs = objs,
            locs = locs,
            interfaces = interfaces,
            components = components,
            varps = varps,
            varbits = varbits,
            params = params,
            npcs = npcs,
            enums = enums,
            invs = invs,
            seqs = seqs,
            fonts = fonts,
        )
    }

    private fun ShallowDirectoryMap.readHashes(
        parent: String,
        fileName: String,
    ): Map<Long, String> {
        val file = find(parent, fileName) ?: return emptyMap()
        return HashLoader.read(file)
    }

    private fun List<ShallowDirectoryMap>.readHashes(
        parent: String,
        fileName: String = DEFAULT_FILE_NAMES,
    ): Map<Long, String> {
        val merged = mutableMapOf<Long, String>()
        for (entry in this) {
            merged += entry.readHashes(parent, fileName)
        }
        return merged
    }

    private companion object {
        private const val DEFAULT_FILE_NAMES = "hashes"
    }
}

private fun shallowSymbolDirectories(): List<ShallowDirectoryMap> {
    val root = DirectoryConstants.SYMBOL_PATH
    val local = root.resolve(".local")
    return listOf(root.shallowDirectoryMap(), local.shallowDirectoryMap())
}

@OptIn(ExperimentalPathApi::class)
private fun Path.shallowDirectoryMap(): ShallowDirectoryMap {
    val map =
        walk(PathWalkOption.INCLUDE_DIRECTORIES)
            .filter { it != this && it.isDirectory() }
            .associate { it.name to it.listFiles() }
    return ShallowDirectoryMap(map)
}

private fun Path.listFiles(): List<Path> = Files.list(this).filter(Files::isRegularFile).toList()

private data class ShallowDirectoryMap(private val directories: Map<String, List<Path>>) :
    Map<String, List<Path>> by directories {
    fun find(parent: String, fileName: String): Path? =
        this[parent]?.firstOrNull { fileName == it.nameWithoutExtension }
}
