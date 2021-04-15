package org.rsmod.game.config

import org.rsmod.game.GameEnv
import org.rsmod.game.model.map.Coordinates
import java.math.BigInteger
import java.nio.file.Path
import java.nio.file.Paths

data class GameConfig(
    val name: String,
    val majorRevision: Int,
    val minorRevision: Int,
    val port: Int,
    val dataPath: Path,
    val pluginPath: Path,
    val home: Coordinates,
    val env: GameEnv
) {

    val cachePath: Path
        get() = dataPath.resolve("cache")

    val rsaPath: Path
        get() = dataPath.resolve(Paths.get("rsa", "key.pem"))

    val internalConfig: Path
        get() = dataPath.resolve("internal.yml")

    val pluginConfigPath: Path
        get() = pluginPath.resolve("resources")
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
