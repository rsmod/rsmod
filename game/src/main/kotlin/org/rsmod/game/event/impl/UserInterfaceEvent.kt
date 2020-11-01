package org.rsmod.game.event.impl

import org.rsmod.game.event.Event
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.UserInterface

data class OpenTopLevel(
    val player: Player,
    val top: UserInterface
) : Event

data class CloseTopLevel(
    val player: Player,
    val top: UserInterface
) : Event

data class OpenModal(
    val player: Player,
    val parent: Component,
    val modal: UserInterface
) : Event

data class CloseModal(
    val player: Player,
    val parent: Component,
    val modal: UserInterface
) : Event

data class OpenOverlay(
    val player: Player,
    val parent: Component,
    val overlay: UserInterface
) : Event

data class CloseOverlay(
    val player: Player,
    val parent: Component,
    val overlay: UserInterface
) : Event
