package org.rsmod.plugins.api

import org.rsmod.game.event.impl.CloseModal
import org.rsmod.game.event.impl.CloseOverlay
import org.rsmod.game.event.impl.CloseTopLevel
import org.rsmod.game.event.impl.LoginEvent
import org.rsmod.game.event.impl.OpenModal
import org.rsmod.game.event.impl.OpenOverlay
import org.rsmod.game.event.impl.OpenTopLevel
import org.rsmod.game.cmd.CommandBuilder
import org.rsmod.game.model.ui.UserInterface
import org.rsmod.game.plugin.Plugin
import org.rsmod.plugins.api.protocol.packet.client.OperateObjectAction
import org.rsmod.plugins.api.protocol.packet.client.OperateObjectOneAction

fun Plugin.onEarlyLogin(block: LoginEvent.() -> Unit) {
    onEvent<LoginEvent>()
        .where { priority == LoginEvent.Priority.High }
        .then(block)
}

fun Plugin.onLogin(block: LoginEvent.() -> Unit) {
    onEvent<LoginEvent>()
        .where { priority == LoginEvent.Priority.Normal }
        .then(block)
}

fun Plugin.onPostLogin(block: LoginEvent.() -> Unit) {
    onEvent<LoginEvent>()
        .where { priority == LoginEvent.Priority.Low }
        .then(block)
}

fun Plugin.onCommand(cmd: String, block: CommandBuilder.() -> Unit) {
    commands.register(cmd, block)
}

fun Plugin.onOpenTopLevel(top: UserInterface, block: OpenTopLevel.() -> Unit) {
    onEvent<OpenTopLevel>()
        .where { this.top == top }
        .then(block)
}

fun Plugin.onCloseTopLevel(top: UserInterface, block: CloseTopLevel.() -> Unit) {
    onEvent<CloseTopLevel>()
        .where { this.top == top }
        .then(block)
}

fun Plugin.onOpenModal(modal: UserInterface, block: OpenModal.() -> Unit) {
    onEvent<OpenModal>()
        .where { this.modal == modal }
        .then(block)
}

fun Plugin.onCloseModal(modal: UserInterface, block: CloseModal.() -> Unit) {
    onEvent<CloseModal>()
        .where { this.modal == modal }
        .then(block)
}

fun Plugin.onOpenOverlay(overlay: UserInterface, block: OpenOverlay.() -> Unit) {
    onEvent<OpenOverlay>()
        .where { this.overlay == overlay }
        .then(block)
}

fun Plugin.onCloseOverlay(overlay: UserInterface, block: CloseOverlay.() -> Unit) {
    onEvent<CloseOverlay>()
        .where { this.overlay == overlay }
        .then(block)
}
