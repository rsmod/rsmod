package org.rsmod.api.testing.factory

import org.rsmod.api.testing.factory.font.TestFontMetricsTypeListFactory
import org.rsmod.api.testing.factory.loc.TestLocTypeListFactory
import org.rsmod.api.testing.factory.obj.TestObjTypeListFactory
import org.rsmod.game.type.font.UnpackedFontMetricsType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.UnpackedObjType

/* Font metrics extension functions */
public fun Map<Int, UnpackedFontMetricsType>.q8Full(): UnpackedFontMetricsType =
    getValue(TestFontMetricsTypeListFactory.Q8_FULL_ID)

/* Loc extension functions */
public fun Map<Int, UnpackedLocType>.smallBlockWalk(): UnpackedLocType =
    getValue(TestLocTypeListFactory.SMALL_LOC_BLOCK_WALK_ID)

public fun Map<Int, UnpackedLocType>.smallBlockRange(): UnpackedLocType =
    getValue(TestLocTypeListFactory.SMALL_LOC_BLOCK_RANGE_ID)

public fun Map<Int, UnpackedLocType>.mediumBlockWalk(): UnpackedLocType =
    getValue(TestLocTypeListFactory.MEDIUM_LOC_BLOCK_WALK_ID)

public fun Map<Int, UnpackedLocType>.mediumBlockRange(): UnpackedLocType =
    getValue(TestLocTypeListFactory.MEDIUM_LOC_BLOCK_RANGE_ID)

/* Obj extension functions */
public fun Map<Int, UnpackedObjType>.standard1(): UnpackedObjType =
    getValue(TestObjTypeListFactory.STANDARD_OBJ_1_ID)

public fun Map<Int, UnpackedObjType>.standard2(): UnpackedObjType =
    getValue(TestObjTypeListFactory.STANDARD_OBJ_2_ID)

public fun Map<Int, UnpackedObjType>.stackable1(): UnpackedObjType =
    getValue(TestObjTypeListFactory.STACKABLE_OBJ_1_ID)

public fun Map<Int, UnpackedObjType>.stackable2(): UnpackedObjType =
    getValue(TestObjTypeListFactory.STACKABLE_OBJ_2_ID)

public fun Map<Int, UnpackedObjType>.cert1(): UnpackedObjType =
    getValue(TestObjTypeListFactory.CERT_OBJ_1_ID)

public fun Map<Int, UnpackedObjType>.cert2(): UnpackedObjType =
    getValue(TestObjTypeListFactory.CERT_OBJ_2_ID)
