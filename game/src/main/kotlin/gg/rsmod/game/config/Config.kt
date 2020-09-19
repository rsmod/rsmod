package gg.rsmod.game.config

import gg.rsmod.game.model.map.Coordinates
import java.math.BigInteger
import java.nio.file.Path
import java.nio.file.Paths

data class GameConfig(
    val revision: Int,
    val port: Int,
    val dataPath: Path,
    val home: Coordinates
) {
    val cachePath: Path
        get() = dataPath.resolve(Paths.get("cache", "packed"))

    val rsaPath: Path
        get() = dataPath.resolve(Paths.get("rsa", "key.pem"))

    val internalConfig: Path
        get() = dataPath.resolve("internal.yml")
}

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

data class InternalConfig(
    val gameTickDelay: Int,
    val loginsPerCycle: Int,
    val logoutsPerCycle: Int
)
