package org.rsmod.plugins.testing.simple

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.util.concurrent.locks.ReentrantLock

@Suppress("DuplicatedCode")
public class SimpleGameTestExtension : BeforeAllCallback, ParameterResolver {

    private val lock = ReentrantLock()
    private val namespace = ExtensionContext.Namespace.create("simple-game-tests")

    private lateinit var sharedState: SimpleGameTestState

    override fun beforeAll(context: ExtensionContext) {
        lock.lock()
        try {
            val store = context.root.getStore(namespace)
            val initializeSharedState = store.get(SimpleGameTestState::class) == null
            if (initializeSharedState) {
                val state = SimpleGameTestState()
                store.put(SimpleGameTestState::class, state)
                setSharedState(state, context.root.getStore(ExtensionContext.Namespace.GLOBAL))
            }
        } finally {
            lock.unlock()
        }
    }

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean = parameterContext.parameter.type == SimpleGameTestState::class.java

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any = extensionContext.root.getStore(namespace).get(SimpleGameTestState::class)

    private fun setSharedState(state: SimpleGameTestState, store: ExtensionContext.Store) {
        check(!::sharedState.isInitialized) { "Shared state has already been set." }
        sharedState = state
        store.put(namespace.toString(), this)
    }
}
