package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.RunClientScript
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid

public fun Player.runClientScript(id: Int, vararg args: Any) {
    runClientScript(id, args.toList())
}

public fun Player.runClientScript(id: Int, args: List<Any>) {
    client.write(RunClientScript(id, args))
}

public object ClientScripts {
    public fun playerMember(player: Player, member: Boolean = player.members): Unit =
        player.runClientScript(828, if (member) 1 else 0)

    public fun ccDeleteAll(player: Player, component: ComponentType): Unit =
        player.runClientScript(2249, component.packed)

    public fun highlightingOff(player: Player): Unit = player.runClientScript(5485)

    public fun highlightingOn(player: Player): Unit = player.runClientScript(5487)

    public fun camForceAngle(player: Player, rate: Int, rate2: Int): Unit =
        player.runClientScript(143, rate, rate2)

    /** @param joinedChoices Menu list choices must be split by the `|` character. */
    public fun menu(player: Player, title: String, joinedChoices: String, hotkeys: Boolean): Unit =
        player.runClientScript(217, title, joinedChoices, if (hotkeys) 1 else 0)

    /**
     * Switches, or opens, the toplevel side tab. Values for [side] can be found in
     * [org.rsmod.api.config.Constants] prefixed with `toplevel_`. (i.e., `toplevel_attack`)
     */
    public fun toplevelSidebuttonSwitch(player: Player, side: Int): Unit =
        player.runClientScript(915, side)

    /** @param joinedChoices Dialogue choices must be split by the `|` character. */
    public fun chatboxMultiInit(player: Player, title: String, joinedChoices: String): Unit =
        player.runClientScript(58, title, joinedChoices)

    /**
     * Values for [layerMode] can be found in [org.rsmod.api.config.Constants] prefixed with
     * `meslayer_mode`. (i.e., `meslayer_mode_countdialog`)
     */
    public fun mesLayerClose(player: Player, layerMode: Int): Unit =
        player.runClientScript(101, layerMode)

    public fun mesLayerMode7(player: Player, title: String): Unit =
        player.runClientScript(108, title)

    public fun mesLayerMode14(
        player: Player,
        title: String,
        stockMarketRestriction: Boolean = true,
        enumRestriction: EnumType<ObjType, Boolean>? = null,
        showLastSearched: Boolean = false,
    ): Unit =
        player.runClientScript(
            750,
            title,
            if (stockMarketRestriction) 1 else 0,
            enumRestriction?.id ?: -1,
            if (showLastSearched) 1 else 0,
        )

    public fun chatDefaultRestoreInput(player: Player): Unit = player.runClientScript(2158)

    public fun topLevelMainModalOpen(
        player: Player,
        colour: Int = -1,
        transparency: Int = -1,
    ): Unit = player.runClientScript(2524, colour, transparency)

    public fun topLevelMainModalBackground(
        player: Player,
        colour: Int = -1,
        transparency: Int = -1,
    ): Unit = player.runClientScript(917, colour, transparency)

    public fun topLevelChatboxResetBackground(player: Player): Unit = player.runClientScript(2379)

    public fun ifSetTextAlign(
        player: Player,
        target: ComponentType,
        alignH: Int,
        alignV: Int,
        lineHeight: Int,
    ): Unit = player.runClientScript(600, alignH, alignV, lineHeight, target.packed)

    public fun objboxSetButtons(player: Player, text: String): Unit =
        player.runClientScript(2868, text)

    public fun interfaceInvInit(
        player: Player,
        inv: Inventory,
        target: ComponentType,
        objRowCount: Int,
        objColCount: Int,
        dragType: Int = 0,
        dragComponent: ComponentType? = null,
        op1: String? = null,
        op2: String? = null,
        op3: String? = null,
        op4: String? = null,
        op5: String? = null,
    ): Unit =
        player.runClientScript(
            149,
            target.packed,
            inv.type.id,
            objRowCount,
            objColCount,
            dragType,
            dragComponent?.packed ?: -1,
            op1 ?: "",
            op2 ?: "",
            op3 ?: "",
            op4 ?: "",
            op5 ?: "",
        )

    public fun shopMainInit(
        player: Player,
        shopInv: InvType,
        title: String,
        enableBuy50: Boolean = true,
        customBuyAmountObj: ObjType? = null,
        customBuyAmount: Int? = null,
    ) {
        check(customBuyAmount == null || customBuyAmountObj != null) {
            "`customBuyAmount` must be set if `customBuyAmountObj` is set."
        }
        check(customBuyAmountObj == null || customBuyAmount != null) {
            "`customBuyAmountObj` must be set if `customBuyAmount` is set."
        }
        player.runClientScript(
            1074,
            shopInv.id,
            title,
            customBuyAmountObj?.id ?: -1,
            customBuyAmount ?: 0,
            if (enableBuy50) 1 else 0,
        )
    }

    public fun examineItem(
        player: Player,
        obj: Int,
        count: Int,
        desc: String,
        market: Boolean,
        marketPrice: Int,
        alchable: Boolean,
        highAlch: Int,
        lowAlch: Int,
    ) {
        player.runClientScript(
            6003,
            obj,
            count,
            desc,
            if (market) 1 else 0,
            marketPrice,
            if (alchable) 1 else 0,
            highAlch,
            lowAlch,
        )
    }

    /**
     * @param timer the overlay timer type, see [org.rsmod.api.config.Constants] for known values.
     *   (prefixed with `overlay_timer_`)
     * @param isDespawnTimer `true` if the overlay represents a despawn timer as opposed to respawn
     *   timer. (i.e., hunter trap despawn vs tree respawn)
     */
    public fun addOverlayTimerLoc(
        player: Player,
        coords: CoordGrid,
        loc: LocType,
        shape: LocShape,
        timer: Int,
        ticks: Int,
        colour: Int,
        isDespawnTimer: Boolean = false,
    ) {
        player.runClientScript(
            5474,
            coords.packed,
            loc.id,
            shape.id,
            timer,
            ticks,
            colour,
            if (isDespawnTimer) 1 else 0,
        )
    }

    public fun confirmDestroyInit(
        player: Player,
        header: String,
        text: String,
        obj: Int,
        count: Int,
    ): Unit = player.runClientScript(814, obj, count, header, text)

    public fun pvpIconsComLevelRange(player: Player, combatLevel: Int): Unit =
        player.runClientScript(5224, combatLevel)

    public fun statGroupTooltip(
        player: Player,
        tooltip: ComponentType,
        container: ComponentType,
        text: String,
    ): Unit = player.runClientScript(7065, tooltip.packed, container.packed, text)

    public fun tooltip(
        player: Player,
        text: String,
        container: ComponentType,
        tooltip: ComponentType,
    ): Unit = player.runClientScript(1495, text, container.packed, tooltip.packed)

    public fun confirmOverlayInit(
        player: Player,
        target: ComponentType,
        title: String,
        text: String,
        cancel: String,
        confirm: String,
    ): Unit = player.runClientScript(4212, "$title|$text|$cancel|$confirm", target.packed)
}
