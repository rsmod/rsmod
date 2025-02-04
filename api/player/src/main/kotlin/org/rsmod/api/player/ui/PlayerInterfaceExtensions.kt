package org.rsmod.api.player.ui

import net.rsprot.protocol.game.outgoing.interfaces.IfCloseSub
import net.rsprot.protocol.game.outgoing.interfaces.IfOpenSub
import net.rsprot.protocol.game.outgoing.interfaces.IfOpenTop
import net.rsprot.protocol.game.outgoing.interfaces.IfSetAnim
import net.rsprot.protocol.game.outgoing.interfaces.IfSetEvents
import net.rsprot.protocol.game.outgoing.interfaces.IfSetNpcHead
import net.rsprot.protocol.game.outgoing.interfaces.IfSetNpcHeadActive
import net.rsprot.protocol.game.outgoing.interfaces.IfSetObject
import net.rsprot.protocol.game.outgoing.interfaces.IfSetPlayerHead
import net.rsprot.protocol.game.outgoing.interfaces.IfSetText
import net.rsprot.protocol.game.outgoing.misc.player.TriggerOnDialogAbort
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.input.ResumePCountDialogInput
import org.rsmod.api.player.input.ResumePNameDialogInput
import org.rsmod.api.player.input.ResumePObjDialogInput
import org.rsmod.api.player.input.ResumePStringDialogInput
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.ClientScripts.chatboxMultiInit
import org.rsmod.api.player.output.ClientScripts.confirmDestroyInit
import org.rsmod.api.player.output.ClientScripts.confirmOverlayInit
import org.rsmod.api.player.output.ClientScripts.ifSetTextAlign
import org.rsmod.api.player.output.ClientScripts.objboxSetButtons
import org.rsmod.api.player.output.ClientScripts.topLevelChatboxResetBackground
import org.rsmod.api.player.output.ClientScripts.topLevelMainModalBackground
import org.rsmod.api.player.output.ClientScripts.topLevelMainModalOpen
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.vars.intVarp
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.coroutine.resume.DeferredResumeCondition
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface
import org.rsmod.game.ui.UserInterfaceMap

private typealias OpenTop = org.rsmod.api.player.ui.IfOpenTop

private typealias OpenSub = org.rsmod.api.player.ui.IfOpenSub

private typealias CloseSub = org.rsmod.api.player.ui.IfCloseSub

private var Player.modalWidthAndHeightMode: Int by intVarp(varbits.modal_widthandheight_mode)

public fun Player.ifSetObj(target: ComponentType, obj: ObjType, zoomOrCount: Int) {
    client.write(IfSetObject(target.packed, obj.id, zoomOrCount))
}

public fun Player.ifSetObj(target: ComponentType, obj: InvObj, zoomOrCount: Int) {
    client.write(IfSetObject(target.packed, obj.id, zoomOrCount))
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

public fun Player.ifOpenMainSidePair(
    main: InterfaceType,
    side: InterfaceType,
    colour: Int,
    transparency: Int,
    eventBus: EventBus,
) {
    topLevelMainModalBackground(this, colour, transparency)
    openModal(main, components.main_modal, eventBus)
    openModal(side, components.side_modal, eventBus)
}

public fun Player.ifOpenOverlay(interf: InterfaceType, eventBus: EventBus): Unit =
    ifOpenSub(interf, components.overlay_target, IfSubType.Overlay, eventBus)

/**
 * Difference from [ifCloseModals]: this function clears all weak queues for the player and closes
 * any active dialog.
 *
 * @see [cancelActiveDialog]
 */
public fun Player.ifClose(eventBus: EventBus) {
    cancelActiveDialog()
    weakQueueList.clear()
    ifCloseModals(eventBus)
}

/**
 * Cancels and closes any active _input_ dialog suspension.
 *
 * #### Note
 * This is a custom concept and not part of any known game mechanic or command. It is primarily used
 * internally to close suspending input dialogs during the handling of `If3Button` packets.
 *
 * **Warning:** Avoid calling this function unless you fully understand its implications, as it can
 * interrupt active dialog-related processes.
 */
@InternalApi("Usage of this function should only be used internally, or sparingly.")
public fun Player.ifCloseInputDialog() {
    val coroutine = activeCoroutine ?: return
    if (coroutine.requiresInputDialogAbort()) {
        cancelActiveCoroutine()
        client.write(TriggerOnDialogAbort)
    }
}

/**
 * If [requiresInputDialogAbort] or [requiresPauseDialogAbort] conditions are met, the player's
 * active script will be cancelled ([Player.cancelActiveCoroutine]) and [TriggerOnDialogAbort] will
 * be sent to their client.
 */
private fun Player.cancelActiveDialog() {
    val coroutine = activeCoroutine ?: return
    if (coroutine.requiresPauseDialogAbort() || coroutine.requiresInputDialogAbort()) {
        cancelActiveCoroutine()
        client.write(TriggerOnDialogAbort)
    }
}

/**
 * Checks if the coroutine is suspended on a [DeferredResumeCondition] and the deferred type matches
 * [ResumePauseButtonInput], which occurs during dialogs with `Click here to continue`-esque pause
 * buttons.
 */
private fun GameCoroutine.requiresPauseDialogAbort(): Boolean =
    isAwaiting(ResumePauseButtonInput::class)

/**
 * Checks if the coroutine is suspended on a [DeferredResumeCondition] and the deferred type matches
 * any input from dialogue boxes built through cs2, which are not standard modals or overlays.
 */
private fun GameCoroutine.requiresInputDialogAbort(): Boolean {
    return isAwaitingAny(
        ResumePCountDialogInput::class,
        ResumePNameDialogInput::class,
        ResumePStringDialogInput::class,
        ResumePObjDialogInput::class,
    )
}

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

public fun Player.ifCloseModal(interf: InterfaceType, eventBus: EventBus) {
    closeModal(interf, eventBus)
}

public fun Player.ifCloseOverlay(interf: InterfaceType, eventBus: EventBus) {
    closeOverlay(interf, eventBus)
}

private fun Player.openModal(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    closeSubs(idComponent, eventBus)
    ui.modals[idComponent] = idInterface

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Modal.id))

    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Modal))
}

private fun Player.openOverlay(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    closeSubs(idComponent, eventBus)
    ui.overlays[idComponent] = idInterface

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Overlay.id))

    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Overlay))
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

    closeOverlayChildren(interf, eventBus)
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

    closeOverlayChildren(interf, eventBus)
}

/**
 * The difference between this and [ifClose]/[closeOverlay] is that this function will check if
 * [from] is being occupied by either a modal, or an overlay, and then close it accordingly.
 */
private fun Player.closeSubs(from: Component, eventBus: EventBus) {
    val remove = ui.modals.remove(from) ?: ui.overlays.remove(from)
    if (remove != null) {
        // Translate any gameframe target component when sent to the client. As far as the server
        // is aware, the interface was open on the "base" target component. (when applicable)
        val translated = ui.translate(from)
        client.write(IfCloseSub(translated.parent, translated.child))

        eventBus.publish(CloseSub(this, remove, from))

        closeOverlayChildren(remove, eventBus)
    }
}

private fun Player.closeOverlayChildren(parent: UserInterface, eventBus: EventBus) {
    val overlayEntries = ui.overlays.entries()
    for ((key, value) in overlayEntries) {
        val interf = UserInterface(value)
        val target = Component(key)
        if (target.parent == parent.id) {
            closeOverlay(interf, target, eventBus)
        }
    }
}

private fun InterfaceType.toIdInterface() = UserInterface(id)

private fun ComponentType.toIdComponent() = Component(packed)

private fun UserInterfaceMap.translate(component: Component): Component =
    gameframe.getOrNull(component) ?: component

/*
 * Dialogue helper functions
 *
 * These functions are intended to assist with displaying various dialogue interfaces to the player.
 * However, they do _not_ properly handle state suspension or resuming from player input.
 *
 * Important: These functions should only be used internally within systems that properly manage
 * player state, input handling, and coroutine suspension. Direct usage in other contexts may result
 * in unwanted behavior.
 */

internal fun Player.ifMesbox(text: String, pauseText: String, lineHeight: Int, eventBus: EventBus) {
    mes(text, ChatType.Mesbox)
    openModal(interfaces.text_dialogue, components.chat_dialogue_target, eventBus)
    ifSetText(components.text_dialogue_text, text)
    ifSetTextAlign(this, components.text_dialogue_text, alignH = 1, alignV = 1, lineHeight)
    ifSetEvents(components.text_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.text_dialogue_pbutton, pauseText)
    // TODO: Look into clientscript to name property and place in clientscript utility class.
    runClientScript(1508, "0")
}

internal fun Player.ifObjbox(
    text: String,
    obj: Int,
    zoom: Int,
    pauseText: String,
    eventBus: EventBus,
) {
    mes(text, ChatType.Mesbox)
    ifOpenChat(interfaces.obj_dialogue, constants.modal_infinitewidthandheight, eventBus)
    objboxSetButtons(this, pauseText)
    ifSetEvents(components.obj_dialogue_pbutton, 0..1, IfEvent.PauseButton)
    ifSetObj(components.obj_dialogue_obj, obj, zoom)
    ifSetText(components.obj_dialogue_text, text)
}

internal fun Player.ifDoubleobjbox(
    text: String,
    obj1: Int,
    zoom1: Int,
    obj2: Int,
    zoom2: Int,
    pauseText: String,
    eventBus: EventBus,
) {
    mes(text, ChatType.Mesbox)
    ifOpenChat(interfaces.double_obj_dialogue, constants.modal_infinitewidthandheight, eventBus)
    ifSetEvents(components.double_obj_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.double_obj_dialogue_pbutton, pauseText)
    ifSetObj(components.double_obj_dialogue_obj1, obj1, zoom1)
    ifSetObj(components.double_obj_dialogue_obj2, obj2, zoom2)
    ifSetText(components.double_obj_dialogue_text, text)
}

internal fun Player.ifConfirmDestroy(
    header: String,
    text: String,
    obj: Int,
    count: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.destroy_obj_dialogue, constants.modal_fixedwidthandheight, eventBus)
    confirmDestroyInit(this, header, text, obj, count)
    ifSetEvents(components.destroy_obj_dialogue_pbutton, 0..1, IfEvent.PauseButton)
}

internal fun Player.ifConfirmOverlay(
    target: ComponentType,
    title: String,
    text: String,
    cancel: String,
    confirm: String,
    eventBus: EventBus,
) {
    ifOpenSub(interfaces.overlay_confirmation, target, IfSubType.Overlay, eventBus)
    confirmOverlayInit(this, target, title, text, cancel, confirm)
}

internal fun Player.ifConfirmOverlayClose(eventBus: EventBus): Unit =
    ifCloseOverlay(interfaces.overlay_confirmation, eventBus)

/** @see [chatboxMultiInit] */
internal fun Player.ifChoice(
    title: String,
    joinedChoices: String,
    choiceCountInclusive: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.options_dialogue, constants.modal_infinitewidthandheight, eventBus)
    chatboxMultiInit(this, title, joinedChoices)
    ifSetEvents(components.options_dialogue_pbutton, 1..choiceCountInclusive, IfEvent.PauseButton)
}

internal fun Player.ifChatPlayer(
    title: String,
    text: String,
    expression: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(interfaces.player_dialogue, constants.modal_fixedwidthandheight, eventBus)
    ifSetPlayerHead(components.player_dialogue_head)
    ifSetAnim(components.player_dialogue_head, expression)
    ifSetText(components.player_dialogue_title, title)
    ifSetText(components.player_dialogue_text, text)
    ifSetTextAlign(this, components.player_dialogue_text, alignH = 1, alignV = 1, lineHeight)
    ifSetEvents(components.player_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.player_dialogue_pbutton, pauseText)
}

internal fun Player.ifChatNpcActive(
    title: String,
    npcSlotId: Int,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(interfaces.npc_dialogue, constants.modal_fixedwidthandheight, eventBus)
    ifSetNpcHeadActive(components.npc_dialogue_head, npcSlotId)
    ifSetAnim(components.npc_dialogue_head, chatanim)
    ifSetText(components.npc_dialogue_title, title)
    ifSetText(components.npc_dialogue_text, text)
    ifSetTextAlign(this, components.npc_dialogue_text, alignH = 1, alignV = 1, lineHeight)
    ifSetEvents(components.npc_dialogue_pbutton, -1..-1, IfEvent.PauseButton)
    ifSetText(components.npc_dialogue_pbutton, pauseText)
}

internal fun Player.ifChatNpcSpecific(
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

internal fun Player.ifOpenChat(interf: InterfaceType, widthAndHeightMode: Int, eventBus: EventBus) {
    modalWidthAndHeightMode = widthAndHeightMode
    topLevelChatboxResetBackground(this)
    openModal(interf, components.chat_dialogue_target, eventBus)
}

private fun Player.ifSetObj(target: ComponentType, obj: Int, zoomOrCount: Int) {
    client.write(IfSetObject(target.packed, obj, zoomOrCount))
}
