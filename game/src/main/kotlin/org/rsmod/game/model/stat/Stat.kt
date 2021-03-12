package org.rsmod.game.model.stat

data class Stat(var currLevel: Int, var experience: Double) {

    companion object {

        val ZERO = Stat(0, 0.0)
    }
}
