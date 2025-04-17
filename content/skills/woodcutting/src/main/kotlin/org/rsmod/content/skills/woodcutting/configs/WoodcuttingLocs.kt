package org.rsmod.content.skills.woodcutting.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.content.skills.woodcutting.configs.WoodcuttingParams.success_rates
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType

private typealias stumps = WoodcuttingStumps

private typealias axe_enums = WoodcuttingEnums

internal typealias woodcutting_locs = WoodcuttingLocs

internal object WoodcuttingTrees : LocEditor() {
    init {
        regular("achey_tree", stumps.achey_stump_3371, objs.achey_tree_logs)
        arctic("arctic_pine", stumps.arctic_pine_stump_21274)
        burnt("fossil_deadtree_large1", stumps.burnt_stump_30855)
        burnt("fossil_deadtree_small1", stumps.burnt_stump_30856)
        dead("deadtree1", stumps.dead_stump_1347)
        dead("deadtree1_large", stumps.dead_stump_1347)
        dead("deadtree4", stumps.dead_stump_6212)
        dead("lightdeadtree1", stumps.dead_stump_1349)
        dead("deadtree2", stumps.dead_stump_1351)
        dead("deadtree2_dark", stumps.dead_stump_1353)
        dead("deadtree3", stumps.dead_stump_1354)
        dead("deadtree2_snowy", stumps.dead_stump_23054)
        dead("deadtree2_swamp", stumps.dead_stump_1352)
        dead("deadtree6", stumps.dead_stump_1358)
        dead("deadtree_burnt", stumps.dead_stump_1353)
        dead("mdaughter_passable_tree", stumps.dead_stump_5905)
        dead("fai_varrock_dead_tree", stumps.dying_stump_3649)
        evergreen("evergreen_vsnowy_large", stumps.evergreen_stump_1355)
        evergreen("evergreen_snowy_large", stumps.evergreen_stump_1355)
        evergreen("evergreen", stumps.regular_stump_1342)
        evergreen("evergreen_large", stumps.evergreen_stump_1355)
        hollow("hollow_tree", stumps.hollow_stump_2310)
        hollow("hollow_tree_big", stumps.hollow_stump_4061)
        jungle("mm_bush_kharazi_jungle_tree1", stumps.jungle_stump_4819)
        jungle("mm_bush_kharazi_jungle_tree2", stumps.jungle_stump_4821)
        magic("magictree", stumps.magic_stump_9713)
        mahogany("prif_mahoganytree", stumps.mahogany_stump_36689)
        mahogany("mahoganytree_update", stumps.mahogany_stump_40761)
        mahogany("mahoganytree", stumps.mahogany_stump_9035)
        maple("mapletree", stumps.maple_stump_9712)
        maple("prif_mapletree", stumps.maple_stump_36682)
        maple("mapletree_update", stumps.maple_stump_40755)
        juniper("mature_juniper_tree", stumps.mature_stump_27500)
        oak("oaktree", stumps.oak_stump_1356)
        oak("tree_oak_default01", stumps.oak_stump_42396)
        redwood("redwoodtree_l", stumps.redwood_stump_29669)
        redwood("redwoodtree_r", stumps.redwood_stump_29671)
        regular("tree", stumps.regular_stump_1342)
        regular("lighttree", stumps.regular_stump_1343)
        regular("tree2", stumps.regular_stump_1342)
        regular("tree3", stumps.regular_stump_1342)
        regular("lighttree2", stumps.regular_stump_1343)
        regular("snowtree1", stumps.evergreen_stump_1355)
        regular("snowtree2", stumps.evergreen_stump_1355)
        regular("snowtree3", stumps.evergreen_stump_1355)
        regular("pest_tree", stumps.regular_stump_1342)
        regular("pest_tree2", stumps.regular_stump_1342)
        regular("prif_tree_normal_1", stumps.regular_stump_36673)
        regular("prif_tree_normal_2", stumps.regular_stump_36675)
        regular("prif_outside_tree_normal_1", stumps.regular_stump_36678)
        regular("prif_outside_tree_normal_2", stumps.regular_stump_36678)
        regular("regicide_tree_large", stumps.regular_stump_3880)
        regular("regicide_tree_large2", stumps.regular_stump_3880)
        regular("regicide_tree_large3", stumps.regular_stump_3880)
        regular("regicide_tree_small", stumps.regular_stump_3884)
        regular("tree_update_1", stumps.regular_stump_40751)
        regular("tree_update_2", stumps.regular_stump_40753)
        regular("tree_normal_default01", stumps.regular_stump_42394)
        regular("avium_tree_1", stumps.regular_stump_51763)
        regular("avium_tree_2", stumps.regular_stump_51765)
        regular("tree4", stumps.regular_stump_1342)
        regular("avium_tree_dark", stumps.regular_stump_53189)
        teak("prif_teaktree", stumps.teak_stump_36687)
        teak("teaktree_update", stumps.teak_stump_40759)
        teak("teaktree", stumps.teak_stump_9037)
        willow("willowtree", stumps.willow_stump_9711)
        willow("willow_tree2", stumps.willow_stump_9471)
        willow("willow_tree3", stumps.willow_stump_9471)
        willow("willow_tree4", stumps.willow_stump_9471)
        yew("yewtree", stumps.yew_stump_9714)
        yew("prif_yewtree", stumps.yew_stump_36684)
        yew("yewtree_update", stumps.yew_stump_40757)
        yew("tree_yew_default01", stumps.yew_stump_42392)
        yew("deadman_yewtree_insurancestall", stumps.yew_stump_9714)
    }

    private fun regular(internalName: String, stump: LocType, logs: ObjType = objs.logs) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.deplete_chance] = 0
            param[params.respawn_time] = 0
            param[params.respawn_time_low] = 60
            param[params.respawn_time_high] = 100
            param[params.levelrequire] = 1
            param[params.skill_xp] = PlayerStatMap.toFineXP(25.0).toInt()
            param[params.skill_productitem] = logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.regular_tree_axes
        }
    }

    private fun dead(internalName: String, stump: LocType) {
        regular(internalName, stump)
    }

    private fun burnt(internalName: String, stump: LocType) {
        regular(internalName, stump, objs.charcoal)
    }

    private fun evergreen(internalName: String, stump: LocType) {
        regular(internalName, stump)
    }

    private fun jungle(internalName: String, stump: LocType) {
        regular(internalName, stump)
    }

    private fun oak(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 45
            param[params.respawn_time] = 15
            param[params.levelrequire] = 15
            param[params.skill_xp] = PlayerStatMap.toFineXP(37.5).toInt()
            param[params.skill_productitem] = objs.oak_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.oak_tree_axes
        }
    }

    private fun willow(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 50
            param[params.respawn_time] = 15
            param[params.levelrequire] = 30
            param[params.skill_xp] = PlayerStatMap.toFineXP(67.5).toInt()
            param[params.skill_productitem] = objs.willow_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.willow_tree_axes
        }
    }

    private fun teak(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 50
            param[params.respawn_time] = 16
            param[params.levelrequire] = 35
            param[params.skill_xp] = PlayerStatMap.toFineXP(85.0).toInt()
            param[params.skill_productitem] = objs.teak_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.teak_tree_axes
        }
    }

    @Suppress("SameParameterValue")
    private fun juniper(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.deplete_chance] = 16
            param[params.respawn_time] = 15
            param[params.levelrequire] = 42
            param[params.skill_xp] = PlayerStatMap.toFineXP(35.0).toInt()
            param[params.skill_productitem] = objs.juniper_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.regular_tree_axes
        }
    }

    private fun maple(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 100
            param[params.respawn_time] = 60
            param[params.levelrequire] = 45
            param[params.skill_xp] = PlayerStatMap.toFineXP(100.0).toInt()
            param[params.skill_productitem] = objs.maple_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.maple_tree_axes
        }
    }

    @Suppress("SameParameterValue")
    private fun arctic(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 140
            param[params.respawn_time] = 15
            param[params.levelrequire] = 54
            param[params.skill_xp] = PlayerStatMap.toFineXP(40.0).toInt()
            param[params.skill_productitem] = objs.arctic_pine_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.arctic_tree_axes
        }
    }

    private fun mahogany(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 100
            param[params.respawn_time] = 15
            param[params.levelrequire] = 50
            param[params.skill_xp] = PlayerStatMap.toFineXP(125.0).toInt()
            param[params.skill_productitem] = objs.mahogany_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.mahogany_tree_axes
        }
    }

    private fun yew(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 190
            param[params.respawn_time] = 100
            param[params.levelrequire] = 60
            param[params.skill_xp] = PlayerStatMap.toFineXP(175.0).toInt()
            param[params.skill_productitem] = objs.yew_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.yew_tree_axes
        }
    }

    @Suppress("SameParameterValue")
    private fun magic(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 390
            param[params.respawn_time] = 200
            param[params.levelrequire] = 75
            param[params.skill_xp] = PlayerStatMap.toFineXP(250.0).toInt()
            param[params.skill_productitem] = objs.magic_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.magic_tree_axes
        }
    }

    private fun redwood(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 440
            param[params.respawn_time] = 200
            param[params.levelrequire] = 90
            param[params.skill_xp] = PlayerStatMap.toFineXP(380.0).toInt()
            param[params.skill_productitem] = objs.redwood_logs
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.redwood_tree_axes
        }
    }

    private fun hollow(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 60
            param[params.respawn_time] = 43
            param[params.levelrequire] = 45
            param[params.skill_xp] = PlayerStatMap.toFineXP(82.5).toInt()
            param[params.skill_productitem] = objs.bark
            param[params.next_loc_stage] = stump
            param[success_rates] = axe_enums.hollow_tree_axes
        }
    }
}

internal object WoodcuttingLocs : LocReferences() {
    val redwoodtree_l = find("redwoodtree_l")
    val redwoodtree_r = find("redwoodtree_r")
}

internal object WoodcuttingStumps : LocReferences() {
    val arctic_pine_stump_21274 = find("arctic_pine_tree_stump")
    val achey_stump_3371 = find("achey_tree_stump")
    val burnt_stump_30855 = find("fossil_deadtree_large1_stump")
    val burnt_stump_30856 = find("fossil_deadtree_small1_stump")
    val dead_stump_1347 = find("deadtree1_large_stump")
    val dead_stump_1349 = find("deadtree1_light_stump")
    val dead_stump_1351 = find("deadtree2_stump")
    val dead_stump_1352 = find("deadtree2_stump_swamp")
    val dead_stump_1353 = find("deadtree2_stump_dark")
    val dead_stump_1354 = find("deadtree3_stump")
    val dead_stump_1358 = find("deadtree6_stump")
    val dead_stump_23054 = find("deadtree2_stump_snow")
    val dead_stump_5905 = find("mdaughter_passable_tree_stump")
    val dead_stump_6212 = find("deadtree4_stump")
    val dying_stump_3649 = find("fai_varrock_dead_tree_stump")
    val evergreen_stump_1355 = find("evergreen_large_stump")
    val hollow_stump_2310 = find("hollow_tree_stump")
    val hollow_stump_4061 = find("hollow_tree_stump_big")
    val jungle_stump_4819 = find("mm_bush_kharazi_jungle_tree1_stump")
    val jungle_stump_4821 = find("mm_bush_kharazi_jungle_tree2_stump")
    val magic_stump_9713 = find("magic_tree_stump_new")
    val mahogany_stump_36689 = find("prif_mahogany_stump")
    val mahogany_stump_40761 = find("mahoganytree_update_stump")
    val mahogany_stump_9035 = find("mahogany_stump")
    val maple_stump_36682 = find("prif_mapletree_stump")
    val maple_stump_40755 = find("mapletree_update_stump")
    val maple_stump_9712 = find("maple_tree_stump_new")
    val mature_stump_27500 = find("mature_juniper_tree_stump")
    val oak_stump_1356 = find("oaktree_stump")
    val oak_stump_42396 = find("tree_oak_stump01")
    val redwood_stump_29669 = find("redwoodtree_l_cut")
    val redwood_stump_29671 = find("redwoodtree_r_cut")
    val regular_stump_1342 = find("treestump2")
    val regular_stump_1343 = find("treestump2_light")
    val regular_stump_36673 = find("prif_tree_normal_1_stump")
    val regular_stump_36675 = find("prif_tree_normal_2_stump")
    val regular_stump_36678 = find("prif_outside_tree_normal_1_stump")
    val regular_stump_3880 = find("regicide_tree_stump")
    val regular_stump_3884 = find("regicide_tree_stump_small")
    val regular_stump_40751 = find("tree_update_1_stump")
    val regular_stump_40753 = find("tree_update_2_stump")
    val regular_stump_42394 = find("tree_normal_stump01")
    val regular_stump_51763 = find("avium_tree_1_stump")
    val regular_stump_51765 = find("avium_tree_2_stump")
    val regular_stump_53189 = find("avium_tree_dark_stump")
    val teak_stump_36687 = find("prif_teak_stump")
    val teak_stump_40759 = find("teaktree_update_stump")
    val teak_stump_9037 = find("teak_stump")
    val willow_stump_9471 = find("willow_tree2or3or4_stump")
    val willow_stump_9711 = find("willow_tree_stump_new")
    val yew_stump_36684 = find("prif_yewtree_stump")
    val yew_stump_40757 = find("yewtree_update_stump")
    val yew_stump_42392 = find("tree_yew_stump01")
    val yew_stump_9714 = find("yew_tree_stump_new")
}
