package org.rsmod.api.player.ui

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
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.ClientScripts.chatboxMultiInit
import org.rsmod.api.player.output.ClientScripts.ifSetTextAlign
import org.rsmod.api.player.output.ClientScripts.topLevelChatboxResetBackground
import org.rsmod.api.player.output.ClientScripts.topLevelMainModalOpen
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.runClientScript
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

private typealias OpenTop = org.rsmod.api.player.ui.IfOpenTop

private typealias OpenSub = org.rsmod.api.player.ui.IfOpenSub

private typealias CloseSub = org.rsmod.api.player.ui.IfCloseSub

private var Player.modalWidthAndHeightMode: Int by intVarp(varbits.modal_widthandheight_mode)

public fun Player.ifMesbox(text: String, pauseText: String, eventBus: EventBus) {
    mes(text, ChatType.Mesbox)
    openModal(interfaces.text_dialogue, components.chat_dialogue_target, eventBus)
    ifSetText(components.text_dialogue_text, text)
    ifSetTextAlign(this, components.text_dialogue_text, alignH = 1, alignV = 1, lineHeight = 0)
    ifSetEvents(components.text_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.text_dialogue_pbutton, pauseText)
    // TODO: Look into clientscript to name property and place in clientscript utility class.
    runClientScript(1508, "0")
}

/** @see [chatboxMultiInit] */
public fun Player.ifChoice(
    title: String,
    joinedChoices: String,
    choiceCountInclusive: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.options_dialogue, constants.modal_infinitewidthandheight, eventBus)
    chatboxMultiInit(this, title, joinedChoices)
    ifSetEvents(components.options_dialogue_pbutton, 1..choiceCountInclusive, IfEvent.PauseButton)
}

public fun Player.ifChatPlayer(
    title: String,
    text: String,
    expression: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.player_dialogue, constants.modal_fixedwidthandheight, eventBus)
    ifSetPlayerHead(components.player_dialogue_head)
    ifSetAnim(components.player_dialogue_head, expression)
    ifSetText(components.player_dialogue_title, title)
    ifSetText(components.player_dialogue_text, text)
    ifSetTextAlign(this, components.player_dialogue_text, alignH = 1, alignV = 1, lineHeight)
    ifSetEvents(components.player_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.player_dialogue_pbutton, pauseText)
}

public fun Player.ifChatNpcActive(
    title: String,
    npcSlotId: Int,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.npc_dialogue, constants.modal_fixedwidthandheight, eventBus)
    ifSetNpcHeadActive(components.npc_dialogue_head, npcSlotId)
    ifSetAnim(components.npc_dialogue_head, chatanim)
    ifSetText(components.npc_dialogue_title, title)
    ifSetText(components.npc_dialogue_text, text)
    ifSetTextAlign(this, components.npc_dialogue_text, alignH = 1, alignV = 1, lineHeight)
    ifSetEvents(components.npc_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.npc_dialogue_pbutton, pauseText)
}

public fun Player.ifChatNpcSpecific(
    title: String,
    type: NpcType,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(interfaces.npc_dialogue, constants.modal_fixedwidthandheight, eventBus)
    ifSetNpcHead(components.npc_dialogue_head, type)
    ifSetAnim(components.npc_dialogue_head, chatanim)
    ifSetText(components.npc_dialogue_title, title)
    ifSetText(components.npc_dialogue_text, text)
    ifSetTextAlign(this, components.npc_dialogue_text, alignH = 1, alignV = 1, lineHeight)
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

public fun Player.ifOpenChat(interf: InterfaceType, widthAndHeightMode: Int, eventBus: EventBus) {
    modalWidthAndHeightMode = widthAndHeightMode
    topLevelChatboxResetBackground(this)
    openModal(interf, components.chat_dialogue_target, eventBus)
}

public fun Player.ifOpenMainModal(
    interf: InterfaceType,
    eventBus: EventBus,
    colour: Int = -1,
    transparency: Int = -1,
) {
    topLevelMainModalOpen(this, colour, transparency)
    ifOpenMain(interf, eventBus)
}

public fun Player.ifOpenMain(interf: InterfaceType, eventBus: EventBus) {
    openModal(interf, components.main_modal, eventBus)
}

public fun Player.ifOpenMainSidePair(main: InterfaceType, side: InterfaceType, eventBus: EventBus) {
    openModal(main, components.main_modal, eventBus)
    openModal(side, components.side_modal, eventBus)
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
        closeModal(interf, target, eventBus)
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

public fun Player.ifOpenTop(topLevel: InterfaceType, eventBus: EventBus) {
    val userInterface = UserInterface(topLevel.id)
    ui.topLevels.clear()
    ui.topLevels += userInterface
    client.write(IfOpenTop(topLevel.id))
    eventBus.publish(OpenTop(this, topLevel.toIdInterface()))
}

public fun Player.ifOpenSub(
    interf: InterfaceType,
    target: ComponentType,
    type: IfSubType,
    eventBus: EventBus,
): Unit =
    when (type) {
        IfSubType.Modal -> openModal(interf, target, eventBus)
        IfSubType.Overlay -> openOverlay(interf, target, eventBus)
    }

public fun Player.ifCloseSub(interf: InterfaceType, eventBus: EventBus) {
    closeModal(interf, eventBus)
    closeOverlay(interf, eventBus)
}

private fun Player.openModal(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    closeSubs(idComponent, eventBus)
    ui.modals[idComponent] = idInterface
    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Modal))

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Modal.id))
}

private fun Player.openOverlay(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    closeSubs(idComponent, eventBus)
    ui.overlays[idComponent] = idInterface
    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Overlay))

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Overlay.id))
}

private fun Player.closeModal(interf: InterfaceType, eventBus: EventBus) {
    val idInterface = interf.toIdInterface()
    val target = ui.modals.getComponent(idInterface)
    if (target != null) {
        closeModal(idInterface, target, eventBus)
    }
}

public fun Player.closeModal(interf: UserInterface, target: Component, eventBus: EventBus) {
    ui.modals.remove(target)

    // Translate any gameframe target component when sent to the client. As far as the server
    // is aware, the interface was open on the "base" target component. (when applicable)
    val translated = ui.translate(target)
    client.write(IfCloseSub(translated.parent, translated.child))

    eventBus.publish(CloseSub(this, interf, target))
}

private fun Player.closeOverlay(interf: InterfaceType, eventBus: EventBus) {
    val idInterface = interf.toIdInterface()
    val target = ui.overlays.getComponent(idInterface)
    if (target != null) {
        closeOverlay(idInterface, target, eventBus)
    }
}

public fun Player.closeOverlay(interf: UserInterface, target: Component, eventBus: EventBus) {
    ui.overlays.remove(target)

    // Translate any gameframe target component when sent to the client. As far as the server
    // is aware, the interface was open on the "base" target component. (when applicable)
    val translated = ui.translate(target)
    client.write(IfCloseSub(translated.parent, translated.child))

    eventBus.publish(CloseSub(this, interf, target))
}

/**
 * The difference between this and [ifCloseModals]/[closeOverlay] is that this function will check
 * if [from] is being occupied by either a modal, or an overlay, and then close it accordingly.
 */
private fun Player.closeSubs(from: Component, eventBus: EventBus) {
    val remove = ui.modals.remove(from) ?: ui.overlays.remove(from)
    if (remove != null) {
        // Translate any gameframe target component when sent to the client. As far as the server
        // is aware, the interface was open on the "base" target component. (when applicable)
        val translated = ui.translate(from)
        client.write(IfCloseSub(translated.parent, translated.child))

        eventBus.publish(CloseSub(this, remove, from))
    }
}

private fun InterfaceType.toIdInterface() = UserInterface(id)

private fun ComponentType.toIdComponent() = Component(packed)

private fun UserInterfaceMap.translate(component: Component): Component =
    gameframe.getOrNull(component) ?: component