package org.rsmod.content.other.generic.npcs.cow

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc2
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.script.onPlayerQueue
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DairyCow : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.dairy_cow) { attemptMilkingCow(it.bound) }
        onOpLoc2(content.dairy_cow) { stealCowbell(it.bound) }
        onOpLocU(content.dairy_cow) { mes("The cow doesn't want that.") }
        onOpLocU(content.dairy_cow, objs.bucket_empty) { attemptMilkingCow(it.bound) }

        onPlayerQueue(cow_queues.milk) { milkCow() }
    }

    private suspend fun ProtectedAccess.attemptMilkingCow(loc: BoundLocInfo) {
        arriveDelay()
        faceSquare(loc.coords)
        if (objs.bucket_empty !in inv) {
            startDialogue { noBucket() }
            return
        }
        weakQueue(cow_queues.milk, 2)
    }

    private fun ProtectedAccess.milkCow() {
        val replace = invReplace(inv, objs.bucket_empty, 1, objs.bucket_of_milk)
        if (replace.failure) {
            return
        }
        spam("You milk the cow.")
        anim(seqs.human_milk_cow)
        soundSynth(synths.milk_cow)
        weakQueue(cow_queues.milk, 8)
    }

    private suspend fun Dialogue.noBucket() {
        chatNpcSpecific(
            "Gillie Groats the Milkmaid",
            cow_npcs.gillie_groats,
            laugh,
            "Tee hee! You've never milked a cow before, have you?",
        )
        chatPlayer(quiz, "Erm... No. How could you tell?")
        chatNpcSpecific(
            "Gillie Groats the Milkmaid",
            cow_npcs.gillie_groats,
            laugh,
            "Because you're spilling milk all over the floor. What a " +
                "waste! You need something to hold the milk.",
        )
        chatPlayer(neutral, "Ah yes, I really should have guessed that one, shouldn't I?")
        chatNpcSpecific(
            "Gillie Groats the Milkmaid",
            cow_npcs.gillie_groats,
            laugh,
            "You're from the city, aren't you... Try it again with an empty bucket.",
        )
        chatPlayer(neutral, "Right, I'll do that.")
    }

    private suspend fun ProtectedAccess.stealCowbell(loc: BoundLocInfo) {
        // TODO(content): Should be different actions based on cold war quest progress.
        arriveDelay()
        faceSquare(loc.coords)
        mesbox("You need to have started the Cold War quest to attempt this.", lineHeight = 0)
    }
}
