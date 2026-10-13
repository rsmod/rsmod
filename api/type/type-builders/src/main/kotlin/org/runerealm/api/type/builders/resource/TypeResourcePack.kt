package org.runerealm.api.type.builders.resource

import org.runerealm.api.type.builders.clientscript.ClientScriptBuilder
import org.runerealm.api.type.builders.map.MapBuilderList
import org.runerealm.api.type.builders.model.ModelBuilder

public data class TypeResourcePack(
    public val maps: MapBuilderList,
    public val clientscripts: Collection<ClientScriptBuilder>,
    public val models: Collection<ModelBuilder>,
)
