package org.runerealm.server.install

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
import org.runerealm.annotations.EnrichedCache
import org.runerealm.annotations.GameCache
import org.runerealm.api.cache.CacheModule
import org.runerealm.api.cache.enricher.CacheEnricherModule
import org.runerealm.api.cache.enricher.CacheEnrichmentConfigs
import org.runerealm.api.cache.enricher.CacheEnrichmentMaps
import org.runerealm.api.core.CoreModule
import org.runerealm.api.parsers.jackson.JacksonModule
import org.runerealm.api.parsers.json.JsonModule
import org.runerealm.api.parsers.toml.TomlModule
import org.runerealm.api.type.builders.resource.TypeResourcePack
import org.runerealm.api.type.resolver.TypeResolver
import org.runerealm.api.type.updater.TypeUpdaterCacheSync
import org.runerealm.api.type.updater.TypeUpdaterConfigs
import org.runerealm.api.type.updater.TypeUpdaterResources
import org.runerealm.api.type.verifier.TypeVerifier
import org.runerealm.api.type.verifier.isCacheUpdateRequired
import org.runerealm.api.type.verifier.isFailure
import org.runerealm.server.shared.DirectoryConstants
import org.runerealm.server.shared.loader.ResourceTypeBuilderLoader
import org.runerealm.server.shared.loader.TypeBuilderLoader
import org.runerealm.server.shared.loader.TypeEditorLoader
import org.runerealm.server.shared.loader.TypeReferencesLoader
import org.runerealm.server.shared.module.CacheStoreModule
import org.runerealm.server.shared.module.EventModule
import org.runerealm.server.shared.module.ScannerModule
import org.runerealm.server.shared.module.SymbolModule
import org.runerealm.server.shared.util.ResourceBuilderListLoader

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
        resources.updateAll(createTypeResourcePack(injector))
    }

    private fun createTypeResourcePack(injector: Injector): TypeResourcePack {
        val loader = injector.getInstance(ResourceTypeBuilderLoader::class.java)
        return ResourceBuilderListLoader.load(loader)
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
