package org.rsmod.game.name

import java.nio.file.Path

interface NamedTypeLoader {

    fun load(directory: Path)
}
