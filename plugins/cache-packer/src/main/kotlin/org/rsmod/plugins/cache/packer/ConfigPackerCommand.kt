package org.rsmod.plugins.cache.packer

import com.github.ajalt.clikt.core.CliktCommand
import com.google.inject.Guice
import com.google.inject.Key
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.build.js5.Js5Cache

public fun main(args: Array<String>): Unit = ConfigPackerCommand().main(args)

public class ConfigPackerCommand : CliktCommand(name = "pack-configs") {

    override fun run() {
        val injector = Guice.createInjector(ConfigPackerModule)
        val packer = injector.getInstance(ConfigCachePacker::class.java)
        val gameCache = injector.getInstance(Key.get(Cache::class.java, GameCache::class.java))
        val js5Cache = injector.getInstance(Key.get(Cache::class.java, Js5Cache::class.java))
        gameCache.use { cache -> packer.pack(cache, isJs5 = false) }
        js5Cache.use { cache -> packer.pack(cache, isJs5 = true) }
    }
}
