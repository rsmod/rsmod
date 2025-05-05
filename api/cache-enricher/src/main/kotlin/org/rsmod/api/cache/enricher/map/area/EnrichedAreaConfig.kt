package org.rsmod.api.cache.enricher.map.area

import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.map.square.MapSquareKey

public data class EnrichedAreaConfig(val square: MapSquareKey, val areas: MapAreaDefinition)
