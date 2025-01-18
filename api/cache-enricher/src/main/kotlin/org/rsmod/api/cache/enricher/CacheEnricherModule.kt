package org.rsmod.api.cache.enricher

import org.rsmod.module.ExtendedModule

public object CacheEnricherModule : ExtendedModule() {
    override fun bind() {
        bindInstance<CacheEnrichment>()
    }
}
