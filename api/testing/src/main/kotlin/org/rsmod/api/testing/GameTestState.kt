package org.rsmod.api.testing

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Injector
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass
import kotlin.time.measureTime
import org.junit.jupiter.api.extension.ExtensionContext
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastFactory
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.api.testing.scope.AdvancedGameTestScope
import org.rsmod.api.testing.scope.AdvancedReadOnly
import org.rsmod.api.testing.scope.BasicGameTestScope
import org.rsmod.api.testing.scope.GameTestScope
import org.rsmod.events.EventBus
import org.rsmod.game.map.XteaMap
import org.rsmod.game.type.TypeListMap
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.server.app.GameServer

public class GameTestState {
    public val collision: CollisionFlagMap by lazy { injected.collision }
    public val routeFactory: RouteFactory by lazy { injected.routeFactory }
    public val rayCastFactory: RayCastFactory by lazy { injected.rayCastFactory }
    public val stepFactory: StepFactory by lazy { injected.stepFactory }
    public val rayCastValidator: RayCastValidator by lazy { injected.rayCastValidator }
    public val boundValidator: BoundValidator by lazy { injected.boundValidator }
    public val xteaRepository: XteaMap by lazy { injected.xteaRepository }
    public val cacheTypes: TypeListMap by lazy { injected.cacheTypes }

    /* The following declaration should be treated as read-only by tests. */
    public val eventBus: EventBus by lazy { injected.eventBus }

    private lateinit var injected: GameTestInjected
    private lateinit var readOnly: AdvancedReadOnly

    private val logger = InlineLogger()

    /**
     * @param scripts Associated [PluginScript] classes relevant to the newly created [scope]. If
     *   one or more scripts are provided, the test is run given isolated [ScriptContext]s that are
     *   only available to the specified scripts. Otherwise, the default [ScriptContext] is used,
     *   which includes a globally shared [EventBus] and events registered by all available
     *   [PluginScript]s.
     *
     *   This is useful when you want to ignore scripts that are irrelevant to the current test case
     *   but might cause cross-contamination or unexpected failures. For example, when presetting a
     *   [GameTestScope.random] sequence for a woodcutting test, an unrelated plugin such as a "get
     *   a random reward after 30 seconds of playtime" script could consume and reset the random
     *   value before the woodcutting script executes. In such cases, you can specify the relevant
     *   script like so: `runGameTest(WoodcuttingScript::class) { ... }`
     *
     * @see [GameTestScope]
     */
    public fun runGameTest(
        vararg scripts: KClass<out PluginScript>,
        scope: GameTestScope = GameTestScope.Builder(this, scripts.toSet()).build(),
        testBody: GameTestScope.() -> Unit,
    ): Unit = testBody(scope)

    /**
     * Runs a loosely-coupled test using the [BasicGameTestScope], which provides basic properties
     * and explicit control over the systems being tested. This is useful for test cases that
     * require minimal dependencies or where the focus is on isolated components rather than the
     * full game system.
     *
     * For most scenarios, prefer using [runGameTest], which offers a more integrated scope with all
     * game systems operating as they would in a live environment.
     */
    public fun runBasicGameTest(
        scope: BasicGameTestScope = BasicGameTestScope(eventBus),
        testBody: BasicGameTestScope.() -> Unit,
    ): Unit = testBody(scope)

    /**
     * Executes a game test with higher-level privileged access.
     *
     * This function enables running integration tests with both standard and advanced testing
     * scopes. The [AdvancedGameTestScope] grants additional capabilities and resources, offering
     * greater flexibility and control over the testing environment. However, using this scope
     * requires careful handling to maintain test integrity.
     *
     * Within the [testBody], the [AdvancedGameTestScope] is available as a receiver and can be
     * accessed using the default `it` keyword or an explicit parameter name.
     *
     * **Note**: **By default**, use the standard [runBasicGameTest] function. The
     * [runAdvancedGameTest] function should only be used when absolutely necessary and no other
     * workaround is possible.
     *
     * @see [AdvancedGameTestScope]
     * @see [runBasicGameTest]
     */
    public fun runAdvancedGameTest(
        standardScope: BasicGameTestScope = BasicGameTestScope(eventBus),
        advancedScope: AdvancedGameTestScope = AdvancedGameTestScope(readOnly),
        testBody: BasicGameTestScope.(AdvancedGameTestScope) -> Unit,
    ): Unit = testBody(standardScope, advancedScope)

    internal fun initialize() {
        logger.info { "Setting up game-test state..." }
        val duration = measureTime {
            val server = GameServer()
            val injector = initializeGameInjector(server)
            injected = injector.getInstance(GameTestInjected::class.java)
            readOnly = injector.getInstance(AdvancedReadOnly::class.java)
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

    private fun initializeGameInjector(server: GameServer): Injector {
        val injector = server.createInjector()
        server.prepareGame(injector)
        return injector
    }
}
