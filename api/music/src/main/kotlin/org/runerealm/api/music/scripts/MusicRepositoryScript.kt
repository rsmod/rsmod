package org.runerealm.api.music.scripts

import jakarta.inject.Inject
import org.runerealm.api.music.MusicRepository
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

internal class MusicRepositoryScript @Inject constructor(private val repo: MusicRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
        repo.load()
    }
}
