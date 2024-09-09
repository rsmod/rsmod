package org.rsmod.api.dialogue

import jakarta.inject.Inject
import org.rsmod.api.dialogue.align.TextAlignment
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc

public class Dialogues
@Inject
constructor(private val eventBus: EventBus, private val alignment: TextAlignment) {
    public suspend fun start(access: ProtectedAccess, conversation: suspend Dialogue.() -> Unit) {
        val dialogue = Dialogue(access, eventBus, alignment, null, faceFar = false)
        conversation(dialogue)
    }

    public suspend fun start(
        access: ProtectedAccess,
        npc: Npc,
        faceFar: Boolean,
        conversation: suspend Dialogue.() -> Unit,
    ) {
        val dialogue = Dialogue(access, eventBus, alignment, npc, faceFar)
        conversation(dialogue)
    }
}

public suspend fun ProtectedAccess.startDialogue(
    dialogues: Dialogues,
    conversation: suspend Dialogue.() -> Unit,
) = dialogues.start(this, conversation)

public suspend fun ProtectedAccess.startDialogue(
    dialogues: Dialogues,
    npc: Npc,
    faceFar: Boolean = false,
    conversation: suspend Dialogue.() -> Unit,
) = dialogues.start(this, npc, faceFar, conversation)
