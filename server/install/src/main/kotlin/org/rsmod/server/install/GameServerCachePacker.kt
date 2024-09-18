package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.google.inject.Guice
import com.google.inject.Injector
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import org.rsmod.api.cache.CacheModule
import org.rsmod.api.core.CoreModule
import org.rsmod.api.parsers.jackson.JacksonModule
import org.rsmod.api.parsers.json.JsonModule
import org.rsmod.api.parsers.toml.TomlModule
import org.rsmod.api.type.resolver.TypeResolver
import org.rsmod.api.type.updater.TypeUpdater
import org.rsmod.api.type.verifier.BuilderVerifier
import org.rsmod.api.type.verifier.EditorVerifier
import org.rsmod.api.type.verifier.ReferenceVerifier
import org.rsmod.api.type.verifier.isFailure
import org.rsmod.server.shared.DirectoryConstants
import org.rsmod.server.shared.loader.TypeBuilderLoader
import org.rsmod.server.shared.loader.TypeEditorLoader
import org.rsmod.server.shared.loader.TypeReferencesLoader
import org.rsmod.server.shared.module.CacheStoreModule
import org.rsmod.server.shared.module.EventModule
import org.rsmod.server.shared.module.ScannerModule
import org.rsmod.server.shared.module.SymbolModule

fun main(args: Array<String>): Unit = GameServerCachePacker().main(args)

@OptIn(ExperimentalPathApi::class)
class GameServerCachePacker : CliktCommand(name = "cache-pack") {
    private val gameCacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("game")

    private val js5CacheDir: Path
        get() = DirectoryConstants.CACHE_PATH.resolve("js5")

    override fun run() {
        val injector =
            Guice.createInjector(
                CacheStoreModule,
                SymbolModule,
                CoreModule,
                CacheModule,
                EventModule,
                JacksonModule,
                JsonModule,
                ScannerModule,
                TomlModule,
            )
        deleteOldCacheDirectories()
        resolveAllTypes(injector)
        updateCacheTypes(injector)
    }

    private fun deleteOldCacheDirectories() {
        gameCacheDir.deleteRecursively()
        js5CacheDir.deleteRecursively()
    }

    private fun resolveAllTypes(injector: Injector) {
        val resolver = injector.getInstance(TypeResolver::class.java)

        val references = injector.getInstance(TypeReferencesLoader::class.java)
        val referenceVerifier = injector.getInstance(ReferenceVerifier::class.java)
        resolver.appendReferences(references.load())
        resolver.resolveReferences()

        val referenceVerification = referenceVerifier.verifyErrors()
        if (referenceVerification.isFailure() == true) {
            throw RuntimeException(referenceVerification.formatError())
        }

        val builders = injector.getInstance(TypeBuilderLoader::class.java)
        val builderVerifier = injector.getInstance(BuilderVerifier::class.java)
        resolver.appendBuilders(builders.load())
        resolver.resolveBuilders()

        val builderVerification = builderVerifier.verifyErrors()
        if (builderVerification.isFailure()) {
            throw RuntimeException(builderVerification.formatError())
        }

        val editors = injector.getInstance(TypeEditorLoader::class.java)
        val editorVerifier = injector.getInstance(EditorVerifier::class.java)
        resolver.appendEditors(editors.load())
        resolver.resolveEditors()

        val editorVerification = editorVerifier.verifyErrors()
        if (editorVerification.isFailure()) {
            throw RuntimeException(editorVerification.formatError())
        }
    }

    private fun updateCacheTypes(injector: Injector) {
        val updater = injector.getInstance(TypeUpdater::class.java)
        updater.updateAll()
    }
}
