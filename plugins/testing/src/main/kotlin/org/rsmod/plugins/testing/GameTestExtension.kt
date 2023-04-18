package org.rsmod.plugins.testing

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.util.concurrent.locks.ReentrantLock

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
        extensionContext: ExtensionContext
    ): Boolean = parameterContext.parameter.type == GameTestState::class.java

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any = extensionContext.root.getStore(namespace).get(GameTestState::class)

    override fun close() {
        if (::sharedState.isInitialized) {
            sharedState.finalize()
        }
    }

    private fun setSharedState(state: GameTestState, store: ExtensionContext.Store) {
        if (::sharedState.isInitialized) return
        sharedState = state
        store.put(namespace.toString(), this)
    }
}
