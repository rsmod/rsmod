package org.rsmod.api.music.scripts

import jakarta.inject.Inject
import org.rsmod.api.music.MusicRepository
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class MusicRepositoryScript @Inject constructor(private val repo: MusicRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        repo.load()
    }
}
