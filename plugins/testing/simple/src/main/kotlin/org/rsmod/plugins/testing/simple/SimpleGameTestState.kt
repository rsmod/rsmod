package org.rsmod.plugins.testing.simple

public class SimpleGameTestState {

    public fun runGameTest(
        scope: SimpleGameTestScope = SimpleGameTestScope(),
        testBody: SimpleGameTestScope.() -> Unit
    ): Unit = testBody(scope)
}
