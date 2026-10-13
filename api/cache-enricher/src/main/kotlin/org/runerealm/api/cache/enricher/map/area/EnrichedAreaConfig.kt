package org.runerealm.api.cache.enricher.map.area

import org.runerealm.api.cache.map.area.MapAreaDefinition
import org.runerealm.map.square.MapSquareKey

public data class EnrichedAreaConfig(val square: MapSquareKey, val areas: MapAreaDefinition)
