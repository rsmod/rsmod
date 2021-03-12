package org.rsmod.plugins.content.gameframe

import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.game.model.ui.UserInterface
import org.rsmod.game.model.vars.type.VarpTypeList
import org.rsmod.plugins.api.model.mob.player.runClientScript
import org.rsmod.plugins.api.model.mob.player.sendRunEnergy
import org.rsmod.plugins.api.model.mob.player.setVarp
import org.rsmod.plugins.api.model.ui.openOverlay
import org.rsmod.plugins.api.model.ui.openTopLevel
import org.rsmod.plugins.api.onEarlyLogin

val varps: VarpTypeList by inject()

onEarlyLogin {
    player.sendGameframe()
    player.sendOrbs()
}

fun Player.sendGameframe() {
    openTopLevel(UserInterface(548))
    openOverlay(UserInterface(162), Component(548, 27))
    openOverlay(UserInterface(651), Component(548, 16))
    openOverlay(UserInterface(163), Component(548, 20))
    openOverlay(UserInterface(90), Component(548, 15))
    openOverlay(UserInterface(160), Component(548, 11))
    openOverlay(UserInterface(320), Component(548, 70))
    openOverlay(UserInterface(629), Component(548, 71))
    openOverlay(UserInterface(399), Component(629, 33))
    openOverlay(UserInterface(149), Component(548, 72))
    openOverlay(UserInterface(387), Component(548, 73))
    openOverlay(UserInterface(541), Component(548, 74))
    openOverlay(UserInterface(218), Component(548, 75))
    openOverlay(UserInterface(429), Component(548, 78))
    openOverlay(UserInterface(109), Component(548, 77))
    openOverlay(UserInterface(182), Component(548, 79))
    openOverlay(UserInterface(261), Component(548, 80))
    openOverlay(UserInterface(216), Component(548, 81))
    openOverlay(UserInterface(239), Component(548, 82))
    openOverlay(UserInterface(7), Component(548, 76))
    openOverlay(UserInterface(593), Component(548, 69))
    setVarp(varps[1055], 0)
    setVarp(varps[1737], -1)
    runClientScript(1105, 1)
}

fun Player.sendOrbs() {
    sendRunEnergy()
}
