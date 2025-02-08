@file:Suppress("konsist.properties are declared before functions")

package org.rsmod.api.player.dialogue

import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.BaseMesAnims
import org.rsmod.api.player.dialogue.align.TextAlignment
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.VarPlayerIntMapDelegate
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType

public class Dialogue(
    public val access: ProtectedAccess,
    private val alignment: TextAlignment,
    public val npc: Npc? = null,
    public val faceFar: Boolean = false,
) {
    public val player: Player by access::player
    public val vars: VarPlayerIntMapDelegate by access::vars

    public val npcVisType: UnpackedNpcType
        get() = access.npcVisType(resolvedNpc)

    private val resolvedNpc: Npc
        get() = npc ?: error("`npc` must be set. Use `Dialogues.start(player, npc)` instead.")

    private val npcVisName: String
        get() = resolveNpcVisName()

    public suspend fun mesbox(text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.mesbox(pgText, lineHeight)
        }
    }

    public suspend fun objbox(obj: ObjType, text: String): Unit = objbox(obj, zoom = 400, text)

    public suspend fun objbox(obj: ObjType, zoom: Int, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            access.objbox(obj, zoom, page.text)
        }
    }

    public suspend fun objbox(obj: InvObj, text: String): Unit = objbox(obj, 1, text)

    public suspend fun objbox(obj: InvObj, zoom: Int, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            access.objbox(obj, zoom, page.text)
        }
    }

    public suspend fun doubleobjbox(obj1: ObjType, obj2: ObjType, text: String): Unit =
        doubleobjbox(obj1, zoom1 = 400, obj2, zoom2 = 400, text)

    public suspend fun doubleobjbox(
        obj1: ObjType,
        zoom1: Int,
        obj2: ObjType,
        zoom2: Int,
        text: String,
    ) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            access.doubleobjbox(obj1, zoom1, obj2, zoom2, page.text)
        }
    }

    public suspend fun doubleobjbox(obj1: InvObj, obj2: InvObj, text: String): Unit =
        doubleobjbox(obj1, zoom1 = 400, obj2, zoom2 = 400, text)

    public suspend fun doubleobjbox(
        obj1: InvObj,
        zoom1: Int,
        obj2: InvObj,
        zoom2: Int,
        text: String,
    ) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            access.doubleobjbox(obj1, zoom1, obj2, zoom2, page.text)
        }
    }

    public suspend fun chatPlayer(mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatPlayer(pgText, mesanim, lineCount, lineHeight)
        }
    }

    public suspend fun chatPlayerNoAnim(text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatPlayer(pgText, mesanim = null, lineCount, lineHeight)
        }
    }

    public suspend fun chatNpc(mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpc(
                title = npcVisName,
                npc = resolvedNpc,
                text = pgText,
                mesanim = mesanim,
                lineCount = lineCount,
                lineHeight = lineHeight,
                faceFar = faceFar,
            )
        }
    }

    public suspend fun chatNpcNoTurn(mesanim: MesAnimType, text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpcNoTurn(
                title = npcVisName,
                npc = resolvedNpc,
                text = pgText,
                mesanim = mesanim,
                lineCount = lineCount,
                lineHeight = lineHeight,
            )
        }
    }

    public suspend fun chatNpcNoAnim(text: String) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpc(
                title = npcVisName,
                npc = resolvedNpc,
                text = pgText,
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
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpcSpecific(
                type.name,
                resolvedNpc.type,
                pgText,
                mesanim,
                lineCount,
                lineHeight,
            )
        }
    }

    public suspend fun chatNpcSpecific(
        title: String,
        type: NpcType,
        mesanim: MesAnimType,
        text: String,
    ) {
        val pages = alignment.generatePageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = lineHeight(lineCount)
            access.chatNpcSpecific(title, type, pgText, mesanim, lineCount, lineHeight)
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

    public suspend fun confirmDestroy(
        obj: ObjType,
        count: Int,
        header: String,
        text: String,
    ): Boolean = access.confirmDestroy(obj, count, header, text)

    public suspend fun delay(cycles: Int = 1): Unit = access.delay(cycles)

    public fun invTotal(inv: Inventory, content: ContentGroupType): Int =
        access.invTotal(inv, content)

    public operator fun Inventory.contains(content: ContentGroupType): Boolean =
        access.invContains(this, content)

    public fun ocCert(type: ObjType): UnpackedObjType = access.ocCert(type)

    public fun ocUncert(type: ObjType): UnpackedObjType = access.ocUncert(type)

    private fun lineHeight(lineCount: Int): Int = alignment.lineHeight(lineCount)

    private fun resolveNpcVisName(): String {
        val type = resolvedNpc.type
        if (!type.isMultiNpc) {
            return type.name
        }
        val visType = access.npcVisType(resolvedNpc)
        return visType.name
    }

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
