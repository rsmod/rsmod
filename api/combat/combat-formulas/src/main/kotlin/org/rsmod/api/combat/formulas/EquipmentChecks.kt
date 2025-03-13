package org.rsmod.api.combat.formulas

import org.rsmod.api.config.refs.objs
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isAnyType
import org.rsmod.game.obj.isType

internal object EquipmentChecks {
    fun isSoulreaperAxe(obj: InvObj?): Boolean = obj.isType(objs.soulreaper_axe)

    fun isObsidianSet(helm: InvObj?, top: InvObj?, legs: InvObj?): Boolean =
        helm.isType(objs.obsidian_helmet) &&
            top.isType(objs.obsidian_platebody) &&
            legs.isType(objs.obsidian_platelegs)

    fun isVoidMeleeHelm(obj: InvObj?): Boolean =
        obj.isAnyType(
            objs.void_melee_helm,
            objs.void_melee_helm_l,
            objs.void_melee_helm_or,
            objs.void_melee_helm_l_or,
        )

    fun isVoidTop(obj: InvObj?): Boolean = isRegularVoidTop(obj) || isEliteVoidTop(obj)

    fun isRegularVoidTop(obj: InvObj?): Boolean =
        obj.isAnyType(objs.void_top, objs.void_top_l, objs.void_top_or, objs.void_top_l_or)

    fun isEliteVoidTop(obj: InvObj?): Boolean =
        obj.isAnyType(
            objs.elite_void_top,
            objs.elite_void_top_l,
            objs.elite_void_top_or,
            objs.elite_void_top_l_or,
        )

    fun isVoidRobe(obj: InvObj?): Boolean = isRegularVoidRobe(obj) || isEliteVoidRobe(obj)

    fun isRegularVoidRobe(obj: InvObj?): Boolean =
        obj.isAnyType(objs.void_robe, objs.void_robe_l, objs.void_robe_or, objs.void_robe_l_or)

    fun isEliteVoidRobe(obj: InvObj?): Boolean =
        obj.isAnyType(
            objs.elite_void_robe,
            objs.elite_void_robe_l,
            objs.elite_void_robe_or,
            objs.elite_void_robe_l_or,
        )

    fun isVoidGloves(obj: InvObj?): Boolean =
        obj.isAnyType(
            objs.void_gloves,
            objs.void_gloves_l,
            objs.void_gloves_or,
            objs.void_gloves_l_or,
        )

    fun isDharokSet(helm: InvObj?, top: InvObj?, legs: InvObj?, weapon: InvObj?): Boolean =
        helm.isAnyType(
            objs.dharoks_helm_100,
            objs.dharoks_helm_75,
            objs.dharoks_helm_50,
            objs.dharoks_helm_25,
        ) &&
            top.isAnyType(
                objs.dharoks_platebody_100,
                objs.dharoks_platebody_75,
                objs.dharoks_platebody_50,
                objs.dharoks_platebody_25,
            ) &&
            legs.isAnyType(
                objs.dharoks_platelegs_100,
                objs.dharoks_platelegs_75,
                objs.dharoks_platelegs_50,
                objs.dharoks_platelegs_25,
            ) &&
            weapon.isAnyType(
                objs.dharoks_greataxe_100,
                objs.dharoks_greataxe_75,
                objs.dharoks_greataxe_50,
                objs.dharoks_greataxe_25,
            )

    fun isJusticiarSet(helm: InvObj?, top: InvObj?, legs: InvObj?): Boolean =
        helm.isType(objs.justiciar_faceguard) &&
            top.isType(objs.justiciar_chestguard) &&
            legs.isType(objs.justiciar_legguards)
}
