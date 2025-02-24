package org.rsmod.content.skills.woodcutting.configs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.assertions.assertNotNullContract
import org.rsmod.content.skills.woodcutting.scripts.Woodcutting.Companion.cutSuccessRates
import org.rsmod.game.obj.InvObj

class WoodcuttingConfigTest {
    @Test
    fun GameTestState.`ensure all axe objs have required params`() = runBasicGameTest {
        val axes = cacheTypes.objs.values.filter { it.isContentType(content.woodcutting_axe) }
        for (axe in axes) {
            val axeParams = axe.paramMap
            assertNotNullContract(axeParams)
            assertTrue(params.skill_levelreq in axeParams)
            assertTrue(params.skill_anim in axeParams)
        }
    }

    @Test
    fun GameTestState.`ensure all tree locs have required params`() = runBasicGameTest {
        val trees = cacheTypes.locs.values.filter { it.isContentType(content.tree) }
        for (tree in trees) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertTrue(params.skill_levelreq in treeParams)
            assertTrue(params.skill_productitem in treeParams)
            assertTrue(params.skill_xp in treeParams)
            assertTrue(params.next_loc_stage in treeParams)
            assertTrue(params.respawn_time in treeParams)
            assertTrue(WoodcuttingParams.success_rates in treeParams)
        }

        // Trees which turn into stumps after a set period of time.
        val timed = trees.filter { it.paramMap?.contains(params.despawn_time) == true }
        for (tree in timed) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertFalse(params.deplete_chance in treeParams)
        }

        // Trees which turn into stumps based on random rates.
        val standard = trees - timed.toSet()
        for (tree in standard) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertTrue(params.deplete_chance in treeParams)
            assertTrue(treeParams[params.deplete_chance] in 0..255)
        }

        // Trees which have a variable respawn time.
        val respawnVariable =
            trees.filter {
                it.hasParam(params.respawn_time_low) || it.hasParam(params.respawn_time_high)
            }
        for (tree in respawnVariable) {
            val treeParams = tree.paramMap
            assertNotNullContract(treeParams)
            assertTrue(params.respawn_time_low in treeParams)
            assertTrue(params.respawn_time_high in treeParams)
            assertEquals(0, tree.param(params.respawn_time))
        }

        // All trees must have all axe success rates defined.
        val axes = cacheTypes.objs.values.filter { it.isContentType(content.woodcutting_axe) }
        for (tree in trees) {
            val treeParams = checkNotNull(tree.paramMap)
            val enum = checkNotNull(treeParams[WoodcuttingParams.success_rates])
            val successRates = cacheTypes.enums[enum]
            assertNotNullContract(successRates)
            for (axe in axes) {
                val (lowRate, highRate) =
                    assertDoesNotThrow("Axe success rates not defined: axe=$axe, tree=$tree") {
                        cutSuccessRates(tree, InvObj(axe), cacheTypes.enums)
                    }
                assertTrue(lowRate > 0)
                assertTrue(highRate > 0)
            }
        }
    }
}
