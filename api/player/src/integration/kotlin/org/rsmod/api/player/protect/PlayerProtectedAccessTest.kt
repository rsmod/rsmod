package org.rsmod.api.player.protect

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.player.input.CountDialogInput
import org.rsmod.api.testing.GameTestState

class PlayerProtectedAccessTest {
    @Test
    fun GameTestState.`lambda exits gracefully when protected access is lost`() = runGameTest {
        withPlayer {
            var launched = false
            var unreachableInput: Int? = null
            val initialized = withProtectedAccess {
                launch {
                    val input = countDialog()
                    unreachableInput = input
                    assertTrue(false) {
                        "Assertion should never be reached as protected access should be lost."
                    }
                }
                launched = true
            }
            check(initialized) { "Unable to obtain protected access for test." }

            assertTrue(launched)
            assertNotNull(activeCoroutine)
            assertEquals(true, activeCoroutine?.isSuspended)
            // Active coroutine is suspended for the player, so their protected access should be
            // restricted.
            assertTrue(isAccessProtected)

            // Add a delay to player so the lambda block never resumes back to the suspension point,
            // as protected-access is now restricted. (from said delay)
            delay()
            check(activeCoroutine?.isSuspended == true)

            assertDoesNotThrow { resumeActiveCoroutine(CountDialogInput(5)) }

            assertNull(activeCoroutine)
            assertNull(unreachableInput)
        }
    }

    @Test
    fun GameTestState.`lambda continues normally with protected access`() = runGameTest {
        withPlayer {
            var launched = false
            var validInput: Int? = null
            val initialized = withProtectedAccess {
                launch {
                    val input = countDialog()
                    validInput = input
                }
                launched = true
            }
            check(initialized) { "Unable to obtain protected access for test." }

            assertTrue(launched)
            assertNotNull(activeCoroutine)
            assertEquals(true, activeCoroutine?.isSuspended)
            assertTrue(isAccessProtected)

            assertDoesNotThrow { resumeActiveCoroutine(CountDialogInput(5)) }

            assertNull(activeCoroutine)
            assertEquals(5, validInput)
        }
    }
}
