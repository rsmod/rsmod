package org.rsmod.api.dialogue

import jakarta.inject.Inject
import org.rsmod.api.dialogue.align.TextAlignment
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.entity.Npc

public class Dialogues @Inject constructor(private val alignment: TextAlignment) {
    public suspend fun start(access: ProtectedAccess, conversation: suspend Dialogue.() -> Unit) {
        val dialogue = Dialogue(access, alignment, null, faceFar = false)
        conversation(dialogue)
    }

    public suspend fun start(
        access: ProtectedAccess,
        npc: Npc,
        faceFar: Boolean = false,
        conversation: suspend Dialogue.() -> Unit,
    ) {
        val dialogue = Dialogue(access, alignment, npc, faceFar)
        conversation(dialogue)
    }
}

public suspend fun ProtectedAccess.startDialogue(
    dialogues: Dialogues,
    conversation: suspend Dialogue.() -> Unit,
): Unit = dialogues.start(this, conversation)

public suspend fun ProtectedAccess.startDialogue(
    dialogues: Dialogues,
    npc: Npc,
    faceFar: Boolean = false,
    conversation: suspend Dialogue.() -> Unit,
): Unit = dialogues.start(this, npc, faceFar, conversation)
