package org.rsmod.plugins.api

import org.rsmod.game.config.GameConfig
import java.nio.file.Path

public val GameConfig.cachePath: Path
    get() = dataPath.resolve("cache")

public val GameConfig.gameCachePath: Path
    get() = cachePath.resolve("game")
