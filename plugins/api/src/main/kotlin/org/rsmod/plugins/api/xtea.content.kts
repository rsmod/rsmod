package org.rsmod.plugins.api

import org.rsmod.plugins.api.cache.map.xtea.XteaFileLoader

// TODO: offload to a non-blocking thread (that gets joined before game accepts connections)
private val loader: XteaFileLoader by inject()
loader.load()
