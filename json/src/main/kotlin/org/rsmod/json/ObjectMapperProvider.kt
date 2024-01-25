package org.rsmod.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.inject.Inject
import jakarta.inject.Provider

public class ObjectMapperProvider @Inject constructor(
    private val jacksonModules: Set<Module>
) : Provider<ObjectMapper> {

    override fun get(): ObjectMapper {
        return ObjectMapper(JsonFactory())
            .registerKotlinModule()
            .registerModules(jacksonModules)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
    }
}
