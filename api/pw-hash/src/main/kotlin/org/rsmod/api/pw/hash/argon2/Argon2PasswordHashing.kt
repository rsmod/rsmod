package org.rsmod.api.pw.hash.argon2

import de.mkammerer.argon2.Argon2Factory
import org.rsmod.api.pw.hash.PasswordHashing

public class Argon2PasswordHashing : PasswordHashing {
    private val argon2 by lazy { Argon2Factory.create() }

    override fun hash(password: CharArray): String {
        return try {
            argon2.hash(ITERATIONS, MAX_MEMORY, PARALLELISM, password)
        } finally {
            argon2.wipeArray(password)
        }
    }

    override fun verify(hash: String, password: CharArray): Boolean {
        return try {
            argon2.verify(hash, password)
        } finally {
            argon2.wipeArray(password)
        }
    }

    private companion object {
        // The ideal value for this constant can vary from machine to machine, however, we are
        // opting to set a baseline and avoid the startup cost of `Argon2Helper.findIterations`.
        private const val ITERATIONS = 20
        private const val MAX_MEMORY = 65536
        private const val PARALLELISM = 1
    }
}
