package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteRecursively
import org.openrs2.cache.Cache
import org.rsmod.annotations.EnrichedCache
import org.rsmod.annotations.GameCache
import org.rsmod.api.cache.CacheModule
import org.rsmod.api.cache.enricher.CacheEnricherModule
import org.rsmod.api.cache.enricher.CacheEnrichmentConfigs
import org.rsmod.api.cache.enricher.CacheEnrichmentMaps
import org.rsmod.api.core.CoreModule
import org.rsmod.api.parsers.jackson.JacksonModule
import org.rsmod.api.parsers.json.JsonModule
import org.rsmod.api.parsers.toml.TomlModule
import org.rsmod.api.type.builders.map.MapBuilderList
import org.rsmod.api.type.resolver.TypeResolver
import org.rsmod.api.type.updater.TypeUpdaterCacheSync
import org.rsmod.api.type.updater.TypeUpdaterConfigs
import org.rsmod.api.type.updater.TypeUpdaterResources
import org.rsmod.api.type.verifier.TypeVerifier
import org.rsmod.api.type.verifier.isCacheUpdateRequired
import org.rsmod.api.type.verifier.isFailure
import org.rsmod.server.shared.DirectoryConstants
import org.rsmod.server.shared.loader.MapTypeBuilderLoader
import org.rsmod.server.shared.loader.TypeBuilderLoader
import org.rsmod.server.shared.loader.TypeEditorLoader
import org.rsmod.server.shared.loader.TypeReferencesLoader
import org.rsmod.server.shared.module.CacheStoreModule
import org.rsmod.server.shared.module.EventModule
import org.rsmod.server.shared.module.ScannerModule
import org.rsmod.server.shared.module.SymbolModule
import org.rsmod.server.shared.util.MapBuilderListLoader

fun main(args: Array<String>): Unit = GameServerCachePacker().main(args)

@OptIn(ExperimentalPathApi::class)
class GameServerCachePacker : CliktCommand(name = "cache-pack") {
    private var packedCache = false

    private val enrichedCacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("enriched")

    private val gameCacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("game")

    private val js5CacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("js5")

    override fun run() {
        enrichedCacheDir.deleteRecursively()
        gameCacheDir.deleteRecursively()
        js5CacheDir.deleteRecursively()
        packEnrichedTypes()
    }

    private fun packEnrichedTypes() {
        val injector =
            Guice.createInjector(
                CacheEnricherModule,
                CacheModule,
                CacheStoreModule,
                CoreModule,
                EventModule,
                JacksonModule,
                JsonModule,
                ScannerModule,
                SymbolModule,
                TomlModule,
            )
        val resolved = resolveAllTypes(injector)
        if (resolved) {
            enrichGameCache(injector)
            copyGameCache(injector)
        }
    }

    private fun resolveAllTypes(injector: Injector): Boolean {
        val resolver = injector.getInstance(TypeResolver::class.java)

        val references = injector.getInstance(TypeReferencesLoader::class.java)
        resolver.appendReferences(references.load())
        resolver.resolveReferences()

        val builders = injector.getInstance(TypeBuilderLoader::class.java)
        resolver.appendBuilders(builders.load())
        resolver.resolveBuilders()

        val editors = injector.getInstance(TypeEditorLoader::class.java)
        resolver.appendEditors(editors.load())
        resolver.resolveEditors()

        val verifier = injector.getInstance(TypeVerifier::class.java)
        val verification = verifier.verifyAll(verifyIdentityHashes = false)
        if (verification.isCacheUpdateRequired()) {
            if (packedCache) {
                throw RuntimeException(verification.formatError())
            }
            updateCaches(injector)
            closeCaches(injector)
            packedCache = true
            packEnrichedTypes()
            return false
        } else if (verification.isFailure()) {
            throw RuntimeException(verification.formatError())
        }
        return true
    }

    private fun updateCaches(injector: Injector) {
        val sync = injector.getInstance(TypeUpdaterCacheSync::class.java)
        sync.syncFromBaseCaches()

        val configs = injector.getInstance(TypeUpdaterConfigs::class.java)
        configs.updateAll()

        val resources = injector.getInstance(TypeUpdaterResources::class.java)
        resources.updateMaps(createMapBuilderList(injector))
    }

    private fun createMapBuilderList(injector: Injector): MapBuilderList {
        val loader = injector.getInstance(MapTypeBuilderLoader::class.java)
        return MapBuilderListLoader.load(loader)
    }

    private fun enrichGameCache(injector: Injector) {
        val configs = injector.getInstance(CacheEnrichmentConfigs::class.java)
        val maps = injector.getInstance(CacheEnrichmentMaps::class.java)
        val dest = injector.getInstance(Key.get(Cache::class.java, GameCache::class.java))
        dest.use {
            configs.encodeAll(it)
            maps.encodeAll(it)
        }
    }

    private fun copyGameCache(injector: Injector) {
        val source = injector.getInstance(Key.get(Path::class.java, GameCache::class.java))
        val dest = injector.getInstance(Key.get(Path::class.java, EnrichedCache::class.java))
        dest.deleteRecursively()
        dest.createParentDirectories()
        source.copyToRecursively(dest, followLinks = false)
    }

    private fun closeCaches(injector: Injector) {
        val game = injector.getInstance(Key.get(Cache::class.java, GameCache::class.java))
        game.close()

        val enriched = injector.getInstance(Key.get(Cache::class.java, EnrichedCache::class.java))
        enriched.close()
    }
}
