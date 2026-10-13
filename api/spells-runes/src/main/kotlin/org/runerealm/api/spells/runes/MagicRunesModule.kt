package org.runerealm.api.spells.runes

import org.runerealm.api.spells.runes.combo.ComboRuneRepository
import org.runerealm.api.spells.runes.compact.CompactRuneRepository
import org.runerealm.api.spells.runes.fake.FakeRuneRepository
import org.runerealm.api.spells.runes.staves.StaffSubstituteRepository
import org.runerealm.api.spells.runes.subs.RuneSubstituteRepository
import org.runerealm.api.spells.runes.unlimited.UnlimitedRuneRepository
import org.runerealm.plugin.module.PluginModule

internal class MagicRunesModule : PluginModule() {
    override fun bind() {
        bindInstance<ComboRuneRepository>()
        bindInstance<CompactRuneRepository>()
        bindInstance<FakeRuneRepository>()
        bindInstance<RuneSubstituteRepository>()
        bindInstance<StaffSubstituteRepository>()
        bindInstance<UnlimitedRuneRepository>()
    }
}
