package org.rsmod.game.config

import org.rsmod.game.model.GameEnv
import org.rsmod.game.model.map.Coordinates
import java.nio.file.Path

public data class GameConfig(
    val name: String,
    val world: Int,
    val env: GameEnv,
    val dataPath: Path,
    val spawn: Coordinates
) {

    val cachePath: Path get() = dataPath.resolve("cache")
    val packedCachePath: Path get() = cachePath.resolve("packed")

    val rsaPath: Path get() = dataPath.resolve("rsa")
    val rsaFile: Path get() = rsaPath.resolve("game.key")
}
