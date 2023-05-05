package org.rsmod.plugins.testing

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.util.Modules
import org.junit.jupiter.api.extension.ExtensionContext
import org.rsmod.game.GameModule
import org.rsmod.game.scripts.module.KotlinScriptModule
import org.rsmod.game.scripts.module.ModuleBranch
import org.rsmod.game.scripts.module.ModuleScriptLoader
import org.rsmod.game.scripts.plugin.KotlinScriptPlugin
import org.rsmod.game.scripts.plugin.ScriptPluginLoader
import org.rsmod.plugins.api.pathfinder.BoundValidator
import org.rsmod.plugins.api.pathfinder.PathValidator
import org.rsmod.plugins.api.pathfinder.RayCastFactory
import org.rsmod.plugins.api.pathfinder.RouteFactory
import org.rsmod.plugins.api.pathfinder.StepFactory
import kotlin.jvm.optionals.getOrNull
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val logger = InlineLogger()

@OptIn(ExperimentalTime::class)
public class GameTestState {

    private lateinit var injected: GameTestInjected

    public val routeFactory: RouteFactory by lazy { injected.routeFactory }
    public val rayCastFactory: RayCastFactory by lazy { injected.rayCastFactory }
    public val stepFactory: StepFactory by lazy { injected.stepFactory }
    public val pathValidator: PathValidator by lazy { injected.pathValidator }
    public val boundValidator: BoundValidator by lazy { injected.boundValidator }

    public fun runGameTest(
        scope: GameTestScope = GameTestScope(),
        testBody: GameTestScope.() -> Unit
    ): Unit = testBody(scope)

    internal fun initialize() {
        logger.info { "Setting up game-test state..." }
        val duration = measureTime {
            initializeGameInjector().let { injector ->
                injected = injector.getInstance(GameTestInjected::class.java)
            }
        }
        logger.info { "Set up game-test state in $duration." }
    }

    internal fun register(ctx: ExtensionContext) {
        logger.debug { "Register test: ${ctx.testClass.getOrNull()}" }
    }

    internal fun unregister(ctx: ExtensionContext) {
        logger.debug { "Unregister test: ${ctx.testClass.getOrNull()}" }
    }

    internal fun finalize() {
        logger.info { "Finalizing game-test state..." }
    }

    private fun initializeGameInjector(): Injector {
        val scriptModules = loadModuleScripts()
        val combinedModules = Modules.combine(GameModule, *scriptModules.toTypedArray())
        val injector = Guice.createInjector(combinedModules)
        loadPluginScripts(injector)
        return injector
    }

    private fun loadModuleScripts(): List<AbstractModule> {
        val moduleScripts = ModuleScriptLoader.load(KotlinScriptModule::class.java)
        val branchModules = moduleScripts.flatMap { it.branchModules[ModuleBranch.Prod] ?: emptyList() }
        val modules = branchModules + moduleScripts.flatMap { it.modules }
        logger.info {
            "Loaded ${modules.size} module script${if (modules.size == 1) "" else "s"}."
        }
        return modules
    }

    private fun loadPluginScripts(injector: Injector) {
        val plugins = ScriptPluginLoader.load(KotlinScriptPlugin::class.java, injector)
        logger.info { "Loaded ${plugins.size} plugin script${if (plugins.size == 1) "" else "s"}." }
    }
}
