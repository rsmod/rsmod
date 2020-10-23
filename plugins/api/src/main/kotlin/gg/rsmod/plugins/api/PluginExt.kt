package gg.rsmod.plugins.api

import gg.rsmod.game.event.impl.CloseModal
import gg.rsmod.game.event.impl.CloseOverlay
import gg.rsmod.game.event.impl.CloseTopLevel
import gg.rsmod.game.event.impl.CommandEvent
import gg.rsmod.game.event.impl.LoginEvent
import gg.rsmod.game.event.impl.OpenModal
import gg.rsmod.game.event.impl.OpenOverlay
import gg.rsmod.game.event.impl.OpenTopLevel
import gg.rsmod.game.model.ui.UserInterface
import gg.rsmod.game.plugin.Plugin

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

fun Plugin.onCommand(cmd: String, block: CommandEvent.() -> Unit) {
    check(cmd.isNotBlank()) { "Command name must not be empty." }
    onEvent<CommandEvent>()
        .where { cmd.contentEquals(command) }
        .then(block)
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
