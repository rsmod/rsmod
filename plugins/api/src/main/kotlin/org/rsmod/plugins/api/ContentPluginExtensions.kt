package org.rsmod.plugins.api

import org.rsmod.game.plugins.content.ContentPlugin
import org.rsmod.plugins.api.cache.type.enums.EnumTypeList

public val ContentPlugin.enums: EnumTypeList
    get() = injector.getInstance(EnumTypeList::class.java)
