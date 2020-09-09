package gg.rsmod.game.config

import java.math.BigInteger

data class RsaConfig(
    val exponent: BigInteger,
    val modulus: BigInteger
) {
    val isEnabled: Boolean
        get() = this !== DISABLED_RSA

    companion object {
        val DISABLED_RSA = RsaConfig(BigInteger.ZERO, BigInteger.ZERO)
    }
}
