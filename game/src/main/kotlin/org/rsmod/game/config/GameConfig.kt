package org.rsmod.game.config

import java.nio.file.Path

public data class GameConfig(
    val name: String,
    val world: Int,
    val port: Int,
    val dataPath: Path
)
