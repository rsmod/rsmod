package org.rsmod.game.coroutines

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameCoroutineScopeTest {

    @Test
    fun testSupervisedCoroutines() {
        val scope = GameCoroutineScope()
        val coroutine1 = GameCoroutine()
        assertTrue(scope.children.isEmpty())

        /* non-suspended coroutine should not be supervised */
        scope.launch(coroutine1) {}
        assertTrue(scope.children.isEmpty())

        scope.launch(coroutine1) {
            pause(ticks = 1)
        }
        assertEquals(1, scope.children.size)
        assertSame(scope.children[0], coroutine1)

        val coroutine2 = scope.launch {
            pause(Int::class)
        }
        assertEquals(2, scope.children.size)
        assertNotSame(scope.children[0], coroutine2)

        coroutine1.resume()
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
    fun testCancel() {
        val coroutines = MutableList(10) { GameCoroutine() }
        val scope = GameCoroutineScope()

        coroutines.forEach { coroutine ->
            scope.launch(coroutine) {
                pause(ticks = 1)
            }
        }
        assertTrue(coroutines.all { coroutine -> coroutine.isSuspended })
        assertEquals(coroutines.size, scope.children.size)

        scope.cancel()
        assertTrue(scope.children.isEmpty())
        assertTrue(coroutines.all { it.isIdle })
    }
}
