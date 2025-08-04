package org.rsmod.api.type.builders.resource

import org.rsmod.api.type.builders.clientscript.ClientScriptBuilder
import org.rsmod.api.type.builders.map.MapBuilderList
import org.rsmod.api.type.builders.model.ModelBuilder

public data class TypeResourcePack(
    public val maps: MapBuilderList,
    public val clientscripts: Collection<ClientScriptBuilder>,
    public val models: Collection<ModelBuilder>,
)
