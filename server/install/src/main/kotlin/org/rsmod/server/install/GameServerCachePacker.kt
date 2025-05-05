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
import org.rsmod.api.cache.enricher.CacheEnrichment
import org.rsmod.api.core.CoreModule
import org.rsmod.api.parsers.jackson.JacksonModule
import org.rsmod.api.parsers.json.JsonModule
import org.rsmod.api.parsers.toml.TomlModule
import org.rsmod.api.type.builders.map.MapUpdateList
import org.rsmod.api.type.resolver.TypeResolver
import org.rsmod.api.type.updater.TypeUpdater
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
import org.rsmod.server.shared.util.MapUpdateListLoader

fun main(args: Array<String>): Unit = GameServerCachePacker().main(args)

@OptIn(ExperimentalPathApi::class)
class GameServerCachePacker : CliktCommand(name = "cache-pack") {
    private val gameCacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("game")

    private val js5CacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("js5")

    override fun run() {
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
            val enricher = injector.getInstance(CacheEnrichment::class.java)
            val target = injector.getInstance(Key.get(Cache::class.java, GameCache::class.java))
            enricher.encodeAll(target)
            copyEnrichedCache(injector)
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
            val updater = injector.getInstance(TypeUpdater::class.java)
            val mapUpdates = createMapUpdateList(injector)
            updater.updateAll(mapUpdates)
            closeCaches(injector)
            packEnrichedTypes()
            return false
        } else if (verification.isFailure()) {
            throw RuntimeException(verification.formatError())
        }
        return true
    }

    private fun createMapUpdateList(injector: Injector): MapUpdateList {
        val loader = injector.getInstance(MapTypeBuilderLoader::class.java)
        return MapUpdateListLoader.load(loader)
    }

    private fun copyEnrichedCache(injector: Injector) {
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
