package org.rsmod.plugins.net

import org.rsmod.game.config.GameConfig
import java.nio.file.Path

public val GameConfig.rsaPath: Path
    get() = dataPath.resolve("rsa")

public val GameConfig.rsaFile: Path
    get() = rsaPath.resolve("game.key")
