package org.rsmod.server.shared.module

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provider
import com.google.inject.Provides
import com.google.inject.Scopes
import jakarta.inject.Inject
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import org.openrs2.cache.Cache
import org.openrs2.cache.Store
import org.rsmod.annotations.CachePath
import org.rsmod.annotations.GameCache
import org.rsmod.annotations.Js5Cache
import org.rsmod.annotations.VanillaCache
import org.rsmod.annotations.XteaFilePath
import org.rsmod.api.parsers.json.Json
import org.rsmod.game.map.XteaMap
import org.rsmod.map.square.MapSquareKey
import org.rsmod.module.ExtendedModule
import org.rsmod.server.shared.DirectoryConstants

object CacheStoreModule : ExtendedModule() {
    override fun bind() {
        bindProvider(XteaMapProvider::class.java)

        bind(Path::class.java)
            .annotatedWith(CachePath::class.java)
            .toInstance(DirectoryConstants.CACHE_PATH)

        bind(Store::class.java)
            .annotatedWith(GameCache::class.java)
            .toProvider(GameStoreProvider::class.java)
            .`in`(Scopes.SINGLETON)
        bind(Cache::class.java)
            .annotatedWith(GameCache::class.java)
            .toProvider(GameCacheProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Store::class.java)
            .annotatedWith(Js5Cache::class.java)
            .toProvider(Js5StoreProvider::class.java)
            .`in`(Scopes.SINGLETON)
        bind(Cache::class.java)
            .annotatedWith(Js5Cache::class.java)
            .toProvider(Js5CacheProvider::class.java)
            .`in`(Scopes.SINGLETON)

        bind(Store::class.java)
            .annotatedWith(VanillaCache::class.java)
            .toProvider(VanillaStoreProvider::class.java)
            .`in`(Scopes.SINGLETON)
        bind(Cache::class.java)
            .annotatedWith(VanillaCache::class.java)
            .toProvider(VanillaCacheProvider::class.java)
            .`in`(Scopes.SINGLETON)
    }

    @Provides
    @XteaFilePath
    fun xteaFilePath(@CachePath parentDir: Path): Path = parentDir.resolve("xteas.json")

    @Provides
    @GameCache
    fun gameCachePath(@CachePath parentDir: Path): Path = parentDir.resolve("game")

    @Provides
    @Js5Cache
    fun js5CachePath(@CachePath parentDir: Path): Path = parentDir.resolve("js5")

    @Provides
    @VanillaCache
    fun vanillaCachePath(@CachePath parentDir: Path): Path = parentDir.resolve("vanilla")
}

private class GameStoreProvider
@Inject
constructor(
    @GameCache private val gameDir: Path,
    @VanillaCache private val vanillaDir: Path,
    @XteaFilePath private val xteaDir: Path,
) : StoreProvider(gameDir, vanillaDir, xteaDir)

private class Js5StoreProvider
@Inject
constructor(
    @Js5Cache private val js5Dir: Path,
    @VanillaCache private val vanillaDir: Path,
    @XteaFilePath private val xteaDir: Path,
) : StoreProvider(js5Dir, vanillaDir, xteaDir)

private class VanillaStoreProvider
@Inject
constructor(@VanillaCache private val dir: Path, @XteaFilePath private val xteaPath: Path) :
    Provider<Store> {
    private val fullInstructions =
        "\n\n\t\tPlace cache files in ${dir.toAbsolutePath()}" +
            "\n\t\t\tand `xteas.json` in ${xteaPath.toAbsolutePath()}\n"

    private val cacheInstructions = "\n\n\t\tPlace cache files in ${dir.toAbsolutePath()}\n"

    private val xteaInstructions = "\n\n\t\tPlace `xteas.json` in ${xteaPath.toAbsolutePath()}\n"

    override fun get(): Store =
        when {
            !Files.exists(dir) && !Files.exists(xteaPath) -> {
                throw FileNotFoundException("Cache not found.$fullInstructions")
            }
            !Files.exists(dir) -> {
                throw FileNotFoundException("Cache not found.$cacheInstructions")
            }
            !Files.exists(xteaPath) -> {
                throw FileNotFoundException("XTEAs not found.$xteaInstructions")
            }
            else -> Store.open(dir)
        }
}

private open class StoreProvider(
    private val dir: Path,
    private val copyFrom: Path,
    private val xteaPath: Path,
) : Provider<Store> {
    private val fullInstructions =
        "\n\n\t\tPlace cache files in ${copyFrom.toAbsolutePath()}" +
            "\n\t\t\tand `xteas.json` in ${xteaPath.toAbsolutePath()}\n"

    private val cacheInstructions = "\n\n\t\tPlace cache files in ${copyFrom.toAbsolutePath()}\n"

    private val xteaInstructions = "\n\n\t\tPlace `xteas.json` in ${xteaPath.toAbsolutePath()}\n"

    override fun get(): Store {
        validateVanillaFiles()
        if (!dir.exists()) {
            createDirectory()
        } else if (dir.listDirectoryEntries().isEmpty()) {
            copyFiles()
        }
        return Store.open(dir)
    }

    private fun createDirectory() {
        dir.createDirectories()
        copyFiles()
    }

    @OptIn(ExperimentalPathApi::class)
    private fun copyFiles() {
        copyFrom.copyToRecursively(dir, overwrite = false, followLinks = false)
    }

    private fun validateVanillaFiles() {
        when {
            !Files.exists(copyFrom) && !Files.exists(xteaPath) -> {
                throw FileNotFoundException("Cache not found.$fullInstructions")
            }
            !Files.exists(copyFrom) -> {
                throw FileNotFoundException("Cache not found.$cacheInstructions")
            }
            !Files.exists(xteaPath) -> {
                throw FileNotFoundException("XTEAs not found.$xteaInstructions")
            }
        }
    }
}

private class GameCacheProvider
@Inject
constructor(@GameCache private val store: Store, @GameCache private val dir: Path) :
    CacheProvider(store, dir)

private class Js5CacheProvider
@Inject
constructor(@Js5Cache private val store: Store, @Js5Cache private val dir: Path) :
    CacheProvider(store, dir)

private class VanillaCacheProvider
@Inject
constructor(@VanillaCache private val store: Store, @VanillaCache private val dir: Path) :
    CacheProvider(store, dir)

private open class CacheProvider(private val store: Store, private val dir: Path) :
    Provider<Cache> {
    override fun get(): Cache =
        try {
            Cache.open(store)
        } catch (_: FileNotFoundException) {
            throw FileNotFoundException("Cache not found in dir: ${dir.toAbsolutePath()}")
        } catch (t: Throwable) {
            throw t
        }
}

private class XteaMapProvider
@Inject
constructor(@CachePath private val cachePath: Path, @Json private val mapper: ObjectMapper) :
    Provider<XteaMap> {
    private val path: Path
        get() = cachePath.resolve("xteas.json")

    override fun get(): XteaMap {
        val reader = Files.newBufferedReader(path)
        val fileKeys = mapper.readValue(reader, Array<FileXtea>::class.java)
        val keys = fileKeys.associate { MapSquareKey(it.mapsquare) to it.key.toIntArray() }
        return XteaMap(HashMap(keys))
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class FileXtea(val mapsquare: Int = 0, val key: List<Int> = emptyList())
}
