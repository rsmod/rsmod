package org.rsmod

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Scopes
import dev.misfitlabs.kotlinguice4.getInstance
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.rsmod.game.Game
import org.rsmod.game.GameModule
import org.rsmod.game.cache.CacheModule
import org.rsmod.game.cache.GameCache
import org.rsmod.game.cache.type.CacheTypeLoaderList
import org.rsmod.game.config.ConfigModule
import org.rsmod.game.config.GameConfig
import org.rsmod.game.coroutine.CoroutineModule
import org.rsmod.game.coroutine.IoCoroutineScope
import org.rsmod.game.dispatch.DispatcherModule
import org.rsmod.game.event.EventBus
import org.rsmod.game.event.impl.ServerStartup
import org.rsmod.game.name.NamedTypeLoaderList
import org.rsmod.game.net.NetworkModule
import org.rsmod.game.net.channel.ClientChannelInitializer
import org.rsmod.game.net.handshake.HandshakeDecoder
import org.rsmod.game.plugin.kotlin.KotlinModuleLoader
import org.rsmod.game.plugin.kotlin.KotlinPluginLoader
import org.rsmod.game.task.StartupTaskList
import org.rsmod.game.task.launchBlocking
import org.rsmod.game.task.launchNonBlocking
import org.rsmod.util.mapper.ObjectMapperModule
import java.net.InetSocketAddress
import java.nio.file.Path

private val logger = InlineLogger()

class Server {

    fun startup() = runBlocking {
        logger.info { "Starting up server - please wait..." }

        val scope = Scopes.SINGLETON

        val moduleLoader = KotlinModuleLoader(scope)
        val moduleScripts = moduleLoader.load()
        val modules = moduleScripts.flatMap { it.modules }

        val injector = Guice.createInjector(
            ServerModule(scope),
            ObjectMapperModule(scope),
            CoroutineModule(scope),
            DispatcherModule(scope),
            ConfigModule(scope),
            CacheModule(scope),
            GameModule(scope),
            NetworkModule(scope),
            *modules.toTypedArray()
        )
        val gameConfig: GameConfig = injector.getInstance()
        logger.info { "Launching ${gameConfig.name}" }

        val cache: GameCache = injector.getInstance()
        cache.start()

        val ioCoroutineScope: IoCoroutineScope = injector.getInstance()

        val typeLoaders: CacheTypeLoaderList = injector.getInstance()
        loadCacheTypes(ioCoroutineScope, typeLoaders)

        val typeNames: NamedTypeLoaderList = injector.getInstance()
        val typeNamePath = cache.directory.parent.resolve("name")
        loadTypeNames(typeNamePath, ioCoroutineScope, typeNames)

        val pluginLoader: KotlinPluginLoader = injector.getInstance()
        val plugins = pluginLoader.load()

        val startupTasks: StartupTaskList = injector.getInstance()
        startupTasks.launchNonBlocking(ioCoroutineScope)
        startupTasks.launchBlocking()

        val game: Game = injector.getInstance()
        game.start()

        bind(injector)

        logger.info { "Loaded ${plugins.size} plugin(s)" }
        logger.debug { "Loaded game with configuration: $gameConfig" }
        logger.info { "Game listening to connections on port ${gameConfig.port}" }

        val eventBus: EventBus = injector.getInstance()
        eventBus.publish(ServerStartup())
    }

    private fun bind(injector: Injector) {
        val channelInitializer = ClientChannelInitializer(
            handshakeDecoder = { injector.getInstance<HandshakeDecoder>() }
        )

        val bootstrap = ServerBootstrap()
        bootstrap.channel(NioServerSocketChannel::class.java)
        bootstrap.childHandler(channelInitializer)
        bootstrap.group(NioEventLoopGroup(2), NioEventLoopGroup(1))

        val config: GameConfig = injector.getInstance()
        val bind = bootstrap.bind(InetSocketAddress(config.port)).awaitUninterruptibly()
        if (!bind.isSuccess) {
            error("Could not bind game port.")
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val server = Server()
            server.startup()
        }
    }
}

private fun loadCacheTypes(
    ioCoroutineScope: IoCoroutineScope,
    loaders: CacheTypeLoaderList
) = runBlocking {
    val jobs = mutableListOf<Deferred<Unit>>()
    loaders.forEach { loader ->
        val job = ioCoroutineScope.async { loader.load() }
        jobs.add(job)
    }
    jobs.forEach { it.await() }
}

private fun loadTypeNames(
    directory: Path,
    ioCoroutineScope: IoCoroutineScope,
    loaders: NamedTypeLoaderList
) = runBlocking {
    val jobs = mutableListOf<Deferred<Unit>>()
    loaders.forEach { loader ->
        val job = ioCoroutineScope.async { loader.load(directory) }
        jobs.add(job)
    }
    jobs.forEach { it.await() }
}
