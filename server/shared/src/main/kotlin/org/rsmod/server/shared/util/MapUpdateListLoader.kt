package org.rsmod.server.shared.util

import org.rsmod.api.type.builders.map.MapTypeBuilder
import org.rsmod.api.type.builders.map.MapUpdateList
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.server.shared.loader.MapTypeBuilderLoader

object MapUpdateListLoader {
    fun load(loader: MapTypeBuilderLoader): MapUpdateList {
        val builders = loader.load()
        return create(builders)
    }

    private fun create(builders: Collection<MapTypeBuilder>): MapUpdateList {
        val areas = builders.filterIsInstance<MapAreaBuilder>()
        return MapUpdateList(areas)
    }
}
