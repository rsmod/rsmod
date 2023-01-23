package org.rsmod.toml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import javax.inject.Provider

public class ObjectMapperProvider : Provider<ObjectMapper> {

    override fun get(): ObjectMapper {
        return ObjectMapper(TomlFactory())
            .registerKotlinModule()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }
}
