package org.rsmod.api.testing

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Injector
import kotlin.jvm.optionals.getOrNull
import kotlin.time.measureTime
import org.junit.jupiter.api.extension.ExtensionContext
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastFactory
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.api.testing.advanced.AdvancedGameTestScope
import org.rsmod.api.testing.advanced.AdvancedReadOnly
import org.rsmod.events.EventBus
import org.rsmod.game.map.XteaMap
import org.rsmod.game.type.TypeListMap
import org.rsmod.pathfinder.collision.CollisionFlagMap
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

    public fun runGameTest(
        scope: GameTestScope = GameTestScope(eventBus),
        testBody: GameTestScope.() -> Unit,
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
     * **Note**: **By default**, use the standard [runGameTest] function. The [runAdvancedGameTest]
     * function should only be used when absolutely necessary and no other workaround is possible.
     *
     * @see [AdvancedGameTestScope]
     * @see [runGameTest]
     */
    public fun runAdvancedGameTest(
        standardScope: GameTestScope = GameTestScope(eventBus),
        advancedScope: AdvancedGameTestScope = AdvancedGameTestScope(readOnly),
        testBody: GameTestScope.(AdvancedGameTestScope) -> Unit,
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
