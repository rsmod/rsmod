package org.rsmod.api.totp

import java.time.Instant

public interface Totp {
    /**
     * Verifies that the provided [code] is valid for the given [secret] at the specified [now]
     * timestamp.
     *
     * _This method does **not** wipe the [secret] after verification. Callers should use
     * [useSecret] or manually wipe the secret after use to avoid sensitive data lingering in
     * memory._
     */
    public fun verifyCode(secret: CharArray, code: String, now: Instant = Instant.now()): Boolean

    /**
     * Generates a new TOTP secret that can be used for future verification.
     *
     * _Note: The returned [CharArray] contains sensitive data and should be wiped after use.
     * Callers should prefer using [useGeneratedSecret] to automatically wipe the secret after use._
     *
     * @see [useGeneratedSecret]
     */
    public fun generateSecret(): CharArray
}

/**
 * Uses the provided [secret] within the given [block], then wipes the secret after execution.
 *
 * This ensures that sensitive data is cleared from memory after use.
 */
public inline fun <R> useSecret(secret: CharArray, block: (CharArray) -> R): R {
    return try {
        block(secret)
    } finally {
        secret.fill('\u0000')
    }
}

/**
 * Generates a new TOTP secret, passes it to [block], then wipes the secret after execution.
 *
 * This is a convenience wrapper around [Totp.generateSecret] and [useSecret].
 */
public inline fun <R> Totp.useGeneratedSecret(block: (CharArray) -> R): R {
    val secret = generateSecret()
    return useSecret(secret, block)
}
