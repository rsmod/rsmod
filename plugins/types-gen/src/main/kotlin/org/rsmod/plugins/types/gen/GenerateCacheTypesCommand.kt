package org.rsmod.plugins.types.gen

import com.github.ajalt.clikt.core.CliktCommand
import com.google.inject.Guice
import org.rsmod.game.types.NamedTypeGenerator
import org.rsmod.game.types.NamedTypeMapHolder

fun main(args: Array<String>): Unit = GenerateCacheTypesCommand().main(args)

class GenerateCacheTypesCommand : CliktCommand("generate-cache-types") {

    override fun run() {
        val injector = Guice.createInjector(CacheTypeGeneratorModule)
        val generator = injector.getInstance(NamedTypeGenerator::class.java)
        val loader = injector.getInstance(CacheTypeNameLoader::class.java)
        val names = injector.getInstance(NamedTypeMapHolder::class.java)
        loader.loadAndPutAll(names)
        generator.writeFiles()
    }
}
