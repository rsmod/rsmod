package org.rsmod.game.coroutines

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GameCoroutineScopeTest {

    @Test
    fun testSupervisedCoroutines() {
        val scope = GameCoroutineScope(superviseCoroutines = true)
        val coroutine1 = GameCoroutine()
        Assertions.assertTrue(scope.getSupervisedChildren().isEmpty())

        /* non-suspended coroutine should not be supervised */
        scope.launch(coroutine1) {}
        Assertions.assertTrue(scope.getSupervisedChildren().isEmpty())

        scope.launch(coroutine1) {
            pause(ticks = 1)
        }
        Assertions.assertEquals(1, scope.getSupervisedChildren().size)
        Assertions.assertSame(scope.getSupervisedChildren()[0], coroutine1)

        val coroutine2 = scope.launch {
            pause(Int::class)
        }
        Assertions.assertEquals(2, scope.getSupervisedChildren().size)
        Assertions.assertNotSame(scope.getSupervisedChildren()[0], coroutine2)

        coroutine1.resume()
        Assertions.assertTrue(coroutine1.isIdle)
        Assertions.assertEquals(1, scope.getSupervisedChildren().size)
        Assertions.assertSame(scope.getSupervisedChildren()[0], coroutine2)

        coroutine2.resumeWith(0)
        Assertions.assertTrue(coroutine2.isIdle)
        Assertions.assertTrue(scope.getSupervisedChildren().isEmpty())
    }

    @Test
    fun testAutoClose() {
        val coroutines = MutableList(10) { GameCoroutine() }
        val scope = GameCoroutineScope(superviseCoroutines = true)
        scope.use {
            coroutines.forEach { coroutine ->
                it.launch(coroutine) {
                    pause(ticks = 1)
                }
            }
            Assertions.assertTrue(coroutines.all { coroutine -> coroutine.isSuspended })
            Assertions.assertEquals(coroutines.size, it.getSupervisedChildren().size)
        }
        Assertions.assertTrue(scope.getSupervisedChildren().isEmpty())
        Assertions.assertTrue(coroutines.all { it.isIdle })
    }
}
