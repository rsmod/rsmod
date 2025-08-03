package org.rsmod.api.type.builders.map

import org.rsmod.api.type.builders.resource.ResourceTypeBuilder

public abstract class MapTypeBuilder : ResourceTypeBuilder() {
    public abstract fun onPackMapTask()
}
