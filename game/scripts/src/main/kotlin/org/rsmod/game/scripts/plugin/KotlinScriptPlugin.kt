package org.rsmod.game.scripts.plugin

import com.google.inject.Injector
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Plugin Script",
    fileExtension = "plugin.kts"
)
public abstract class KotlinScriptPlugin(injector: Injector) : ScriptPlugin(injector)
