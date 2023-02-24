package org.rsmod.plugins.api.cache.name

import org.rsmod.plugins.types.NamedTypeMapHolder
import javax.inject.Inject
import javax.inject.Provider

public class CacheTypeNamesProvider @Inject constructor(
    private val loader: CacheTypeNameLoader
) : Provider<NamedTypeMapHolder> {

    override fun get(): NamedTypeMapHolder {
        return loader.load()
    }
}
