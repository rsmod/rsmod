package org.runerealm.api.core.module

import org.runerealm.api.npc.hit.modifier.NpcHitModifier
import org.runerealm.api.npc.hit.modifier.StandardNpcHitModifier
import org.runerealm.api.npc.hit.processor.NpcHitProcessor
import org.runerealm.api.npc.hit.processor.StandardNpcHitProcessor
import org.runerealm.api.player.hit.processor.DamageOnlyPlayerHitProcessor
import org.runerealm.api.player.hit.processor.InstantPlayerHitProcessor
import org.runerealm.module.ExtendedModule

public object EntityHitModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<NpcHitModifier>(StandardNpcHitModifier::class.java)
        bindBaseInstance<NpcHitProcessor>(StandardNpcHitProcessor::class.java)
        bindBaseInstance<InstantPlayerHitProcessor>(DamageOnlyPlayerHitProcessor::class.java)
    }
}
