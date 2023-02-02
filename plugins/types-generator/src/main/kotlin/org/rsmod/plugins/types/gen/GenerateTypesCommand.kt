package org.rsmod.plugins.types.gen

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ajalt.clikt.core.CliktCommand
import com.google.inject.Guice
import com.google.inject.Key
import org.rsmod.game.config.DataPath
import org.rsmod.game.types.NamedTypeGenerator
import org.rsmod.toml.Toml
import java.nio.file.Path

public fun main(args: Array<String>): Unit = GenerateTypesCommand().main(args)

public class GenerateTypesCommand : CliktCommand("generate-types") {

    override fun run() {
        val injector = Guice.createInjector(CacheTypeGeneratorModule)
        val generator = injector.getInstance(NamedTypeGenerator::class.java)
        val pluginConstLoader = injector.getInstance(PluginConstTypeNameLoader::class.java)
        val cacheLoader = injector.getInstance(CacheTypeNameLoader::class.java)
        val mapper = injector.getInstance(Key.get(ObjectMapper::class.java, Toml::class.java))
        val dataPath = injector.getInstance(Key.get(Path::class.java, DataPath::class.java))
        val cacheNames = cacheLoader.load()
        val pluginNames = pluginConstLoader.load(OUTPUT_PACKAGE)
        val configMapOutput = dataPath.resolve("names")

        generator.writeConstFiles(
            names = cacheNames + pluginNames,
            outputPath = CONST_FILES_OUTPUT_PATH,
            packageName = OUTPUT_PACKAGE
        )

        generator.writeConfigMapFiles(
            names = cacheNames,
            outputPath = configMapOutput.resolve("cache"),
            mapper = mapper
        )

        generator.writeConfigMapFiles(
            names = pluginNames,
            outputPath = configMapOutput.resolve("plugins"),
            mapper = mapper
        )
    }

    public companion object {

        public const val OUTPUT_PACKAGE: String = "org.rsmod.types"

        public val CONST_FILES_OUTPUT_PATH: Path = Path.of(
            "plugins/types-generated/src/main/gen/${OUTPUT_PACKAGE.replace(".", "/")}"
        )
    }
}
