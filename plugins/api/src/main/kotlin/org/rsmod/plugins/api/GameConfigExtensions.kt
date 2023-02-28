package org.rsmod.plugins.api

import org.rsmod.game.config.GameConfig
import java.nio.file.Path

public val GameConfig.cachePath: Path
    get() = dataPath.resolve("cache")

public val GameConfig.gameCachePath: Path
    get() = cachePath.resolve("game")

public val GameConfig.js5CachePath: Path
    get() = cachePath.resolve("js5")

public val GameConfig.vanillaCachePath: Path
    get() = cachePath.resolve("vanilla")

public val GameConfig.pluginPath: Path
    get() = dataPath.resolve("plugins")

public val GameConfig.pluginConfigPath: Path
    get() = pluginPath.resolve("configs")
