package org.rsmod.game.region

import org.rsmod.game.entity.EntityList

public class RegionListSmall : EntityList<Region>(capacity = 1377, initialLastSlotUsed = -1)

public class RegionListLarge : EntityList<Region>(capacity = 700, initialLastSlotUsed = -1)
