package org.runerealm.content.other.ironman.configs

import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.varp.VarpReferences

typealias ironman_npcs = IronmanNpcs

typealias ironman_objs = IronmanObjs

typealias ironman_interfaces = IronmanInterfaces

typealias ironman_components = IronmanComponents

typealias ironman_varps = IronmanVarps

object IronmanNpcs : NpcReferences() {
    val adam = find("ironman_tutor_1")
}

object IronmanObjs : ObjReferences() {
    val ironman_helm = find("ironman_helm")
    val ironman_platebody = find("ironman_platebody")
    val ironman_platelegs = find("ironman_platelegs")
    val ultimate_ironman_helm = find("ultimate_ironman_helm")
    val ultimate_ironman_platebody = find("ultimate_ironman_platebody")
    val ultimate_ironman_platelegs = find("ultimate_ironman_platelegs")
    val hardcore_ironman_helm = find("hardcore_ironman_helm")
    val hardcore_ironman_platebody = find("hardcore_ironman_platebody")
    val hardcore_ironman_platelegs = find("hardcore_ironman_platelegs")
}

object IronmanInterfaces : InterfaceReferences() {
    val ironman_setup = find("ironman_setup")
}

object IronmanComponents : ComponentReferences() {
    val none_button = find("ironman_setup:none_button")
    val im_button = find("ironman_setup:im_button")
    val uim_button = find("ironman_setup:uim_button")
    val hcim_button = find("ironman_setup:hcim_button")
    val gim_button = find("ironman_setup:gim_button")
    val hcgim_button = find("ironman_setup:hcgim_button")
    val ugim_button = find("ironman_setup:ugim_button")
    val downgrade_short = find("ironman_setup:downgrade_short")
    val downgrade_long = find("ironman_setup:downgrade_long")
    val downgrade_cancel = find("ironman_setup:downgrade_cancel")
}

/** Generic interface varps read by the `ironman_setup` client scripts. */
object IronmanVarps : VarpReferences() {
    /** Days remaining on a pending downgrade. */
    val days_remaining = find("if3")
    /** `1` offers the 90-day downgrade protection (permanent lock-in was removed in 2024). */
    val long_protection_available = find("if4")
    /** `1` disables every option ("not available on this game world"). */
    val setup_unavailable = find("if5")
    /** `1` allows switching freely between modes, as with Paul on Tutorial Island. */
    val free_choice = find("if6")
}
