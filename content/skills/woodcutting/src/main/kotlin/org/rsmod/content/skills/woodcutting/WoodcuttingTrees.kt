package org.rsmod.content.skills.woodcutting

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.loc.LocEditor
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType

internal object WoodcuttingTrees : LocEditor() {
    init {
        regular("achey_tree_2023", WoodcuttingStumps.achey_stump_3371, objs.achey_tree_logs)
        arctic("arctic_pine_tree_3037", WoodcuttingStumps.arctic_pine_stump_21274)
        burnt("burnt_tree_30852", WoodcuttingStumps.burnt_stump_30855)
        burnt("burnt_tree_30854", WoodcuttingStumps.burnt_stump_30856)
        dead("dead_tree_1282", WoodcuttingStumps.dead_stump_1347)
        dead("dead_tree_1283", WoodcuttingStumps.dead_stump_1347)
        dead("dead_tree_1284", WoodcuttingStumps.dead_stump_6212)
        dead("dead_tree_1285", WoodcuttingStumps.dead_stump_1349)
        dead("dead_tree_1286", WoodcuttingStumps.dead_stump_1351)
        dead("dead_tree_1289", WoodcuttingStumps.dead_stump_1353)
        dead("dead_tree_1290", WoodcuttingStumps.dead_stump_1354)
        dead("dead_tree_1291", WoodcuttingStumps.dead_stump_23054)
        dead("dead_tree_1365", WoodcuttingStumps.dead_stump_1352)
        dead("dead_tree_1383", WoodcuttingStumps.dead_stump_1358)
        dead("dead_tree_1384", WoodcuttingStumps.dead_stump_1353)
        dead("dead_tree_5904", WoodcuttingStumps.dead_stump_5905)
        dead("dying_tree_3648", WoodcuttingStumps.dying_stump_3649)
        evergreen("evergreen_tree_1318", WoodcuttingStumps.evergreen_stump_1355)
        evergreen("evergreen_tree_1319", WoodcuttingStumps.evergreen_stump_1355)
        evergreen("evergreen_tree_2091", WoodcuttingStumps.regular_stump_1342)
        evergreen("evergreen_tree_2092", WoodcuttingStumps.evergreen_stump_1355)
        hollow("hollow_tree_10821", WoodcuttingStumps.hollow_stump_2310)
        hollow("hollow_tree_10830", WoodcuttingStumps.hollow_stump_4061)
        jungle("jungle_tree_4818", WoodcuttingStumps.jungle_stump_4819)
        jungle("jungle_tree_4820", WoodcuttingStumps.jungle_stump_4821)
        magic("magic_tree_10834", WoodcuttingStumps.magic_stump_9713)
        mahogany("mahogany_tree_36688", WoodcuttingStumps.mahogany_stump_36689)
        mahogany("mahogany_tree_40760", WoodcuttingStumps.mahogany_stump_40761)
        mahogany("mahogany_tree_9034", WoodcuttingStumps.mahogany_stump_9035)
        maple("maple_tree_10832", WoodcuttingStumps.maple_stump_9712)
        maple("maple_tree_36681", WoodcuttingStumps.maple_stump_36682)
        maple("maple_tree_40754", WoodcuttingStumps.maple_stump_40755)
        juniper("mature_juniper_tree_27499", WoodcuttingStumps.mature_stump_27500)
        oak("oak_tree_10820", WoodcuttingStumps.oak_stump_1356)
        oak("oak_tree_42395", WoodcuttingStumps.oak_stump_42396)
        redwood("redwood_tree_29668", WoodcuttingStumps.redwood_stump_29669)
        redwood("redwood_tree_29670", WoodcuttingStumps.redwood_stump_29671)
        regular("regular_tree_1276", WoodcuttingStumps.regular_stump_1342)
        regular("regular_tree_1277", WoodcuttingStumps.regular_stump_1343)
        regular("regular_tree_1278", WoodcuttingStumps.regular_stump_1342)
        regular("regular_tree_1279", WoodcuttingStumps.regular_stump_1342)
        regular("regular_tree_1280", WoodcuttingStumps.regular_stump_1343)
        regular("regular_tree_1330", WoodcuttingStumps.evergreen_stump_1355)
        regular("regular_tree_1331", WoodcuttingStumps.evergreen_stump_1355)
        regular("regular_tree_1332", WoodcuttingStumps.evergreen_stump_1355)
        regular("regular_tree_14308", WoodcuttingStumps.regular_stump_1342)
        regular("regular_tree_14309", WoodcuttingStumps.regular_stump_1342)
        regular("regular_tree_36672", WoodcuttingStumps.regular_stump_36673)
        regular("regular_tree_36674", WoodcuttingStumps.regular_stump_36675)
        regular("regular_tree_36677", WoodcuttingStumps.regular_stump_36678)
        regular("regular_tree_36679", WoodcuttingStumps.regular_stump_36678)
        regular("regular_tree_3879", WoodcuttingStumps.regular_stump_3880)
        regular("regular_tree_3881", WoodcuttingStumps.regular_stump_3880)
        regular("regular_tree_3882", WoodcuttingStumps.regular_stump_3880)
        regular("regular_tree_3883", WoodcuttingStumps.regular_stump_3884)
        regular("regular_tree_40750", WoodcuttingStumps.regular_stump_40751)
        regular("regular_tree_40752", WoodcuttingStumps.regular_stump_40753)
        regular("regular_tree_42393", WoodcuttingStumps.regular_stump_42394)
        regular("regular_tree_51762", WoodcuttingStumps.regular_stump_51763)
        regular("regular_tree_51764", WoodcuttingStumps.regular_stump_51765)
        regular("regular_tree_52823", WoodcuttingStumps.regular_stump_1342)
        regular("regular_tree_53188", WoodcuttingStumps.regular_stump_53189)
        teak("teak_tree_36686", WoodcuttingStumps.teak_stump_36687)
        teak("teak_tree_40758", WoodcuttingStumps.teak_stump_40759)
        teak("teak_tree_9036", WoodcuttingStumps.teak_stump_9037)
        willow("willow_tree_10819", WoodcuttingStumps.willow_stump_9711)
        willow("willow_tree_10829", WoodcuttingStumps.willow_stump_9471)
        willow("willow_tree_10831", WoodcuttingStumps.willow_stump_9471)
        willow("willow_tree_10833", WoodcuttingStumps.willow_stump_9471)
        yew("yew_tree_10822", WoodcuttingStumps.yew_stump_9714)
        yew("yew_tree_36683", WoodcuttingStumps.yew_stump_36684)
        yew("yew_tree_40756", WoodcuttingStumps.yew_stump_40757)
        yew("yew_tree_42391", WoodcuttingStumps.yew_stump_42392)
        yew("yew_tree_multi_10828", WoodcuttingStumps.yew_stump_9714)
    }

    private fun regular(internalName: String, stump: LocType, logs: ObjType = objs.logs) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.deplete_chance] = 0
            param[params.respawn_time] = 15
            param[params.skill_levelreq] = 1
            param[params.skill_xp] = PlayerStatMap.toFineXP(25.0).toInt()
            param[params.skill_productitem] = logs
            param[params.next_loc_stage] = stump
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
            param[params.skill_levelreq] = 15
            param[params.skill_xp] = PlayerStatMap.toFineXP(37.5).toInt()
            param[params.skill_productitem] = objs.oak_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun willow(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 50
            param[params.respawn_time] = 15
            param[params.skill_levelreq] = 30
            param[params.skill_xp] = PlayerStatMap.toFineXP(67.5).toInt()
            param[params.skill_productitem] = objs.willow_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun teak(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 50
            param[params.respawn_time] = 16
            param[params.skill_levelreq] = 35
            param[params.skill_xp] = PlayerStatMap.toFineXP(85.0).toInt()
            param[params.skill_productitem] = objs.teak_logs
            param[params.next_loc_stage] = stump
        }
    }

    @Suppress("SameParameterValue")
    private fun juniper(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.deplete_chance] = 16
            param[params.respawn_time] = 15
            param[params.skill_levelreq] = 42
            param[params.skill_xp] = PlayerStatMap.toFineXP(35.0).toInt()
            param[params.skill_productitem] = objs.juniper_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun maple(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 100
            param[params.respawn_time] = 60
            param[params.skill_levelreq] = 45
            param[params.skill_xp] = PlayerStatMap.toFineXP(100.0).toInt()
            param[params.skill_productitem] = objs.maple_logs
            param[params.next_loc_stage] = stump
        }
    }

    @Suppress("SameParameterValue")
    private fun arctic(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 140
            param[params.respawn_time] = 15
            param[params.skill_levelreq] = 54
            param[params.skill_xp] = PlayerStatMap.toFineXP(40.0).toInt()
            param[params.skill_productitem] = objs.arctic_pine_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun mahogany(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 100
            param[params.respawn_time] = 15
            param[params.skill_levelreq] = 50
            param[params.skill_xp] = PlayerStatMap.toFineXP(125.0).toInt()
            param[params.skill_productitem] = objs.mahogany_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun yew(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 190
            param[params.respawn_time] = 100
            param[params.skill_levelreq] = 60
            param[params.skill_xp] = PlayerStatMap.toFineXP(175.0).toInt()
            param[params.skill_productitem] = objs.yew_logs
            param[params.next_loc_stage] = stump
        }
    }

    @Suppress("SameParameterValue")
    private fun magic(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 390
            param[params.respawn_time] = 200
            param[params.skill_levelreq] = 75
            param[params.skill_xp] = PlayerStatMap.toFineXP(250.0).toInt()
            param[params.skill_productitem] = objs.magic_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun redwood(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 440
            param[params.respawn_time] = 200
            param[params.skill_levelreq] = 90
            param[params.skill_xp] = PlayerStatMap.toFineXP(380.0).toInt()
            param[params.skill_productitem] = objs.redwood_logs
            param[params.next_loc_stage] = stump
        }
    }

    private fun hollow(internalName: String, stump: LocType) {
        edit(internalName) {
            contentGroup = content.tree
            param[params.despawn_time] = 60
            param[params.respawn_time] = 43
            param[params.skill_levelreq] = 45
            param[params.skill_xp] = PlayerStatMap.toFineXP(82.5).toInt()
            param[params.skill_productitem] = objs.bark
            param[params.next_loc_stage] = stump
        }
    }
}

internal object WoodcuttingStumps : LocReferences() {
    val arctic_pine_stump_21274 = find("arctic_pine_tree_stump_21274")
    val achey_stump_3371 = find("achey_tree_stump_3371")
    val burnt_stump_30855 = find("burnt_tree_stump_30855")
    val burnt_stump_30856 = find("burnt_tree_stump_30856")
    val dead_stump_1347 = find("dead_tree_stump_1347")
    val dead_stump_1349 = find("dead_tree_stump_1349")
    val dead_stump_1351 = find("dead_tree_stump_1351")
    val dead_stump_1352 = find("dead_tree_stump_1352")
    val dead_stump_1353 = find("dead_tree_stump_1353")
    val dead_stump_1354 = find("dead_tree_stump_1354")
    val dead_stump_1358 = find("dead_tree_stump_1358")
    val dead_stump_23054 = find("dead_tree_stump_23054")
    val dead_stump_5905 = find("dead_tree_stump_5905")
    val dead_stump_6212 = find("dead_tree_stump_6212")
    val dying_stump_3649 = find("dying_tree_stump_3649")
    val evergreen_stump_1355 = find("evergreen_tree_stump_1355")
    val hollow_stump_2310 = find("hollow_tree_stump_2310")
    val hollow_stump_4061 = find("hollow_tree_stump_4061")
    val jungle_stump_4819 = find("jungle_tree_stump_4819")
    val jungle_stump_4821 = find("jungle_tree_stump_4821")
    val magic_stump_9713 = find("magic_tree_stump_9713")
    val mahogany_stump_36689 = find("mahogany_tree_stump_36689")
    val mahogany_stump_40761 = find("mahogany_tree_stump_40761")
    val mahogany_stump_9035 = find("mahogany_tree_stump_9035")
    val maple_stump_36682 = find("maple_tree_stump_36682")
    val maple_stump_40755 = find("maple_tree_stump_40755")
    val maple_stump_9712 = find("maple_tree_stump_9712")
    val mature_stump_27500 = find("mature_tree_stump_27500")
    val oak_stump_1356 = find("oak_tree_stump_1356")
    val oak_stump_42396 = find("oak_tree_stump_42396")
    val redwood_stump_29669 = find("redwood_tree_stump_29669")
    val redwood_stump_29671 = find("redwood_tree_stump_29671")
    val regular_stump_1342 = find("regular_tree_stump_1342")
    val regular_stump_1343 = find("regular_tree_stump_1343")
    val regular_stump_36673 = find("regular_tree_stump_36673")
    val regular_stump_36675 = find("regular_tree_stump_36675")
    val regular_stump_36678 = find("regular_tree_stump_36678")
    val regular_stump_3880 = find("regular_tree_stump_3880")
    val regular_stump_3884 = find("regular_tree_stump_3884")
    val regular_stump_40751 = find("regular_tree_stump_40751")
    val regular_stump_40753 = find("regular_tree_stump_40753")
    val regular_stump_42394 = find("regular_tree_stump_42394")
    val regular_stump_51763 = find("regular_tree_stump_51763")
    val regular_stump_51765 = find("regular_tree_stump_51765")
    val regular_stump_53189 = find("regular_tree_stump_53189")
    val teak_stump_36687 = find("teak_tree_stump_36687")
    val teak_stump_40759 = find("teak_tree_stump_40759")
    val teak_stump_9037 = find("teak_tree_stump_9037")
    val willow_stump_9471 = find("willow_tree_stump_9471")
    val willow_stump_9711 = find("willow_tree_stump_9711")
    val yew_stump_36684 = find("yew_tree_stump_36684")
    val yew_stump_40757 = find("yew_tree_stump_40757")
    val yew_stump_42392 = find("yew_tree_stump_42392")
    val yew_stump_9714 = find("yew_tree_stump_9714")
}
