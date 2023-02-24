package org.rsmod.plugins.net

import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.api.cachePath
import java.nio.file.Path

public val GameConfig.js5CachePath: Path
    get() = cachePath.resolve("js5")

public val GameConfig.rsaPath: Path
    get() = dataPath.resolve("rsa")

public val GameConfig.rsaFile: Path
    get() = rsaPath.resolve("game.key")
