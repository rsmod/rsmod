package org.runerealm.api.testing.factory.obj

import org.runerealm.api.registry.obj.ObjRegistry
import org.runerealm.api.registry.zone.ZoneUpdateMap
import org.runerealm.game.type.obj.ObjTypeList

public class TestObjRegistryFactory {
    public fun create(
        zones: ZoneUpdateMap = ZoneUpdateMap(),
        types: ObjTypeList = createDefaultTypes(),
    ): ObjRegistry = ObjRegistry(zones, types)

    private fun createDefaultTypes(): ObjTypeList = TestObjTypeListFactory().createDefault()
}
