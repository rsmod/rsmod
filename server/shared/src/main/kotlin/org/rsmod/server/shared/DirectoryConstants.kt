package org.rsmod.server.shared

import java.nio.file.Path
import java.nio.file.Paths

object DirectoryConstants {
    val DATA_PATH: Path = Paths.get(".data")
    val SYMBOL_PATH: Path = DATA_PATH.resolve("symbols")
    val CACHE_PATH: Path = DATA_PATH.resolve("cache")
}
