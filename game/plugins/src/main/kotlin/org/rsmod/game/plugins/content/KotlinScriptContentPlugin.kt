package org.rsmod.game.plugins.content

import com.google.inject.Injector
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Content Plugin",
    fileExtension = "content.kts"
)
public abstract class KotlinScriptContentPlugin(
    injector: Injector
) : ContentPlugin(injector)
