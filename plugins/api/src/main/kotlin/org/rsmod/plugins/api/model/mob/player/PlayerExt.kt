package org.rsmod.plugins.api.model.mob.player

import org.rsmod.game.coroutine.delay
import org.rsmod.game.coroutine.stop
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.item.container.ItemContainerKey
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatKey
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.UserInterface
import org.rsmod.game.model.vars.type.VarbitType
import org.rsmod.game.model.vars.type.VarpType
import org.rsmod.plugins.api.event.ContinueDialogue
import org.rsmod.plugins.api.event.EquipItem
import org.rsmod.plugins.api.event.IntChatInput
import org.rsmod.plugins.api.event.ItemSearchInput
import org.rsmod.plugins.api.event.NameChatInput
import org.rsmod.plugins.api.event.StringChatInput
import org.rsmod.plugins.api.model.ui.InterfaceEvent
import org.rsmod.plugins.api.model.ui.closeModal
import org.rsmod.plugins.api.model.ui.openModal
import org.rsmod.plugins.api.model.ui.setComponentAnim
import org.rsmod.plugins.api.model.ui.setComponentEvents
import org.rsmod.plugins.api.model.ui.setComponentEventsAll
import org.rsmod.plugins.api.model.ui.setComponentItem
import org.rsmod.plugins.api.model.ui.setComponentNpc
import org.rsmod.plugins.api.model.ui.setComponentPlayer
import org.rsmod.plugins.api.model.ui.setComponentText
import org.rsmod.plugins.api.model.vars.getVarbit
import org.rsmod.plugins.api.model.vars.getVarp
import org.rsmod.plugins.api.model.vars.setVarbit
import org.rsmod.plugins.api.model.vars.setVarp
import org.rsmod.plugins.api.model.vars.toggleVarbit
import org.rsmod.plugins.api.model.vars.toggleVarp
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.server.MessageGame
import org.rsmod.plugins.api.protocol.packet.server.MinimapFlagSet
import org.rsmod.plugins.api.protocol.packet.server.RunClientScript
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import org.rsmod.plugins.api.protocol.packet.server.UpdateRunEnergy
import org.rsmod.plugins.api.protocol.packet.server.UpdateStat
import org.rsmod.plugins.api.protocol.packet.server.VarpLarge
import org.rsmod.plugins.api.protocol.packet.server.VarpSmall
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.update.player.mask.of

private val CHAT_BOX_COMPONENT = Component(162, 562)

fun Player.closeChatBoxModal() {
    closeModal(CHAT_BOX_COMPONENT)
}

fun Player.moveTo(destination: Coordinates, speed: MovementSpeed = this.speed, noclip: Boolean = false) {
    val type = when (speed) {
        MovementSpeed.Walk -> MoveType.ForceWalk
        MovementSpeed.Run -> MoveType.ForceRun
    }
    val action = MapMove(this, destination, type, noclip)
    actionBus.publish(action)
}

fun Player.updateAppearance() {
    val mask = AppearanceMask.of(this)
    entity.updates.add(mask)
}

fun Player.clearMinimapFlag() {
    sendMinimapFlag(-1, -1)
}

fun Player.sendMessage(text: String, type: Int = MessageType.GAME_MESSAGE, username: String? = null) {
    write(MessageGame(type, text, username))
}

fun Player.sendFilteredMessage(text: String, username: String? = null) {
    sendMessage(text, MessageType.FILTERED, username)
}

fun Player.sendRunEnergy(energy: Int = runEnergy.toInt()) {
    write(UpdateRunEnergy(energy))
}

fun Player.sendMinimapFlag(x: Int, y: Int) {
    val base = viewport.base
    val lx = (x - base.x)
    val ly = (y - base.y)
    write(MinimapFlagSet(lx, ly))
}

fun Player.sendMinimapFlag(coords: Coordinates) {
    sendMinimapFlag(coords.x, coords.y)
}

fun Player.sendItemContainer(key: Int? = null, component: Component? = null, container: ItemContainer) {
    check(key != null || component == null) { "Container key and/or component must be set." }
    val packet = UpdateInvFull(key ?: -1, component?.packed ?: -1, container)
    write(packet)
}

fun Player.sendVarp(varp: Int, value: Int) {
    val packet = when (value) {
        in Byte.MIN_VALUE..Byte.MAX_VALUE -> VarpSmall(varp, value)
        else -> VarpLarge(varp, value)
    }
    write(packet)
}

fun Player.sendStat(key: StatKey, stat: Stat) {
    write(UpdateStat(key.id, stat.currLevel, stat.experience.toInt()))
}

fun Player.runClientScript(id: Int, vararg args: Any) {
    write(RunClientScript(id, *args))
}

fun Player.getVarp(type: VarpType): Int = varpMap.getVarp(type)

fun Player.setVarp(type: VarpType, value: Int) = varpMap.setVarp(type, value)

fun Player.setVarp(type: VarpType, flag: Boolean, falseValue: Int = 0, trueValue: Int = 1) {
    return varpMap.setVarp(type, flag, falseValue, trueValue)
}

fun Player.toggleVarp(type: VarpType, value1: Int = 0, value2: Int = 1) {
    return varpMap.toggleVarp(type, value1, value2)
}

fun Player.getVarbit(type: VarbitType): Int = varpMap.getVarbit(type)

fun Player.setVarbit(type: VarbitType, value: Int) = varpMap.setVarbit(type, value)

fun Player.setVarbit(type: VarbitType, flag: Boolean, falseValue: Int = 0, trueValue: Int = 1) {
    return varpMap.setVarbit(type, flag, falseValue, trueValue)
}

fun Player.toggleVarbit(type: VarbitType, value1: Int = 0, value2: Int = 1) {
    return varpMap.toggleVarbit(type, value1, value2)
}

fun Player.addContainer(key: ItemContainerKey, container: ItemContainer) {
    container.ensureCapacity(key.capacity)
    if (containers.containsKey(key)) {
        val old = containers.getValue(key)
        old.forEachIndexed { index, item -> container[index] = item }
    }
    containers[key] = container
}

fun Player.equipItem(item: ItemType, slot: Int) {
    // TODO: equip item
    val event = EquipItem(this, item, slot)
    submitEvent(event)
}

suspend fun Player.inputInt(title: String = "Enter amount"): Int {
    runClientScript(108, title)
    val input = delay(IntChatInput::class)
    return input.amount
}

suspend fun Player.inputName(title: String = "Enter name"): String {
    runClientScript(109, title)
    val input = delay(NameChatInput::class)
    return input.text
}

suspend fun Player.inputString(title: String = "Enter text"): String {
    runClientScript(110, title)
    val input = delay(StringChatInput::class)
    return input.text
}

suspend fun Player.searchItemCatalogue(title: String): ItemType {
    runClientScript(750, title, 1, -1)
    val input = delay(ItemSearchInput::class)
    return input.item
}

/**
 * @param leading spacing between adjacent [text] lines.
 */
suspend fun Player.chatMessage(
    text: String,
    leading: Int = 31,
    pauseText: String = "Click here to continue"
) {
    val dialogueBox = UserInterface(229)
    openModal(dialogueBox, CHAT_BOX_COMPONENT)
    setComponentText(dialogueBox.child(1), text)
    runClientScript(600, 1, 1, leading, (dialogueBox.id shl 16) or 1)
    setComponentEventsAll(dialogueBox.child(2), InterfaceEvent.PAUSE)
    setComponentText(dialogueBox.child(2), pauseText)
    val input = delay(ContinueDialogue::class)
    if (input.component.interfaceId != dialogueBox.id) {
        stop()
    }
    closeModal(dialogueBox)
}

/**
 * @param animation animation for [npc] head model to play.
 *
 * @param leading spacing between adjacent [text] lines.
 */
suspend fun Player.chatNpc(
    text: String,
    npc: NpcType,
    animation: Int = 588,
    title: String = npc.name,
    leading: Int = 16
) {
    val dialogueBox = UserInterface(231)
    runClientScript(2379)
    openModal(dialogueBox, CHAT_BOX_COMPONENT)
    setComponentNpc(dialogueBox.child(1), npc)
    setComponentAnim(dialogueBox.child(1), animation)
    setComponentText(dialogueBox.child(2), title)
    setComponentText(dialogueBox.child(4), text)
    runClientScript(600, 1, 1, leading, (dialogueBox.id shl 16) or 4)
    setComponentEventsAll(dialogueBox.child(3), InterfaceEvent.PAUSE)
    setComponentText(dialogueBox.child(3), "Click here to continue")
    val input = delay(ContinueDialogue::class)
    if (input.component.interfaceId != dialogueBox.id) {
        stop()
    }
    closeModal(dialogueBox)
}

/**
 * @param animation animation for player head model to play.
 *
 * @param leading spacing between adjacent [text] lines.
 */
suspend fun Player.chatPlayer(
    text: String,
    animation: Int = 588,
    title: String = username,
    leading: Int = 16,
    pauseText: String = "Click here to continue"
) {
    val dialogueBox = UserInterface(217)
    runClientScript(2379)
    openModal(dialogueBox, CHAT_BOX_COMPONENT)
    setComponentPlayer(dialogueBox.child(1))
    setComponentAnim(dialogueBox.child(1), animation)
    setComponentText(dialogueBox.child(2), title)
    setComponentText(dialogueBox.child(4), text)
    runClientScript(600, 1, 1, leading, (dialogueBox.id shl 16) or 4)
    setComponentEventsAll(dialogueBox.child(3), InterfaceEvent.PAUSE)
    setComponentText(dialogueBox.child(3), pauseText)
    val input = delay(ContinueDialogue::class)
    if (input.component.interfaceId != dialogueBox.id) {
        stop()
    }
    closeModal(dialogueBox)
}

/**
 * Send chat box dialogue with item.
 *
 * @param options replaces the default [pauseText] text when not empty.
 */
suspend fun Player.chatItem(
    text: String,
    item: ItemType,
    amountOrZoom: Int = 1,
    pauseText: String = "Click here to continue",
    vararg options: String
) {
    val dialogueBox = UserInterface(193)
    openModal(dialogueBox, CHAT_BOX_COMPONENT)
    setComponentEvents(dialogueBox.child(0), 0..1, InterfaceEvent.PAUSE)
    setComponentItem(dialogueBox.child(1), item, amountOrZoom)
    setComponentText(dialogueBox.child(2), text)
    if (options.isNotEmpty()) {
        runClientScript(2868, *options)
    } else {
        runClientScript(2868, pauseText)
    }
    val input = delay(ContinueDialogue::class)
    if (input.component.interfaceId != dialogueBox.id) {
        stop()
    }
    closeModal(dialogueBox)
}

suspend fun Player.chatItem2(
    text: String,
    item1: ItemType,
    item2: ItemType,
    amountOrZoom1: Int = 1,
    amountOrZoom2: Int = 1,
    pauseText: String = "Click here to continue"
) {
    val dialogueBox = UserInterface(11)
    setComponentItem(dialogueBox.child(1), item1, amountOrZoom1)
    setComponentText(dialogueBox.child(2), text)
    setComponentItem(dialogueBox.child(3), item2, amountOrZoom2)
    setComponentText(dialogueBox.child(4), pauseText)
    setComponentEventsAll(dialogueBox.child(4), InterfaceEvent.PAUSE)
    openModal(dialogueBox, CHAT_BOX_COMPONENT)
    val input = delay(ContinueDialogue::class)
    if (input.component.interfaceId != dialogueBox.id) {
        stop()
    }
    closeModal(dialogueBox)
}

suspend fun Player.options(vararg options: String, title: String = "Select an Option"): Int {
    val dialogueBox = UserInterface(219)
    sendVarp(1021, 512)
    runClientScript(2379)
    openModal(dialogueBox, CHAT_BOX_COMPONENT)
    runClientScript(58, title, options.joinToString("|"))
    setComponentEvents(dialogueBox.child(1), 1..options.size, InterfaceEvent.PAUSE)
    val input = delay(ContinueDialogue::class)
    sendVarp(1021, 0)
    if (input.component.interfaceId != dialogueBox.id) {
        stop()
    }
    closeModal(dialogueBox)
    return input.slot
}
