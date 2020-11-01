package org.rsmod.game.config

import java.math.BigInteger
import java.nio.file.Path
import java.nio.file.Paths
import org.rsmod.game.model.map.Coordinates

data class GameConfig(
    val name: String,
    val majorRevision: Int,
    val minorRevision: Int,
    val port: Int,
    val dataPath: Path,
    val home: Coordinates
) {

    val cachePath: Path
        get() = dataPath.resolve(Paths.get("cache"))

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
    val logoutsPerCycle: Int,
    val actionsPerCycle: Int
)
