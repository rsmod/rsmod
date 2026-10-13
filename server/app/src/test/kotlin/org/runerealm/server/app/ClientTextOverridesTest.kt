package org.runerealm.server.app

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ClientTextOverridesTest {
    @Test
    fun `replaces the bank title string operand in a client script`() {
        // `push_constant_string "The Bank of Gielinor"` followed by another instruction.
        val script = byteArrayOf(0, 3) + "The Bank of Gielinor".latin1() + byteArrayOf(0, 0, 33)
        val expected = byteArrayOf(0, 3) + "The Bank of RuneRealm".latin1() + byteArrayOf(0, 0, 33)
        assertArrayEquals(expected, ClientTextOverrides.replaceStrings(script))
    }

    @Test
    fun `replaces the deposit box title as a whole string`() {
        val iface =
            byteArrayOf(3, 1) + "The Bank of Gielinor - Deposit Box".latin1() + byteArrayOf(0)
        val expected =
            byteArrayOf(3, 1) + "The Bank of RuneRealm - Deposit Box".latin1() + byteArrayOf(0)
        assertArrayEquals(expected, ClientTextOverrides.replaceStrings(iface))
    }

    @Test
    fun `leaves other strings alone`() {
        val text = byteArrayOf(0, 3) + "Welcome to The Bank of Gielinor".latin1() + byteArrayOf(0)
        assertNull(ClientTextOverrides.replaceStrings(text))
        val unterminated = byteArrayOf(0, 3) + "The Bank of Gielinor!".latin1() + byteArrayOf(0)
        assertNull(ClientTextOverrides.replaceStrings(unterminated))
    }

    private fun String.latin1(): ByteArray = toByteArray(Charsets.ISO_8859_1)
}
