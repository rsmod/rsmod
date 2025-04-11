package org.rsmod.api.pw.hash

public interface PasswordHashing {
    /**
     * Hashes the provided [password] and returns the resulting encoded hash.
     *
     * _Note: The [password] may be wiped after hashing for security._
     */
    public fun hash(password: CharArray): String

    /**
     * Verifies that the given [password] matches the stored [hash].
     *
     * _Note: The [password] may be wiped after verification for security._
     */
    public fun verify(hash: String, password: CharArray): Boolean
}
