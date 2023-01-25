package org.rsmod.toml

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import javax.inject.Inject
import javax.inject.Provider

public class ObjectMapperProvider @Inject constructor(
    private val jacksonModules: Set<Module>
) : Provider<ObjectMapper> {

    override fun get(): ObjectMapper {
        return ObjectMapper(TomlFactory())
            .registerKotlinModule()
            .registerModules(jacksonModules)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }
}
