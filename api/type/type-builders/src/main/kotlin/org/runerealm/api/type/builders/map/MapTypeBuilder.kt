package org.runerealm.api.type.builders.map

import org.runerealm.api.type.builders.resource.ResourceTypeBuilder

public abstract class MapTypeBuilder : ResourceTypeBuilder() {
    public abstract fun onPackMapTask()
}
