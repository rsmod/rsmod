package org.rsmod.api.spells.runes

import org.rsmod.api.spells.runes.combo.ComboRuneRepository
import org.rsmod.api.spells.runes.compact.CompactRuneRepository
import org.rsmod.api.spells.runes.fake.FakeRuneRepository
import org.rsmod.api.spells.runes.staves.StaffSubstituteRepository
import org.rsmod.api.spells.runes.unlimited.UnlimitedRuneRepository
import org.rsmod.plugin.module.PluginModule

internal class MagicRunesModule : PluginModule() {
    override fun bind() {
        bindInstance<ComboRuneRepository>()
        bindInstance<CompactRuneRepository>()
        bindInstance<FakeRuneRepository>()
        bindInstance<StaffSubstituteRepository>()
        bindInstance<UnlimitedRuneRepository>()
    }
}
