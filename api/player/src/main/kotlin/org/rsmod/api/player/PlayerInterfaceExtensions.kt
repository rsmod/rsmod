package org.rsmod.api.player

import net.rsprot.protocol.game.outgoing.interfaces.IfCloseSub
import net.rsprot.protocol.game.outgoing.interfaces.IfOpenSub
import net.rsprot.protocol.game.outgoing.interfaces.IfOpenTop
import net.rsprot.protocol.game.outgoing.interfaces.IfSetAnim
import net.rsprot.protocol.game.outgoing.interfaces.IfSetEvents
import net.rsprot.protocol.game.outgoing.interfaces.IfSetNpcHead
import net.rsprot.protocol.game.outgoing.interfaces.IfSetNpcHeadActive
import net.rsprot.protocol.game.outgoing.interfaces.IfSetPlayerHead
import net.rsprot.protocol.game.outgoing.interfaces.IfSetText
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.util.ChatType
import org.rsmod.api.player.util.ClientScripts.chatboxMultiInit
import org.rsmod.api.player.util.ClientScripts.ifSetTextAlign
import org.rsmod.api.player.util.ClientScripts.topLevelChatboxResetBackground
import org.rsmod.api.player.vars.intVarp
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface
import org.rsmod.game.ui.UserInterfaceMap

private typealias OpenTop = org.rsmod.api.player.events.IfOpenTop

private typealias OpenSub = org.rsmod.api.player.events.IfOpenSub

private typealias CloseSub = org.rsmod.api.player.events.IfCloseSub

private var Player.modalWidthAndHeightMode: Int by intVarp(varbits.modal_widthandheight_mode)

public fun Player.ifMesbox(eventBus: EventBus, text: String, pauseText: String) {
    mes(text, ChatType.Mesbox)
    openModal(eventBus, interfaces.text_dialogue, components.chat_dialogue_target)
    ifSetText(components.text_dialogue_text, text)
    ifSetTextAlign(components.text_dialogue_text, alignH = 1, alignV = 1, lineHeight = 0)
    ifSetEvents(components.text_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.text_dialogue_pbutton, pauseText)
    // TODO: Look into clientscript to name property and place in clientscript utility class.
    runClientScript(1508, "0")
}

/** @see [chatboxMultiInit] */
public fun Player.ifChoice(
    eventBus: EventBus,
    title: String,
    joinedChoices: String,
    choiceCountInclusive: Int,
) {
    ifOpenChat(eventBus, interfaces.options_dialogue, constants.modal_infinitewidthandheight)
    chatboxMultiInit(title, joinedChoices)
    ifSetEvents(components.options_dialogue_pbutton, 1..choiceCountInclusive, IfEvent.PauseButton)
}

public fun Player.ifChatPlayer(
    eventBus: EventBus,
    title: String,
    text: String,
    expression: SeqType?,
    pauseText: String,
) {
    ifOpenChat(eventBus, interfaces.player_dialogue, constants.modal_fixedwidthandheight)
    ifSetPlayerHead(components.player_dialogue_head)
    ifSetAnim(components.player_dialogue_head, expression)
    ifSetText(components.player_dialogue_title, title)
    ifSetText(components.player_dialogue_text, text)
    ifSetTextAlign(components.player_dialogue_text, alignH = 1, alignV = 1, lineHeight = 16)
    ifSetEvents(components.player_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.player_dialogue_pbutton, pauseText)
}

public fun Player.ifChatNpcActive(
    eventBus: EventBus,
    title: String,
    npcSlotId: Int,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
) {
    ifOpenChat(eventBus, interfaces.npc_dialogue, constants.modal_fixedwidthandheight)
    ifSetNpcHeadActive(components.npc_dialogue_head, npcSlotId)
    ifSetAnim(components.npc_dialogue_head, chatanim)
    ifSetText(components.npc_dialogue_title, title)
    ifSetText(components.npc_dialogue_text, text)
    ifSetTextAlign(components.npc_dialogue_text, alignH = 1, alignV = 1, lineHeight = 16)
    ifSetEvents(components.npc_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.npc_dialogue_pbutton, pauseText)
}

public fun Player.ifChatNpcSpecific(
    eventBus: EventBus,
    title: String,
    type: NpcType,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(eventBus, interfaces.npc_dialogue, constants.modal_fixedwidthandheight)
    ifSetNpcHead(components.npc_dialogue_head, type)
    ifSetAnim(components.npc_dialogue_head, chatanim)
    ifSetText(components.npc_dialogue_title, title)
    ifSetText(components.npc_dialogue_text, text)
    ifSetTextAlign(components.npc_dialogue_text, alignH = 1, alignV = 1, lineHeight = 16)
    ifSetEvents(components.npc_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.npc_dialogue_pbutton, pauseText)
}

public fun Player.ifSetAnim(target: ComponentType, seq: SeqType?) {
    client.write(IfSetAnim(target.interfaceId, target.component, seq?.id ?: -1))
}

public fun Player.ifSetPlayerHead(target: ComponentType) {
    client.write(IfSetPlayerHead(target.interfaceId, target.component))
}

/** @see [IfSetNpcHead] */
public fun Player.ifSetNpcHead(target: ComponentType, npc: NpcType) {
    client.write(IfSetNpcHead(target.interfaceId, target.component, npc.id))
}

/** @see [IfSetNpcHeadActive] */
public fun Player.ifSetNpcHeadActive(target: ComponentType, npcSlotId: Int) {
    client.write(IfSetNpcHeadActive(target.interfaceId, target.component, npcSlotId))
}

public fun Player.ifOpenChat(eventBus: EventBus, interf: InterfaceType, widthAndHeightMode: Int) {
    modalWidthAndHeightMode = widthAndHeightMode
    topLevelChatboxResetBackground()
    openModal(eventBus, interf, components.chat_dialogue_target)
}

public fun Player.ifOpenMain(eventBus: EventBus, interf: InterfaceType) {
    openModal(eventBus, interf, components.main_modal)
}

public fun Player.ifOpenMainSidePair(eventBus: EventBus, main: InterfaceType, side: InterfaceType) {
    openModal(eventBus, main, components.main_modal)
    openModal(eventBus, side, components.side_modal)
}

public fun Player.ifClose(eventBus: EventBus) {
    // TODO: this might clear weakqueues. not sure yet.
    ifCloseModals(eventBus)
}

/**
 * Difference between this and [ifClose] is that `ifClose` _may_ (depending on future research) also
 * remove weak queues for the player.
 */
public fun Player.ifCloseModals(eventBus: EventBus) {
    // This gives us an iterable copy of the entries, so we are safe to modify ui.modals while
    // closing them.
    val modalEntries = ui.modals.entries()
    for ((key, value) in modalEntries) {
        val interf = UserInterface(value)
        val target = Component(key)
        closeModal(eventBus, interf, target)
    }
    // Make sure _all_ modals were closed. If not, then something is wrong, and we'd rather force
    // the player to disconnect than to allow them to keep modals open when they shouldn't.
    check(ui.modals.isEmpty()) {
        "Could not close all modals for player `$this`. (modals=${ui.modals})"
    }
}

public fun Player.ifSetEvents(target: ComponentType, range: IntRange, vararg event: IfEvent) {
    // TODO: keep track of these and clear them when closing their respective interface
    val packed = event.fold(0) { sum, element -> sum or element.bitmask }
    client.write(IfSetEvents(target.interfaceId, target.component, range.first, range.last, packed))
}

public fun Player.ifSetText(target: ComponentType, text: String) {
    client.write(IfSetText(target.interfaceId, target.component, text))
}

public fun Player.ifOpenTop(eventBus: EventBus, topLevel: InterfaceType) {
    val userInterface = UserInterface(topLevel.id)
    ui.topLevels.clear()
    ui.topLevels += userInterface
    client.write(IfOpenTop(topLevel.id))
    eventBus.publish(OpenTop(this, topLevel.toIdInterface()))
}

public fun Player.ifOpenSub(
    eventBus: EventBus,
    interf: InterfaceType,
    target: ComponentType,
    type: IfSubType = IfSubType.Modal,
): Unit =
    when (type) {
        IfSubType.Modal -> openModal(eventBus, interf, target)
        IfSubType.Overlay -> openOverlay(eventBus, interf, target)
    }

public fun Player.ifCloseSub(eventBus: EventBus, interf: InterfaceType) {
    closeModal(eventBus, interf)
    closeOverlay(eventBus, interf)
}

private fun Player.openModal(eventBus: EventBus, interf: InterfaceType, target: ComponentType) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    closeSubs(eventBus, idComponent)
    ui.modals[idComponent] = idInterface
    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Modal))

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Modal.id))
}

private fun Player.openOverlay(eventBus: EventBus, interf: InterfaceType, target: ComponentType) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    closeSubs(eventBus, idComponent)
    ui.overlays[idComponent] = idInterface
    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Overlay))

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Overlay.id))
}

private fun Player.closeModal(eventBus: EventBus, interf: InterfaceType) {
    val idInterface = interf.toIdInterface()
    val target = ui.modals.getComponent(idInterface)
    if (target != null) {
        closeModal(eventBus, idInterface, target)
    }
}

public fun Player.closeModal(eventBus: EventBus, interf: UserInterface, target: Component) {
    ui.modals.remove(target)
    eventBus.publish(CloseSub(this, interf, target))

    // Translate any gameframe target component when sent to the client. As far as the server
    // is aware, the interface was open on the "base" target component. (when applicable)
    val translated = ui.translate(target)
    client.write(IfCloseSub(translated.parent, translated.child))
}

private fun Player.closeOverlay(eventBus: EventBus, interf: InterfaceType) {
    val idInterface = interf.toIdInterface()
    val target = ui.overlays.getComponent(idInterface)
    if (target != null) {
        closeOverlay(eventBus, idInterface, target)
    }
}

public fun Player.closeOverlay(eventBus: EventBus, interf: UserInterface, target: Component) {
    ui.overlays.remove(target)
    eventBus.publish(CloseSub(this, interf, target))

    // Translate any gameframe target component when sent to the client. As far as the server
    // is aware, the interface was open on the "base" target component. (when applicable)
    val translated = ui.translate(target)
    client.write(IfCloseSub(translated.parent, translated.child))
}

/**
 * The difference between this and [ifCloseModals]/[closeOverlay] is that this function will check
 * if [from] is being occupied by either a modal, or an overlay, and then close it accordingly.
 */
private fun Player.closeSubs(eventBus: EventBus, from: Component) {
    val remove = ui.modals.remove(from) ?: ui.overlays.remove(from)
    if (remove != null) {
        eventBus.publish(CloseSub(this, remove, from))

        // Translate any gameframe target component when sent to the client. As far as the server
        // is aware, the interface was open on the "base" target component. (when applicable)
        val translated = ui.translate(from)
        client.write(IfCloseSub(translated.parent, translated.child))
    }
}

private fun InterfaceType.toIdInterface() = UserInterface(id)

private fun ComponentType.toIdComponent() = Component(packed)

private fun UserInterfaceMap.translate(component: Component): Component =
    gameframe.getOrNull(component) ?: component
