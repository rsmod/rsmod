package org.rsmod.api.core.module

import org.rsmod.api.npc.hit.modifier.HitModifierNpc
import org.rsmod.api.npc.hit.modifier.StandardNpcHitModifier
import org.rsmod.api.npc.hit.processor.QueuedNpcHitProcessor
import org.rsmod.api.npc.hit.processor.StandardNpcHitProcessor
import org.rsmod.module.ExtendedModule

public object EntityHitModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<HitModifierNpc>(StandardNpcHitModifier::class.java)
        bindBaseInstance<QueuedNpcHitProcessor>(StandardNpcHitProcessor::class.java)
    }
}
