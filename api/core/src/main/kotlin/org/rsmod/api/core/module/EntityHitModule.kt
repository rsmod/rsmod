package org.rsmod.api.core.module

import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.modifier.StandardNpcHitModifier
import org.rsmod.api.npc.hit.processor.NpcHitProcessor
import org.rsmod.api.npc.hit.processor.StandardNpcHitProcessor
import org.rsmod.api.player.hit.processor.DamageOnlyPlayerHitProcessor
import org.rsmod.api.player.hit.processor.InstantPlayerHitProcessor
import org.rsmod.module.ExtendedModule

public object EntityHitModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<NpcHitModifier>(StandardNpcHitModifier::class.java)
        bindBaseInstance<NpcHitProcessor>(StandardNpcHitProcessor::class.java)
        bindBaseInstance<InstantPlayerHitProcessor>(DamageOnlyPlayerHitProcessor::class.java)
    }
}
