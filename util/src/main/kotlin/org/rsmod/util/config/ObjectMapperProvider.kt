package org.rsmod.util.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.inject.Provider

class ObjectMapperProvider : Provider<ObjectMapper> {

    override fun get(): ObjectMapper {
        return ObjectMapper(YAMLFactory())
            .registerKotlinModule()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    }
}
