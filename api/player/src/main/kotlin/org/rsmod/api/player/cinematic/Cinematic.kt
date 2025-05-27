package org.rsmod.api.player.cinematic

import net.rsprot.protocol.game.outgoing.misc.client.HideLocOps
import net.rsprot.protocol.game.outgoing.misc.client.HideNpcOps
import net.rsprot.protocol.game.outgoing.misc.client.HideObjOps
import net.rsprot.protocol.game.outgoing.misc.client.MinimapToggle
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.output.ClientScripts.ccDeleteAll
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.ui.ifCloseOverlay
import org.rsmod.api.player.ui.ifOpenFullOverlay
import org.rsmod.api.player.ui.ifOpenSub
import org.rsmod.api.player.ui.ifSetHide
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.enumVarBit
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfSubType

public object Cinematic {
    private var Player.camMode by enumVarBit<CameraMode>(varbits.fov_clamp)
    private var Player.minimap by enumVarBit<MinimapState>(varbits.minimap_state)
    private var Player.hideTop by boolVarBit(varbits.cutscene_status)
    private var Player.hideHud by boolVarBit(varbits.gravestone_tli_hide)
    private var Player.acceptAid by boolVarBit(varbits.option_acceptaid)
    private var Player.acceptAidRestore by boolVarBit(varbits.accept_aid_restore)

    public fun setCameraMode(player: Player, mode: CameraMode) {
        player.camMode = mode
    }

    public fun setHideToplevel(player: Player, hide: Boolean) {
        player.hideTop = hide
    }

    public fun clearHealthHud(player: Player) {
        player.ifSetHide(components.hp_hud_hp, hide = true)
        ccDeleteAll(player, components.hp_hud_container)
    }

    public fun setHideHealthHud(player: Player, hide: Boolean) {
        player.hideHud = hide
    }

    public fun disableAcceptAid(player: Player) {
        player.acceptAidRestore = player.acceptAid
        player.acceptAid = false
    }

    public fun restoreAcceptAid(player: Player) {
        player.acceptAid = player.acceptAidRestore
        player.acceptAidRestore = false
    }

    public fun setMinimapState(player: Player, state: MinimapState) {
        player.minimap = state
        player.client.write(MinimapToggle(state.varValue))
    }

    public fun syncMinimapState(player: Player) {
        val state = player.minimap
        player.client.write(MinimapToggle(state.varValue))
    }

    public fun setHideEntityOps(player: Player, hide: Boolean) {
        player.client.write(HideNpcOps(hide))
        player.client.write(HideLocOps(hide))
        player.client.write(HideObjOps(hide))
    }

    public fun fadeOverlay(
        player: Player,
        startColour: Int,
        startTransparency: Int,
        endColour: Int,
        endTransparency: Int,
        clientDuration: Int,
        eventBus: EventBus,
    ) {
        player.ifSetText(components.fade_overlay_message, "")
        player.ifOpenFullOverlay(interfaces.fade_overlay, eventBus)
        player.runClientScript(
            948,
            startColour,
            startTransparency,
            endColour,
            endTransparency,
            clientDuration,
        )
    }

    public fun closeFadeOverlay(player: Player, eventBus: EventBus) {
        player.ifCloseOverlay(interfaces.fade_overlay, eventBus)
    }

    // TODO: Add and publish events for these toplevel tab functions instead to allow for any
    //  "gameframe" plugin script control over what is closed and re-opened.

    public fun closeToplevelTabs(player: Player, eventBus: EventBus) {
        player.ifCloseOverlay(interfaces.orbs, eventBus)
        player.ifCloseOverlay(interfaces.xp_drops, eventBus)
        player.ifCloseOverlay(interfaces.combat_interface, eventBus)
        player.ifCloseOverlay(interfaces.stats, eventBus)
        player.ifCloseOverlay(interfaces.side_journal, eventBus)
        player.ifCloseOverlay(interfaces.inventory, eventBus)
        player.ifCloseOverlay(interfaces.wornitems, eventBus)
        player.ifCloseOverlay(interfaces.prayerbook, eventBus)
        player.ifCloseOverlay(interfaces.magic_spellbook, eventBus)
        player.ifCloseOverlay(interfaces.friends, eventBus)
        player.ifCloseOverlay(interfaces.account, eventBus)
        player.ifCloseOverlay(interfaces.settings_side, eventBus)
        player.ifCloseOverlay(interfaces.emote, eventBus)
        player.ifCloseOverlay(interfaces.music, eventBus)
    }

    public fun closeToplevelTabsLenient(player: Player, eventBus: EventBus) {
        player.ifOpenSub(
            interfaces.orbs,
            components.toplevel_target_orbs,
            IfSubType.Overlay,
            eventBus,
        )
        player.ifCloseOverlay(interfaces.xp_drops, eventBus)
        player.ifCloseOverlay(interfaces.combat_interface, eventBus)
        player.ifCloseOverlay(interfaces.stats, eventBus)
        player.ifCloseOverlay(interfaces.side_journal, eventBus)
        player.ifCloseOverlay(interfaces.inventory, eventBus)
        player.ifCloseOverlay(interfaces.wornitems, eventBus)
        player.ifCloseOverlay(interfaces.prayerbook, eventBus)
        player.ifCloseOverlay(interfaces.magic_spellbook, eventBus)
        player.ifOpenSub(
            interfaces.friends,
            components.toplevel_target_side9,
            IfSubType.Overlay,
            eventBus,
        )
        player.ifOpenSub(
            interfaces.account,
            components.toplevel_target_side8,
            IfSubType.Overlay,
            eventBus,
        )
        player.ifCloseOverlay(interfaces.settings_side, eventBus)
        player.ifCloseOverlay(interfaces.emote, eventBus)
        player.ifOpenSub(
            interfaces.music,
            components.toplevel_target_side13,
            IfSubType.Overlay,
            eventBus,
        )
    }

    public fun openTopLevelTabs(player: Player, eventBus: EventBus) {
        player.ifOpenSub(
            interfaces.xp_drops,
            components.toplevel_target_xp_drops,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.combat_interface,
            components.toplevel_target_side0,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.stats,
            components.toplevel_target_side1,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.side_journal,
            components.toplevel_target_side2,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.inventory,
            components.toplevel_target_side3,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.wornitems,
            components.toplevel_target_side4,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.prayerbook,
            components.toplevel_target_side5,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.magic_spellbook,
            components.toplevel_target_side6,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.friends,
            components.toplevel_target_side9,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.account,
            components.toplevel_target_side8,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.settings_side,
            components.toplevel_target_side11,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.emote,
            components.toplevel_target_side12,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.music,
            components.toplevel_target_side13,
            IfSubType.Overlay,
            eventBus,
        )
    }
}
