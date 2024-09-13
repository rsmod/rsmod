package org.rsmod.api.player.output

import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.obj.ObjType

public object ClientScripts {
    public fun Player.camForceAngle(rate: Int, rate2: Int): Unit = runClientScript(143, rate, rate2)

    /**
     * Switches, or opens, the toplevel side tab. Values for [side] can be found in
     * [org.rsmod.api.config.Constants] prefixed with `toplevel_`. (i.e., `toplevel_attack`)
     */
    public fun Player.toplevelSidebuttonSwitch(side: Int): Unit = runClientScript(915, side)

    /** @param joinedChoices Dialogue choices must be split by the `|` character. */
    public fun Player.chatboxMultiInit(title: String, joinedChoices: String): Unit =
        runClientScript(58, title, joinedChoices)

    public fun Player.mesLayerMode7(title: String): Unit = runClientScript(108, title)

    public fun Player.topLevelMainModalOpen(colour: Int = -1, transparency: Int = -1): Unit =
        runClientScript(2524, colour, transparency)

    public fun Player.topLevelMainModalBackground(colour: Int = -1, transparency: Int = -1): Unit =
        runClientScript(917, colour, transparency)

    public fun Player.topLevelChatboxResetBackground(): Unit = runClientScript(2379)

    public fun Player.ifSetTextAlign(
        target: ComponentType,
        alignH: Int,
        alignV: Int,
        lineHeight: Int,
    ): Unit = runClientScript(600, alignH, alignV, lineHeight, target.packed)

    public fun Player.interfaceInvInit(
        inv: Inventory,
        target: ComponentType,
        objRowCount: Int,
        objColCount: Int,
        op1: String? = null,
        op2: String? = null,
        op3: String? = null,
        op4: String? = null,
        op5: String? = null,
        dragType: Int = 0,
        dragComponent: ComponentType? = null,
    ): Unit =
        runClientScript(
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

    public fun Player.shopMainInit(
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
        runClientScript(
            1074,
            shopInv.id,
            title,
            customBuyAmountObj?.id ?: -1,
            customBuyAmount ?: 0,
            if (enableBuy50) 1 else 0,
        )
    }
}
