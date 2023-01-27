package org.rsmod.game.model.mob.list

import org.rsmod.game.model.mob.Player

private const val DEFAULT_CAPACITY = 2047
private const val DEFAULT_INDEX_PADDING = 1

public class PlayerList(
    capacity: Int = DEFAULT_CAPACITY,
    indexPadding: Int = DEFAULT_INDEX_PADDING
) : MobList<Player>(capacity, indexPadding)
