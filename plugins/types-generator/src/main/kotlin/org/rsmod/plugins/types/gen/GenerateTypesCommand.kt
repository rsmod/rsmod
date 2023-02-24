package org.rsmod.plugins.types.gen

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ajalt.clikt.core.CliktCommand
import com.google.inject.Guice
import com.google.inject.Key
import org.rsmod.game.config.DataPath
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.types.NamedTypeGenerator
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.toml.Toml
import java.nio.file.Path

public fun main(args: Array<String>): Unit = GenerateTypesCommand().main(args)

public class GenerateTypesCommand : CliktCommand("generate-types") {

    override fun run() {
        val injector = Guice.createInjector(CacheTypeGeneratorModule)
        val generator = injector.getInstance(NamedTypeGenerator::class.java)
        val mapper = injector.getInstance(Key.get(ObjectMapper::class.java, Toml::class.java))
        val dataPath = injector.getInstance(Key.get(Path::class.java, DataPath::class.java))
        val cacheNames = injector.getInstance(Key.get(NamedTypeMapHolder::class.java, GameCache::class.java))
        generator.writeConstFiles(
            names = cacheNames,
            outputPath = CONST_FILES_OUTPUT_PATH,
            packageName = CONST_FILES_PACKAGE
        )
        generator.writeConfigMapFiles(
            names = cacheNames,
            outputPath = dataPath.resolve("names/cache"),
            mapper = mapper
        )
    }

    public companion object {

        public const val CONST_FILES_PACKAGE: String = "org.rsmod.types"

        public val CONST_FILES_OUTPUT_PATH: Path = Path.of(
            "plugins/types-generated/src/main/gen/${CONST_FILES_PACKAGE.replace(".", "/")}"
        )
    }
}
