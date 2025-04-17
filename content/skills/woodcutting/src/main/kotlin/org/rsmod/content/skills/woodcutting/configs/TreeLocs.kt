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

private typealias stumps = StumpLocs

internal typealias trees = TreeLocs

private typealias axe_enums = WoodcuttingEnums

internal object WoodcuttingTrees : LocEditor() {
    init {
        regular(trees.achey_tree, stumps.achey_stump_3371, objs.achey_tree_logs)
        arctic(trees.arctic_pine, stumps.arctic_pine_stump_21274)
        burnt(trees.fossil_deadtree_large1, stumps.burnt_stump_30855)
        burnt(trees.fossil_deadtree_small1, stumps.burnt_stump_30856)
        dead(trees.deadtree1, stumps.dead_stump_1347)
        dead(trees.deadtree1_large, stumps.dead_stump_1347)
        dead(trees.deadtree4, stumps.dead_stump_6212)
        dead(trees.lightdeadtree1, stumps.dead_stump_1349)
        dead(trees.deadtree2, stumps.dead_stump_1351)
        dead(trees.deadtree2_dark, stumps.dead_stump_1353)
        dead(trees.deadtree3, stumps.dead_stump_1354)
        dead(trees.deadtree2_snowy, stumps.dead_stump_23054)
        dead(trees.deadtree2_swamp, stumps.dead_stump_1352)
        dead(trees.deadtree6, stumps.dead_stump_1358)
        dead(trees.deadtree_burnt, stumps.dead_stump_1353)
        dead(trees.mdaughter_passable_tree, stumps.dead_stump_5905)
        dead(trees.fai_varrock_dead_tree, stumps.dying_stump_3649)
        evergreen(trees.evergreen_vsnowy_large, stumps.evergreen_stump_1355)
        evergreen(trees.evergreen_snowy_large, stumps.evergreen_stump_1355)
        evergreen(trees.evergreen, stumps.regular_stump_1342)
        evergreen(trees.evergreen_large, stumps.evergreen_stump_1355)
        hollow(trees.hollow_tree, stumps.hollow_stump_2310)
        hollow(trees.hollow_tree_big, stumps.hollow_stump_4061)
        jungle(trees.mm_bush_kharazi_jungle_tree1, stumps.jungle_stump_4819)
        jungle(trees.mm_bush_kharazi_jungle_tree2, stumps.jungle_stump_4821)
        magic(trees.magictree, stumps.magic_stump_9713)
        mahogany(trees.prif_mahoganytree, stumps.mahogany_stump_36689)
        mahogany(trees.mahoganytree_update, stumps.mahogany_stump_40761)
        mahogany(trees.mahoganytree, stumps.mahogany_stump_9035)
        maple(trees.mapletree, stumps.maple_stump_9712)
        maple(trees.prif_mapletree, stumps.maple_stump_36682)
        maple(trees.mapletree_update, stumps.maple_stump_40755)
        juniper(trees.mature_juniper_tree, stumps.mature_stump_27500)
        oak(trees.oaktree, stumps.oak_stump_1356)
        oak(trees.tree_oak_default01, stumps.oak_stump_42396)
        redwood(trees.redwoodtree_l, stumps.redwood_stump_29669)
        redwood(trees.redwoodtree_r, stumps.redwood_stump_29671)
        regular(trees.tree, stumps.regular_stump_1342)
        regular(trees.lighttree, stumps.regular_stump_1343)
        regular(trees.tree2, stumps.regular_stump_1342)
        regular(trees.tree3, stumps.regular_stump_1342)
        regular(trees.lighttree2, stumps.regular_stump_1343)
        regular(trees.snowtree1, stumps.evergreen_stump_1355)
        regular(trees.snowtree2, stumps.evergreen_stump_1355)
        regular(trees.snowtree3, stumps.evergreen_stump_1355)
        regular(trees.pest_tree, stumps.regular_stump_1342)
        regular(trees.pest_tree2, stumps.regular_stump_1342)
        regular(trees.prif_tree_normal_1, stumps.regular_stump_36673)
        regular(trees.prif_tree_normal_2, stumps.regular_stump_36675)
        regular(trees.prif_outside_tree_normal_1, stumps.regular_stump_36678)
        regular(trees.prif_outside_tree_normal_2, stumps.regular_stump_36678)
        regular(trees.regicide_tree_large, stumps.regular_stump_3880)
        regular(trees.regicide_tree_large2, stumps.regular_stump_3880)
        regular(trees.regicide_tree_large3, stumps.regular_stump_3880)
        regular(trees.regicide_tree_small, stumps.regular_stump_3884)
        regular(trees.tree_update_1, stumps.regular_stump_40751)
        regular(trees.tree_update_2, stumps.regular_stump_40753)
        regular(trees.tree_normal_default01, stumps.regular_stump_42394)
        regular(trees.avium_tree_1, stumps.regular_stump_51763)
        regular(trees.avium_tree_2, stumps.regular_stump_51765)
        regular(trees.tree4, stumps.regular_stump_1342)
        regular(trees.avium_tree_dark, stumps.regular_stump_53189)
        teak(trees.prif_teaktree, stumps.teak_stump_36687)
        teak(trees.teaktree_update, stumps.teak_stump_40759)
        teak(trees.teaktree, stumps.teak_stump_9037)
        willow(trees.willowtree, stumps.willow_stump_9711)
        willow(trees.willow_tree2, stumps.willow_stump_9471)
        willow(trees.willow_tree3, stumps.willow_stump_9471)
        willow(trees.willow_tree4, stumps.willow_stump_9471)
        yew(trees.yewtree, stumps.yew_stump_9714)
        yew(trees.prif_yewtree, stumps.yew_stump_36684)
        yew(trees.yewtree_update, stumps.yew_stump_40757)
        yew(trees.tree_yew_default01, stumps.yew_stump_42392)
        yew(trees.deadman_yewtree_insurancestall, stumps.yew_stump_9714)
    }

    private fun regular(type: LocType, stump: LocType, logs: ObjType = objs.logs) {
        edit(type) {
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

    private fun dead(type: LocType, stump: LocType) {
        regular(type, stump)
    }

    private fun burnt(type: LocType, stump: LocType) {
        regular(type, stump, objs.charcoal)
    }

    private fun evergreen(type: LocType, stump: LocType) {
        regular(type, stump)
    }

    private fun jungle(type: LocType, stump: LocType) {
        regular(type, stump)
    }

    private fun oak(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun willow(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun teak(type: LocType, stump: LocType) {
        edit(type) {
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
    private fun juniper(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun maple(type: LocType, stump: LocType) {
        edit(type) {
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
    private fun arctic(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun mahogany(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun yew(type: LocType, stump: LocType) {
        edit(type) {
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
    private fun magic(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun redwood(type: LocType, stump: LocType) {
        edit(type) {
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

    private fun hollow(type: LocType, stump: LocType) {
        edit(type) {
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

internal object TreeLocs : LocReferences() {
    val achey_tree = find("achey_tree")
    val arctic_pine = find("arctic_pine")
    val fossil_deadtree_large1 = find("fossil_deadtree_large1")
    val fossil_deadtree_small1 = find("fossil_deadtree_small1")
    val deadtree1 = find("deadtree1")
    val deadtree1_large = find("deadtree1_large")
    val deadtree4 = find("deadtree4")
    val lightdeadtree1 = find("lightdeadtree1")
    val deadtree2 = find("deadtree2")
    val deadtree2_dark = find("deadtree2_dark")
    val deadtree3 = find("deadtree3")
    val deadtree2_snowy = find("deadtree2_snowy")
    val deadtree2_swamp = find("deadtree2_swamp")
    val deadtree6 = find("deadtree6")
    val deadtree_burnt = find("deadtree_burnt")
    val mdaughter_passable_tree = find("mdaughter_passable_tree")
    val fai_varrock_dead_tree = find("fai_varrock_dead_tree")
    val evergreen_vsnowy_large = find("evergreen_vsnowy_large")
    val evergreen_snowy_large = find("evergreen_snowy_large")
    val evergreen = find("evergreen")
    val evergreen_large = find("evergreen_large")
    val hollow_tree = find("hollow_tree")
    val hollow_tree_big = find("hollow_tree_big")
    val mm_bush_kharazi_jungle_tree1 = find("mm_bush_kharazi_jungle_tree1")
    val mm_bush_kharazi_jungle_tree2 = find("mm_bush_kharazi_jungle_tree2")
    val magictree = find("magictree")
    val prif_mahoganytree = find("prif_mahoganytree")
    val mahoganytree_update = find("mahoganytree_update")
    val mahoganytree = find("mahoganytree")
    val mapletree = find("mapletree")
    val prif_mapletree = find("prif_mapletree")
    val mapletree_update = find("mapletree_update")
    val mature_juniper_tree = find("mature_juniper_tree")
    val oaktree = find("oaktree")
    val tree_oak_default01 = find("tree_oak_default01")
    val redwoodtree_l = find("redwoodtree_l")
    val redwoodtree_r = find("redwoodtree_r")
    val tree = find("tree")
    val lighttree = find("lighttree")
    val tree2 = find("tree2")
    val tree3 = find("tree3")
    val lighttree2 = find("lighttree2")
    val snowtree1 = find("snowtree1")
    val snowtree2 = find("snowtree2")
    val snowtree3 = find("snowtree3")
    val pest_tree = find("pest_tree")
    val pest_tree2 = find("pest_tree2")
    val prif_tree_normal_1 = find("prif_tree_normal_1")
    val prif_tree_normal_2 = find("prif_tree_normal_2")
    val prif_outside_tree_normal_1 = find("prif_outside_tree_normal_1")
    val prif_outside_tree_normal_2 = find("prif_outside_tree_normal_2")
    val regicide_tree_large = find("regicide_tree_large")
    val regicide_tree_large2 = find("regicide_tree_large2")
    val regicide_tree_large3 = find("regicide_tree_large3")
    val regicide_tree_small = find("regicide_tree_small")
    val tree_update_1 = find("tree_update_1")
    val tree_update_2 = find("tree_update_2")
    val tree_normal_default01 = find("tree_normal_default01")
    val avium_tree_1 = find("avium_tree_1")
    val avium_tree_2 = find("avium_tree_2")
    val tree4 = find("tree4")
    val avium_tree_dark = find("avium_tree_dark")
    val prif_teaktree = find("prif_teaktree")
    val teaktree_update = find("teaktree_update")
    val teaktree = find("teaktree")
    val willowtree = find("willowtree")
    val willow_tree2 = find("willow_tree2")
    val willow_tree3 = find("willow_tree3")
    val willow_tree4 = find("willow_tree4")
    val yewtree = find("yewtree")
    val prif_yewtree = find("prif_yewtree")
    val yewtree_update = find("yewtree_update")
    val tree_yew_default01 = find("tree_yew_default01")
    val deadman_yewtree_insurancestall = find("deadman_yewtree_insurancestall")
}

internal object StumpLocs : LocReferences() {
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
