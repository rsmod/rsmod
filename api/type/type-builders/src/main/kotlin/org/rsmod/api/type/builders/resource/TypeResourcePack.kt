package org.rsmod.api.type.builders.resource

import org.rsmod.api.type.builders.clientscript.ClientScriptBuilder
import org.rsmod.api.type.builders.map.MapBuilderList

public data class TypeResourcePack(
    public val maps: MapBuilderList,
    public val clientscripts: Collection<ClientScriptBuilder>,
)
