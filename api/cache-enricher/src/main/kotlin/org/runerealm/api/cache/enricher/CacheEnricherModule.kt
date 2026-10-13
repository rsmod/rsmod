package org.runerealm.api.cache.enricher

import org.runerealm.api.cache.enricher.loc.DefaultLocCacheEnricher
import org.runerealm.api.cache.enricher.loc.LocCacheEnricher
import org.runerealm.api.cache.enricher.map.area.AreaCacheEnricher
import org.runerealm.api.cache.enricher.map.area.MultiwayAreaCacheEnricher
import org.runerealm.api.cache.enricher.npc.DefaultNpcCacheEnricher
import org.runerealm.api.cache.enricher.npc.NpcCacheEnricher
import org.runerealm.api.cache.enricher.obj.DefaultObjCacheEnricher
import org.runerealm.api.cache.enricher.obj.ObjCacheEnricher
import org.runerealm.module.ExtendedModule

public object CacheEnricherModule : ExtendedModule() {
    override fun bind() {
        bindInstance<CacheEnrichmentConfigs>()
        bindInstance<CacheEnrichmentMaps>()

        // Define set bindings for currently supported cache enrichers. These sets allow multiple
        // enrichers to be registered and later processed by [CacheEnricher].
        newSetBinding<LocCacheEnricher>()
        newSetBinding<NpcCacheEnricher>()
        newSetBinding<ObjCacheEnricher>()
        newSetBinding<AreaCacheEnricher>()

        // Register default enrichers in their respective sets.
        addSetBinding<LocCacheEnricher>(DefaultLocCacheEnricher::class.java)
        addSetBinding<NpcCacheEnricher>(DefaultNpcCacheEnricher::class.java)
        addSetBinding<ObjCacheEnricher>(DefaultObjCacheEnricher::class.java)
        addSetBinding<AreaCacheEnricher>(MultiwayAreaCacheEnricher::class.java)
    }
}
