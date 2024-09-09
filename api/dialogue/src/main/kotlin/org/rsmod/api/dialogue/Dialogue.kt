@file:Suppress("konsist.properties are declared before functions")

package org.rsmod.api.dialogue

import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.BaseMesAnims
import org.rsmod.api.dialogue.align.TextAlignment
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.shops.Shops
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.npc.UnpackedNpcType

public class Dialogue(
    public val access: ProtectedAccess,
    private val eventBus: EventBus,
    private val alignment: TextAlignment,
    public val npc: Npc? = null,
    public val faceFar: Boolean = false,
) {
    public val player: Player
        get() = access.player

    public fun openShop(
        shops: Shops,
        title: String,
        shopInv: InvType,
        subtext: String = Shops.DEFAULT_SUBTEXT,
    ): Unit = shops.open(player, title, shopInv, subtext)

    public suspend fun mesbox(text: String) {
        access.mesbox(eventBus, text)
    }

    public suspend fun chatPlayer(mesanim: MesAnimType, text: String) {
        val lineCount = text.lineCount()
        val lineHeight = lineHeight(lineCount)
        access.chatPlayer(eventBus, text, mesanim, lineCount, lineHeight)
    }

    public suspend fun chatPlayerNoAnim(text: String) {
        val lineCount = text.lineCount()
        val lineHeight = lineHeight(lineCount)
        access.chatPlayer(eventBus, text, mesanim = null, lineCount, lineHeight)
    }

    public suspend fun chatNpc(mesanim: MesAnimType, text: String) {
        val lineCount = text.lineCount()
        val lineHeight = lineHeight(lineCount)
        access.chatNpc(eventBus, npc(), text, mesanim, lineCount, lineHeight, faceFar = faceFar)
    }

    public suspend fun chatNpcNoTurn(mesanim: MesAnimType, text: String) {
        val lineCount = text.lineCount()
        val lineHeight = lineHeight(lineCount)
        access.chatNpcNoTurn(eventBus, npc(), text, mesanim, lineCount, lineHeight)
    }

    public suspend fun chatNpcNoAnim(text: String) {
        val lineCount = text.lineCount()
        val lineHeight = lineHeight(lineCount)
        access.chatNpc(
            eventBus = eventBus,
            npc = npc(),
            text = text,
            mesanim = null,
            lineCount = lineCount,
            lineHeight = lineHeight,
            faceFar = faceFar,
        )
    }

    public suspend fun chatNpcSpecific(type: UnpackedNpcType, mesanim: MesAnimType, text: String) {
        val lineCount = text.lineCount()
        val lineHeight = lineHeight(lineCount)
        access.chatNpcSpecific(eventBus, type.name, type, text, mesanim, lineCount, lineHeight)
    }

    public suspend fun <T> choice2(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        title: String = Constants.cm_options,
    ): T =
        access.choice2(
            eventBus = eventBus,
            choice1 = choice1,
            result1 = result1,
            choice2 = choice2,
            result2 = result2,
            title = title,
        )

    public suspend fun <T> choice3(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        title: String = Constants.cm_options,
    ): T =
        access.choice3(
            eventBus = eventBus,
            choice1 = choice1,
            result1 = result1,
            choice2 = choice2,
            result2 = result2,
            choice3 = choice3,
            result3 = result3,
            title = title,
        )

    public suspend fun <T> choice4(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        choice4: String,
        result4: T,
        title: String = Constants.cm_options,
    ): T =
        access.choice4(
            eventBus = eventBus,
            choice1 = choice1,
            result1 = result1,
            choice2 = choice2,
            result2 = result2,
            choice3 = choice3,
            result3 = result3,
            choice4 = choice4,
            result4 = result4,
            title = title,
        )

    public suspend fun <T> choice5(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        choice4: String,
        result4: T,
        choice5: String,
        result5: T,
        title: String = Constants.cm_options,
    ): T =
        access.choice5(
            eventBus = eventBus,
            choice1 = choice1,
            result1 = result1,
            choice2 = choice2,
            result2 = result2,
            choice3 = choice3,
            result3 = result3,
            choice4 = choice4,
            result4 = result4,
            choice5 = choice5,
            result5 = result5,
            title = title,
        )

    public suspend fun delay(ticks: Int = 1): Unit = access.delay(ticks)

    private fun npc(): Npc =
        npc ?: error("`npc` must be set. Use `Dialogues.start(player, npc)` to start the dialogue.")

    private fun lineHeight(lineCount: Int): Int = alignment.lineHeight(lineCount)

    private fun String.lineCount(): Int = alignment.computeLineCount(this)

    public val quiz: MesAnimType = BaseMesAnims.quiz
    public val bored: MesAnimType = BaseMesAnims.bored
    public val short: MesAnimType = BaseMesAnims.short
    public val happy: MesAnimType = BaseMesAnims.happy
    public val shocked: MesAnimType = BaseMesAnims.shocked
    public val confused: MesAnimType = BaseMesAnims.confused
    public val silent: MesAnimType = BaseMesAnims.silent
    public val neutral: MesAnimType = BaseMesAnims.neutral
    public val shifty: MesAnimType = BaseMesAnims.shifty
    public val worried: MesAnimType = BaseMesAnims.worried
    public val drunk: MesAnimType = BaseMesAnims.drunk
    public val verymad: MesAnimType = BaseMesAnims.very_mad
    public val laugh: MesAnimType = BaseMesAnims.laugh
    public val madlaugh: MesAnimType = BaseMesAnims.mad_laugh
    public val sad: MesAnimType = BaseMesAnims.sad
    public val angry: MesAnimType = BaseMesAnims.angry
}
