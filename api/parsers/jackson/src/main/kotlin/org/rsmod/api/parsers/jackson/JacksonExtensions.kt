package org.rsmod.api.parsers.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

public inline fun <reified T> ObjectMapper.readReifiedValue(value: String): T =
    readValue(value, object : TypeReference<T>() {})
