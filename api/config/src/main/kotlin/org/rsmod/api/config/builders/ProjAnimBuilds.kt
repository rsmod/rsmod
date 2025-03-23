@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.proj.ProjAnimBuilder

object ProjAnimBuilds : ProjAnimBuilder() {
    init {
        build("arrow") {
            startHeight = 40
            endHeight = 36
            delay = 41
            angle = 15
            lengthAdjustment = 5
            progress = 11
            stepMultiplier = 5
        }

        build("bolt") {
            startHeight = 38
            endHeight = 36
            delay = 41
            angle = 5
            lengthAdjustment = 5
            progress = 11
            stepMultiplier = 5
        }

        build("chinchompa") {
            startHeight = 40
            endHeight = 36
            delay = 21
            angle = 15
            lengthAdjustment = 11
            progress = 11
            stepMultiplier = 5
        }

        build("thrown") {
            startHeight = 40
            endHeight = 36
            delay = 32
            angle = 15
            lengthAdjustment = 0
            progress = 11
            stepMultiplier = 5
        }

        build("doublearrow_one") {
            startHeight = 40
            endHeight = 36
            delay = 41
            angle = 5
            lengthAdjustment = 5
            progress = 11
            stepMultiplier = 5
        }

        build("doublearrow_two") {
            startHeight = 40
            endHeight = 36
            delay = 41
            angle = 25
            lengthAdjustment = 14
            progress = 11
            stepMultiplier = 10
        }
    }
}
