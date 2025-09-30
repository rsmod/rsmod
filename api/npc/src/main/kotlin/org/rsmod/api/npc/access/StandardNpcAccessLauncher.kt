package org.rsmod.api.npc.access

import jakarta.inject.Inject
import org.rsmod.game.entity.Npc

public class StandardNpcAccessLauncher
@Inject
constructor(private val contextFactory: StandardNpcAccessContextFactory) {
    public fun launch(npc: Npc, block: suspend StandardNpcAccess.() -> Unit) {
        check(!npc.isBusy) { "Npc must not be busy: $npc" }
        val context = contextFactory.create()
        npc.launch {
            val standardAccess = StandardNpcAccess(npc, this, context)
            block(standardAccess)
        }
    }
}
