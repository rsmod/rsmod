package gg.rsmod.game.config

import java.nio.file.Path
import java.nio.file.Paths

data class GameConfig(
    val revision: Int,
    val port: Int,
    val dataPath: Path
) {
    val cachePath: Path
        get() = dataPath.resolve(Paths.get("cache", "packed"))

    val rsaPath: Path
        get() = dataPath.resolve(Paths.get("rsa", "key.pem"))
}
