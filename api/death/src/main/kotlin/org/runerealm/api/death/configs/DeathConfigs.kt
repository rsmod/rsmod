@file:Suppress("SpellCheckingInspection")

package org.runerealm.api.death.configs

import org.runerealm.api.type.builders.param.ParamBuilder
import org.runerealm.api.type.builders.varp.VarpBuilder
import org.runerealm.api.type.editors.inv.InvEditor
import org.runerealm.api.type.editors.npc.NpcEditor
import org.runerealm.api.type.refs.inv.InvReferences
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.param.ParamReferences
import org.runerealm.api.type.refs.timer.TimerReferences
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.game.type.inv.InvScope
import org.runerealm.game.type.inv.InvType
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.param.ParamType
import org.runerealm.game.type.timer.TimerType
import org.runerealm.game.type.varp.VarpType

public typealias death_params = DeathParams

public typealias death_invs = DeathInvs

public typealias death_varps = DeathVarps

public typealias death_timers = DeathTimers

public typealias death_npcs = DeathNpcs

public typealias death_locs = DeathLocs

/**
 * Server-side obj params that describe what happens to an obj on an unsafe death. The cache has no
 * death params of its own, so these are set per obj in [DeathObjEditor].
 */
public object DeathParams : ParamReferences() {
    /** Always kept on death (PvM and PvP), without using up a kept-item slot. */
    public val keep: ParamType<Boolean> = find<Boolean>("death_keep")

    /** Always deleted on an unsafe death, even when it would otherwise be kept. */
    public val delete: ParamType<Boolean> = find<Boolean>("death_delete")

    /** Kept on a PvM death without using up a kept-item slot (e.g. Trouver-locked items). */
    public val pvm_keep: ParamType<Boolean> = find<Boolean>("death_pvm_keep")

    /**
     * The `(broken)` form the victim keeps in their inventory after a PvP death. The killer gets
     * [pvp_repair_cost] coins.
     */
    public val pvp_broken: ParamType<ObjType> = find<ObjType>("death_pvp_broken")

    /** Coins the killer receives for a [pvp_broken] obj. */
    public val pvp_repair_cost: ParamType<Int> = find<Int>("death_pvp_repair_cost")

    /**
     * Unlocked [pvp_broken] objs that are lost completely, rather than broken, on a PvP death above
     * level 20 Wilderness. The killer still gets the repair cost.
     */
    public val lost_above_20: ParamType<Boolean> = find<Boolean>("death_lost_above_20")

    /** Untradeable objs that turn into coins (worth the obj's value) for the killer. */
    public val pvp_coins: ParamType<Boolean> = find<Boolean>("death_pvp_coins")

    /** The obj dropped to the killer in place of this one (e.g. Barrows → broken). */
    public val pvp_drop_as: ParamType<ObjType> = find<ObjType>("death_pvp_drop_as")
}

internal object DeathParamBuilder : ParamBuilder() {
    init {
        build<Boolean>("death_keep") { default = false }
        build<Boolean>("death_delete") { default = false }
        build<Boolean>("death_pvm_keep") { default = false }
        build<ObjType>("death_pvp_broken")
        build<Int>("death_pvp_repair_cost") { default = 0 }
        build<Boolean>("death_lost_above_20") { default = false }
        build<Boolean>("death_pvp_coins") { default = false }
        build<ObjType>("death_pvp_drop_as")
    }
}

public object DeathInvs : InvReferences() {
    /** The items held in the player's gravestone. */
    public val gravestone: InvType = find("gravestone")

    /** Items waiting to be reclaimed from Death's Office. */
    public val death_office: InvType = find("death_permanent")
}

internal object DeathInvEditor : InvEditor() {
    init {
        edit(death_invs.gravestone) {
            scope = InvScope.Perm
            size = GRAVESTONE_CAPACITY
        }
        edit(death_invs.death_office) {
            scope = InvScope.Perm
            size = GRAVESTONE_CAPACITY
        }
    }

    // Graves and Death's Office both hold up to 120 items or stacks, per the wiki.
    private const val GRAVESTONE_CAPACITY = 120
}

public object DeathVarps : VarpReferences() {
    /** Where the player's gravestone stands (the cache varp the world map reads). */
    public val gravestone_coords: VarpType = find("gravestone_creation_death_coord")

    /** Game ticks left on the gravestone; only counts down while the owner is logged in. */
    public val gravestone_ticks_left: VarpType = find("gravestone_ticks_left")
}

internal object DeathVarpBuilder : VarpBuilder() {
    init {
        build("gravestone_ticks_left")
    }
}

public object DeathTimers : TimerReferences() {
    public val gravestone: TimerType = find("gravestone")
}

public object DeathNpcs : NpcReferences() {
    public val gravestone: NpcType = find("gravestone_default")
    public val death: NpcType = find("death_office_death")
}

public object DeathLocs : LocReferences() {
    public val access_browngrave: LocType = find("death_office_access_browngrave")
    public val access_coffin: LocType = find("death_office_access_coffin")
    public val access_whitecoffin: LocType = find("death_office_access_whitecoffin")
    public val exit_portal: LocType = find("death_office_exitportal")
}

internal object DeathNpcEditor : NpcEditor() {
    init {
        edit(death_npcs.gravestone) {
            defaultMode = none
            moveRestrict = nomove
            wanderRange = 0
        }
        edit(death_npcs.death) {
            defaultMode = none
            moveRestrict = nomove
            wanderRange = 0
            respawnDir = west
        }
    }
}
