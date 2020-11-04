package org.rsmod.plugins.api.cache.map.collision

import org.rsmod.plugins.api.cache.map.xtea.loader.XteaLoadEvent

val loader: CollisionMapLoader by inject()

onEvent<XteaLoadEvent>()
    .then { loader.load() }
