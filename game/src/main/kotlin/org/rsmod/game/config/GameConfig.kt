package org.rsmod.game.config

import java.nio.file.Path

public data class GameConfig(
    val name: String,
    val world: Int,
    val build: Build,
    val dataPath: Path
) {

    public data class Build(
        val major: Int,
        val minor: Int
    )
}
