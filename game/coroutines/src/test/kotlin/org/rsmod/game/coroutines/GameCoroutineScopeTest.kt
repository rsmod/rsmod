package org.rsmod.game.coroutines

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GameCoroutineScopeTest {

    @Test
    fun testSupervisedCoroutines() {
        val scope = GameCoroutineScope()
        val coroutine1 = GameCoroutine()
        Assertions.assertTrue(scope.children.isEmpty())

        /* non-suspended coroutine should not be supervised */
        scope.launch(coroutine1) {}
        Assertions.assertTrue(scope.children.isEmpty())

        scope.launch(coroutine1) {
            pause(ticks = 1)
        }
        Assertions.assertEquals(1, scope.children.size)
        Assertions.assertSame(scope.children[0], coroutine1)

        val coroutine2 = scope.launch {
            pause(Int::class)
        }
        Assertions.assertEquals(2, scope.children.size)
        Assertions.assertNotSame(scope.children[0], coroutine2)

        coroutine1.resume()
        scope.advance()
        Assertions.assertTrue(coroutine1.isIdle)
        Assertions.assertEquals(1, scope.children.size)
        Assertions.assertSame(scope.children[0], coroutine2)

        coroutine2.resumeWith(0)
        scope.advance()
        Assertions.assertTrue(coroutine2.isIdle)
        Assertions.assertTrue(scope.children.isEmpty())
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
        Assertions.assertTrue(coroutines.all { coroutine -> coroutine.isSuspended })
        Assertions.assertEquals(coroutines.size, scope.children.size)

        scope.cancel()
        Assertions.assertTrue(scope.children.isEmpty())
        Assertions.assertTrue(coroutines.all { it.isIdle })
    }
}
