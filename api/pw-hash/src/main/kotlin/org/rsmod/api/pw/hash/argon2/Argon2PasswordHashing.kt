package org.rsmod.api.pw.hash.argon2

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import org.rsmod.api.pw.hash.PasswordHashing

public class Argon2PasswordHashing : PasswordHashing {
    private val argon2 by lazy { Argon2Factory.create() }
    private val iterations by lazy { findIterations(argon2) }

    override fun hash(password: CharArray): String {
        return try {
            argon2.hash(iterations, MAX_MEMORY, PARALLELISM, password)
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
        private const val MAX_COMPUTE_TIME_MS = 1000L
        private const val MAX_MEMORY = 65536
        private const val PARALLELISM = 1

        private fun findIterations(argon2: Argon2): Int {
            return Argon2Helper.findIterations(argon2, MAX_COMPUTE_TIME_MS, MAX_MEMORY, PARALLELISM)
        }
    }
}
