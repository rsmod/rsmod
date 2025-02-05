package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.varp.VarpBuilder

internal object VarpBuilds : VarpBuilder() {
    init {
        build("inv_capacity_65530")
        build("generic_storage_65531")
    }
}
