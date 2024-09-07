package org.rsmod.api.testing.factory.loc

import org.rsmod.game.type.loc.LocTypeList

public class TestLocTypeListFactory {
    public fun createDefault(): LocTypeList {
        val factory = TestLocTypeFactory()
        val small1 =
            factory.create(SMALL_LOC_BLOCK_WALK_ID) {
                width = 1
                length = 1
                blockRange = false
            }
        val medium1 =
            factory.create(MEDIUM_LOC_BLOCK_WALK_ID) {
                width = 2
                length = 2
                blockRange = false
            }
        val small2 =
            factory.create(SMALL_LOC_BLOCK_RANGE_ID) {
                width = 1
                length = 1
                blockRange = true
            }
        val medium2 =
            factory.create(MEDIUM_LOC_BLOCK_RANGE_ID) {
                width = 2
                length = 2
                blockRange = true
            }
        val map =
            mapOf(
                SMALL_LOC_BLOCK_WALK_ID to small1,
                MEDIUM_LOC_BLOCK_WALK_ID to medium1,
                SMALL_LOC_BLOCK_RANGE_ID to small2,
                MEDIUM_LOC_BLOCK_RANGE_ID to medium2,
            )
        return LocTypeList(map)
    }

    internal companion object {
        const val SMALL_LOC_BLOCK_WALK_ID = 1
        const val SMALL_LOC_BLOCK_RANGE_ID = 2
        const val MEDIUM_LOC_BLOCK_WALK_ID = 3
        const val MEDIUM_LOC_BLOCK_RANGE_ID = 4
    }
}
