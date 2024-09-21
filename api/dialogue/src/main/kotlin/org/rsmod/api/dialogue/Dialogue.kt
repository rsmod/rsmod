@file:Suppress("konsist.properties are declared before functions")

package org.rsmod.api.dialogue

import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.BaseMesAnims
import org.rsmod.api.dialogue.align.TextAlignment
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType

public class Dialogue(
    public val access: ProtectedAccess,
    private val alignment: TextAlignment,
    public val npc: Npc? = null,
    public val faceFar: Boolean = false,
) {
    public val player: Player
        get() = access.player

    public suspend fun mesbox(text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val text = page.text
            access.mesbox(text)
        }
    }

    public suspend fun objbox(obj: ObjType, text: String): Unit = objbox(obj, 1, text)

    public suspend fun objbox(obj: ObjType, zoomOrCount: Int, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val text = page.text
            access.objbox(obj, zoomOrCount, text)
        }
    }

    public suspend fun objbox(obj: InvObj, text: String): Unit = objbox(obj, 1, text)

    public suspend fun objbox(obj: InvObj, zoomOrCount: Int, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val text = page.text
            access.objbox(obj, zoomOrCount, text)
        }
    }

    public suspend fun chatPlayer(mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (text, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatPlayer(text, mesanim, lineCount, lineHeight)
        }
    }

    public suspend fun chatPlayerNoAnim(text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (text, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatPlayer(text, mesanim = null, lineCount, lineHeight)
        }
    }

    public suspend fun chatNpc(mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (text, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpc(npc(), text, mesanim, lineCount, lineHeight, faceFar = faceFar)
        }
    }

    public suspend fun chatNpcNoTurn(mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (text, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpcNoTurn(npc(), text, mesanim, lineCount, lineHeight)
        }
    }

    public suspend fun chatNpcNoAnim(text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (text, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpc(
                npc = npc(),
                text = text,
                mesanim = null,
                lineCount = lineCount,
                lineHeight = lineHeight,
                faceFar = faceFar,
            )
        }
    }

    public suspend fun chatNpcSpecific(type: UnpackedNpcType, mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (text, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpcSpecific(type.name, type, text, mesanim, lineCount, lineHeight)
        }
    }

    public suspend fun <T> choice2(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        title: String = Constants.cm_options,
    ): T =
        access.choice2(
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

    public suspend fun delay(cycles: Int = 1): Unit = access.delay(cycles)

    private fun npc(): Npc =
        npc ?: error("`npc` must be set. Use `Dialogues.start(player, npc)` to start the dialogue.")

    private fun lineHeight(lineCount: Int): Int = alignment.lineHeight(lineCount)

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
