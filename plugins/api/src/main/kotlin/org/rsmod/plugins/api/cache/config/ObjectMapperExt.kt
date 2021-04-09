package org.rsmod.plugins.api.cache.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

internal fun ObjectMapper.toConfigMapper(): ObjectMapper {
    return copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
