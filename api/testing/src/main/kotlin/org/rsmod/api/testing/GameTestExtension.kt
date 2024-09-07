package org.rsmod.api.testing

import java.util.concurrent.locks.ReentrantLock
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * A JUnit extension for managing the lifecycle of a shared test state across test classes.
 * > **Important Note:** Gradle and JUnit handle test class loading in a specific manner. This means
 * > that the shared state managed by this extension is scoped to individual modules. Each module
 * > will have its own instance of the shared state, used for all tests within that module. Each
 * > _**test state**_ loads its own resources, such as plugins or the game cache. It is recommended
 * > to run the integration test task for the specific module you'd like to test as opposed to the
 * > entire root project.
 *
 * This extension provides the following functionalities:
 * - [BeforeAllCallback]: Initializes and sets up the shared game test state before any tests are
 *   run.
 * - [AfterAllCallback]: Cleans up and unregisters the shared game test state after all tests have
 *   completed.
 * - [ParameterResolver]: Resolves instances of [GameTestState] for test methods that require it.
 * - [ExtensionContext.Store.CloseableResource]: Ensures proper cleanup of the shared state.
 *
 * @see [GameTestState]
 */
public class GameTestExtension :
    BeforeAllCallback,
    AfterAllCallback,
    ParameterResolver,
    ExtensionContext.Store.CloseableResource {
    private val lock = ReentrantLock()
    private val namespace = ExtensionContext.Namespace.create("game-tests")

    private lateinit var sharedState: GameTestState

    override fun beforeAll(context: ExtensionContext) {
        lock.lock()
        try {
            val store = context.root.getStore(namespace)
            val initializeSharedState = store.get(GameTestState::class) == null
            if (initializeSharedState) {
                val state = GameTestState().apply { initialize() }
                store.put(GameTestState::class, state)
                setSharedState(state, context.root.getStore(ExtensionContext.Namespace.GLOBAL))
            }
            val state = store.get(GameTestState::class) as GameTestState
            state.register(context)
        } finally {
            lock.unlock()
        }
    }

    override fun afterAll(context: ExtensionContext) {
        lock.lock()
        try {
            val store = context.root.getStore(namespace)
            val state = store.get(GameTestState::class) as GameTestState
            state.unregister(context)
        } finally {
            lock.unlock()
        }
    }

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
    ): Boolean = parameterContext.parameter.type == GameTestState::class.java

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
    ): Any = extensionContext.root.getStore(namespace).get(GameTestState::class)

    override fun close() {
        if (::sharedState.isInitialized) {
            sharedState.finalize()
        }
    }

    private fun setSharedState(state: GameTestState, store: ExtensionContext.Store) {
        check(!::sharedState.isInitialized) { "Shared state has already been set." }
        sharedState = state
        store.put(namespace.toString(), this)
    }
}
