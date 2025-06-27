@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.proj.ProjAnimBuilder

internal object ProjAnimBuilds : ProjAnimBuilder() {
    init {
        build("arrow") {
            startHeight = 163
            endHeight = 146
            delay = 41
            angle = 15
            lengthAdjustment = 5
            progress = 11
            stepMultiplier = 5
        }

        build("bolt") {
            startHeight = 155
            endHeight = 146
            delay = 41
            angle = 5
            lengthAdjustment = 5
            progress = 11
            stepMultiplier = 5
        }

        build("chinchompa") {
            startHeight = 163
            endHeight = 146
            delay = 21
            angle = 15
            lengthAdjustment = 11
            progress = 11
            stepMultiplier = 5
        }

        build("thrown") {
            startHeight = 163
            endHeight = 146
            delay = 32
            angle = 15
            lengthAdjustment = 0
            progress = 11
            stepMultiplier = 5
        }

        build("doublearrow_one") {
            startHeight = 163
            endHeight = 146
            delay = 41
            angle = 5
            lengthAdjustment = 5
            progress = 11
            stepMultiplier = 5
        }

        build("doublearrow_two") {
            startHeight = 163
            endHeight = 146
            delay = 41
            angle = 25
            lengthAdjustment = 14
            progress = 11
            stepMultiplier = 10
        }

        build("magic_spell") {
            startHeight = 172
            endHeight = 124
            delay = 51
            angle = 16
            lengthAdjustment = -5
            progress = 64
            stepMultiplier = 10
        }

        build("magic_spell_low") {
            startHeight = 172
            endHeight = 0
            delay = 51
            angle = 16
            lengthAdjustment = -5
            progress = 64
            stepMultiplier = 10
        }

        build("iban_blast") {
            startHeight = 144
            endHeight = 124
            delay = 60
            angle = 16
            lengthAdjustment = -14
            progress = 64
            stepMultiplier = 10
        }

        build("vulnerability") {
            startHeight = 144
            endHeight = 124
            delay = 34
            angle = 16
            lengthAdjustment = 12
            progress = 64
            stepMultiplier = 10
        }

        build("stun") {
            startHeight = 144
            endHeight = 124
            delay = 52
            angle = 16
            lengthAdjustment = -6
            progress = 64
            stepMultiplier = 10
        }

        build("crumble_undead") {
            startHeight = 124
            endHeight = 124
            delay = 46
            angle = 16
            lengthAdjustment = 0
            progress = 64
            stepMultiplier = 5
        }

        build("enfeeble") {
            startHeight = 144
            endHeight = 124
            delay = 48
            angle = 16
            lengthAdjustment = -2
            progress = 64
            stepMultiplier = 10
        }

        build("confuse") {
            startHeight = 144
            endHeight = 124
            delay = 61
            angle = 16
            lengthAdjustment = -15
            progress = 64
            stepMultiplier = 10
        }

        build("bind") {
            startHeight = 180
            endHeight = 0
            delay = 75
            angle = 16
            lengthAdjustment = -29
            progress = 64
            stepMultiplier = 10
        }

        build("tumekens_shadow") {
            startHeight = 250
            endHeight = 124
            delay = 56
            angle = 32
            lengthAdjustment = 16
            progress = 40
            stepMultiplier = 10
        }
    }
}
