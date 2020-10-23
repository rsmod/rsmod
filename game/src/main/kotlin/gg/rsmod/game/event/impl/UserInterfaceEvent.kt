package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.ui.Component
import gg.rsmod.game.model.ui.UserInterface

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
