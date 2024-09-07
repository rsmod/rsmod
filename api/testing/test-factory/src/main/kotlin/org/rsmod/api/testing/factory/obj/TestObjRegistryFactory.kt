package org.rsmod.api.testing.factory.obj

import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.type.obj.ObjTypeList

public class TestObjRegistryFactory {
    public fun create(
        zones: ZoneUpdateMap = ZoneUpdateMap(),
        types: ObjTypeList = createDefaultTypes(),
    ): ObjRegistry = ObjRegistry(zones, types)

    private fun createDefaultTypes(): ObjTypeList = TestObjTypeListFactory().createDefault()
}
