package org.rsmod.api.testing

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.AbstractModule
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
import org.rsmod.game.map.xtea.XteaMap
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
     * Runs a game test with optional isolated script contexts.
     *
     * If one or more [scripts] are provided, the test runs with isolated [ScriptContext]s that bind
     * events only for the specified scripts. If no scripts are provided, the test runs **without
     * any plugin-specific events**, ensuring a clean execution environment.
     *
     * ### Why This Matters
     * Isolating scripts prevents unintended interactions from unrelated scripts. For example,
     * suppose you're testing a woodcutting script that relies on a preset [GameTestScope.random]
     * sequence. Without isolation, an unrelated script, such as one granting a random reward after
     * 30 seconds, might consume the random value before the woodcutting script executes.
     *
     * To prevent such interference, we explicitly specify the relevant script:
     * ```
     * runGameTest(WoodcuttingScript::class) { ... }
     * ```
     *
     * @param scripts The [PluginScript] classes relevant to the test scope. If specified, only the
     *   events for these scripts will be loaded. Otherwise, no plugin events are registered.
     * @see [GameTestScope]
     */
    public fun runGameTest(
        vararg scripts: KClass<out PluginScript>,
        scope: GameTestScope = GameTestScope.Builder(this, scripts.toSet()).build(),
        testBody: GameTestScope.() -> Unit,
    ): Unit = testBody(scope)

    /**
     * Runs a game test with optional isolated script contexts and an injected dependency.
     *
     * This function allows for injecting a **single test-specific dependency** via an optional
     * child module, while also providing script isolation similar to the simpler [runGameTest]. The
     * injected dependency acts as a **wrapper** around one or more required dependencies, avoiding
     * the need for multiple dependency parameters.
     *
     * ### Why This Matters
     * Some tests require specialized dependencies that do not belong in [GameTestScope]. Instead of
     * modifying `GameTestScope` for every unique test case, this function lets you supply a custom
     * dependency through an optional **child module** and a **dependency wrapper class**. If no
     * child module is provided, the parent's injector is used.
     *
     * **Example Usage:**
     *
     * ```
     * object MeleeAccuracyTestModule : AbstractModule() {
     *   override fun configure() {
     *      bind(PvnMeleeAccuracy::class.java).`in`(Scopes.SINGLETON)
     *   }
     * }
     * ```
     * ```
     * class MeleeAccuracyTestDependencies @Inject constructor(val accuracy: PvNMeleeAccuracy)
     * ```
     * ```
     * runInjectedGameTest(
     *  // Wrapper to be injected that contains sub-dependencies.
     *  MeleeAccuracyTestDependencies::class,
     *  // Optional child module for additional test-specific bindings. (Required in this example)
     *  childModule = MeleeAccuracyTestModule,
     * ) { deps -> // `deps` = the injected `MeleeAccuracyTestDependencies`.
     *  val accuracy = deps.accuracy
     *  val npc = npcFactory.create(...)
     *  val hitChance = accuracy.getHitChance(player, npc, ...)
     *  assertEquals(5000, hitChance)
     * }
     * ```
     *
     * **Why Use a "Dependency Wrapper" Class?**
     * - Allowing multiple direct dependency parameters would necessitate a second vararg, making
     *   the API messy and error-prone.
     * - A wrapper class keeps the test API clean while still providing the flexibility needed for
     *   injecting multiple related dependencies.
     *
     * @param dependency The class type of the **dependency wrapper** to be injected.
     * @param childModule An optional [AbstractModule] that provides additional, test-specific
     *   dependency bindings. If not provided, the parent's injector bindings will be used.
     * @param scripts The [PluginScript] classes relevant to the test scope. If specified, only the
     *   events for these scripts will be loaded; otherwise, no plugin events are registered.
     * @see [GameTestScope]
     */
    public fun <T : Any> runInjectedGameTest(
        dependency: KClass<T>,
        childModule: AbstractModule? = null,
        vararg scripts: KClass<out PluginScript>,
        testBody: GameTestScope.(dependency: T) -> Unit,
    ) {
        val injector = GameTestScope.Builder(this, scripts.toSet()).buildInjector(childModule)
        val scope = injector.getInstance(GameTestScope::class.java)
        val injectedDependency = injector.getInstance(dependency.java)
        testBody(scope, injectedDependency)
    }

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
