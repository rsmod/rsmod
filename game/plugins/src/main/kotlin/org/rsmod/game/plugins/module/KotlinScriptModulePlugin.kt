package org.rsmod.game.plugins.module

import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Module Plugin",
    fileExtension = "module.kts"
)
public abstract class KotlinScriptModulePlugin : ModulePlugin()
