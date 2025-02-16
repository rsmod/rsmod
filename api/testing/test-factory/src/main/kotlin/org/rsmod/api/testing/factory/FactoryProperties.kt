package org.rsmod.api.testing.factory

import org.rsmod.api.testing.factory.entity.TestPathingEntityFactory
import org.rsmod.api.testing.factory.loc.TestLocFactory
import org.rsmod.api.testing.factory.loc.TestLocRegistryFactory
import org.rsmod.api.testing.factory.loc.TestLocTypeFactory
import org.rsmod.api.testing.factory.loc.TestLocTypeListFactory
import org.rsmod.api.testing.factory.map.TestCollisionFactory
import org.rsmod.api.testing.factory.npc.TestNpcFactory
import org.rsmod.api.testing.factory.npc.TestNpcTypeFactory
import org.rsmod.api.testing.factory.obj.TestObjFactory
import org.rsmod.api.testing.factory.obj.TestObjRegistryFactory
import org.rsmod.api.testing.factory.obj.TestObjTypeFactory
import org.rsmod.api.testing.factory.obj.TestObjTypeListFactory
import org.rsmod.api.testing.factory.player.TestPlayerFactory
import org.rsmod.api.testing.factory.region.TestRegionRegistryFactory

/* Entity factory properties */
public val entityFactory: TestPathingEntityFactory
    get() = TestPathingEntityFactory()

/* Loc factory properties */
public val locFactory: TestLocFactory
    get() = TestLocFactory()

public val locTypeFactory: TestLocTypeFactory
    get() = TestLocTypeFactory()

public val locTypeListFactory: TestLocTypeListFactory
    get() = TestLocTypeListFactory()

public val locRegistryFactory: TestLocRegistryFactory
    get() = TestLocRegistryFactory()

/* Map factory properties */
public val collisionFactory: TestCollisionFactory
    get() = TestCollisionFactory()

/* Npc factory properties */
public val npcFactory: TestNpcFactory
    get() = TestNpcFactory()

public val npcTypeFactory: TestNpcTypeFactory
    get() = TestNpcTypeFactory()

/* Obj factory properties */
public val objFactory: TestObjFactory
    get() = TestObjFactory()

public val objTypeFactory: TestObjTypeFactory
    get() = TestObjTypeFactory()

public val objTypeListFactory: TestObjTypeListFactory
    get() = TestObjTypeListFactory()

public val objRegistryFactory: TestObjRegistryFactory
    get() = TestObjRegistryFactory()

/* Player factory properties */
public val playerFactory: TestPlayerFactory
    get() = TestPlayerFactory()

/* Region factory properties */
public val regionRegistryFactory: TestRegionRegistryFactory
    get() = TestRegionRegistryFactory()
