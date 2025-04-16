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
    private var Player.compass by enumVarBit<CompassState>(varbits.minimap_state)
    private var Player.hideTop by boolVarBit(varbits.cutscene_status)
    private var Player.hideHud by boolVarBit(varbits.gravestone_tli_hide)
    private var Player.acceptAid by boolVarBit(varbits.option_acceptaid)
    private var Player.acceptAidRestore by boolVarBit(varbits.accept_aid_restore)

    public fun setCameraMode(player: Player, mode: CameraMode) {
        player.camMode = mode
    }

    public fun setCompassState(player: Player, compass: CompassState) {
        player.compass = compass
    }

    public fun setHideToplevel(player: Player, hide: Boolean) {
        player.hideTop = hide
    }

    public fun clearHealthHud(player: Player) {
        player.ifSetHide(components.hp_hud_com5, hide = true)
        ccDeleteAll(player, components.hp_hud_com1)
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
        player.client.write(MinimapToggle(state.id))
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
        player.ifSetText(components.fade_overlay_text, "")
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
        player.ifCloseOverlay(interfaces.experience_drops_window, eventBus)
        player.ifCloseOverlay(interfaces.combat_tab, eventBus)
        player.ifCloseOverlay(interfaces.skills_tab, eventBus)
        player.ifCloseOverlay(interfaces.journal_header_tab, eventBus)
        player.ifCloseOverlay(interfaces.inventory_tab, eventBus)
        player.ifCloseOverlay(interfaces.equipment_tab, eventBus)
        player.ifCloseOverlay(interfaces.prayer_tab, eventBus)
        player.ifCloseOverlay(interfaces.spellbook_tab, eventBus)
        player.ifCloseOverlay(interfaces.friend_list_tab, eventBus)
        player.ifCloseOverlay(interfaces.account_management_tab, eventBus)
        player.ifCloseOverlay(interfaces.settings_tab, eventBus)
        player.ifCloseOverlay(interfaces.emote_tab, eventBus)
        player.ifCloseOverlay(interfaces.music_tab, eventBus)
    }

    public fun closeToplevelTabsLenient(player: Player, eventBus: EventBus) {
        player.ifOpenSub(interfaces.orbs, components.orbs_target, IfSubType.Overlay, eventBus)
        player.ifCloseOverlay(interfaces.experience_drops_window, eventBus)
        player.ifCloseOverlay(interfaces.combat_tab, eventBus)
        player.ifCloseOverlay(interfaces.skills_tab, eventBus)
        player.ifCloseOverlay(interfaces.journal_header_tab, eventBus)
        player.ifCloseOverlay(interfaces.inventory_tab, eventBus)
        player.ifCloseOverlay(interfaces.equipment_tab, eventBus)
        player.ifCloseOverlay(interfaces.prayer_tab, eventBus)
        player.ifCloseOverlay(interfaces.spellbook_tab, eventBus)
        player.ifOpenSub(
            interfaces.friend_list_tab,
            components.friend_list_tab_target,
            IfSubType.Overlay,
            eventBus,
        )
        player.ifOpenSub(
            interfaces.account_management_tab,
            components.account_management_tab_target,
            IfSubType.Overlay,
            eventBus,
        )
        player.ifCloseOverlay(interfaces.settings_tab, eventBus)
        player.ifCloseOverlay(interfaces.emote_tab, eventBus)
        player.ifOpenSub(
            interfaces.music_tab,
            components.music_tab_target,
            IfSubType.Overlay,
            eventBus,
        )
    }

    public fun openTopLevelTabs(player: Player, eventBus: EventBus) {
        player.ifOpenSub(
            interfaces.experience_drops_window,
            components.experience_drops_window_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.combat_tab,
            components.combat_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.skills_tab,
            components.skills_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.journal_header_tab,
            components.journal_header_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.inventory_tab,
            components.inventory_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.equipment_tab,
            components.equipment_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.prayer_tab,
            components.prayer_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.spellbook_tab,
            components.spellbook_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.friend_list_tab,
            components.friend_list_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.account_management_tab,
            components.account_management_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.settings_tab,
            components.settings_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.emote_tab,
            components.emote_tab_target,
            IfSubType.Overlay,
            eventBus,
        )

        player.ifOpenSub(
            interfaces.music_tab,
            components.music_tab_target,
            IfSubType.Overlay,
            eventBus,
        )
    }
}
