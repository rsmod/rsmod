package org.rsmod.api.cache.enricher

import org.rsmod.api.cache.enricher.loc.DefaultLocCacheEnricher
import org.rsmod.api.cache.enricher.loc.LocCacheEnricher
import org.rsmod.api.cache.enricher.npc.DefaultNpcCacheEnricher
import org.rsmod.api.cache.enricher.npc.NpcCacheEnricher
import org.rsmod.api.cache.enricher.obj.DefaultObjCacheEnricher
import org.rsmod.api.cache.enricher.obj.ObjCacheEnricher
import org.rsmod.module.ExtendedModule

public object CacheEnricherModule : ExtendedModule() {
    override fun bind() {
        bindInstance<CacheEnrichment>()

        // Define set bindings for currently supported cache enrichers. These sets allow multiple
        // enrichers to be registered and later processed by [CacheEnricher].
        newSetBinding<LocCacheEnricher>()
        newSetBinding<NpcCacheEnricher>()
        newSetBinding<ObjCacheEnricher>()

        // Register default enrichers in their respective sets.
        addSetBinding<LocCacheEnricher>(DefaultLocCacheEnricher::class.java)
        addSetBinding<NpcCacheEnricher>(DefaultNpcCacheEnricher::class.java)
        addSetBinding<ObjCacheEnricher>(DefaultObjCacheEnricher::class.java)
    }
}
