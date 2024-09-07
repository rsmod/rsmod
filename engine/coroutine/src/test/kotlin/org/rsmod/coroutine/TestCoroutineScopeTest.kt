package org.rsmod.coroutine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestCoroutineScopeTest {
    @Test
    fun `supervise coroutines`() {
        val scope = TestCoroutineScope()
        val coroutine1 = GameCoroutine()
        assertTrue(scope.children.isEmpty())

        // Non-suspended coroutine should not be supervised
        scope.launch(coroutine1) { /* no-op */ }
        assertTrue(scope.children.isEmpty())

        scope.launch(coroutine1) { pause(Unit::class) }
        assertEquals(1, scope.children.size)
        assertSame(scope.children[0], coroutine1)

        val coroutine2 = scope.launch { pause(Int::class) }
        assertEquals(2, scope.children.size)
        assertNotSame(scope.children[0], coroutine2)

        coroutine1.resumeWith(Unit)
        scope.advance()
        assertTrue(coroutine1.isIdle)
        assertEquals(1, scope.children.size)
        assertSame(scope.children[0], coroutine2)

        coroutine2.resumeWith(0)
        scope.advance()
        assertTrue(coroutine2.isIdle)
        assertTrue(scope.children.isEmpty())
    }

    @Test
    fun `cancel scope and children`() {
        val children = List(10) { GameCoroutine() }
        val scope = TestCoroutineScope()

        children.forEach { coroutine -> scope.launch(coroutine) { pause(Unit::class) } }
        assertTrue(children.all { coroutine -> coroutine.isSuspended })
        assertEquals(children.size, scope.children.size)

        scope.cancel()
        assertTrue(scope.children.isEmpty())
        assertTrue(children.all { it.isIdle })
    }
}
