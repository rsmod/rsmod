package gg.rsmod.game.plugin.kotlin

import com.google.inject.Inject
import com.google.inject.Injector
import gg.rsmod.game.action.ActionBus
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.model.cmd.CommandMap
import gg.rsmod.game.plugin.Plugin
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

@KotlinScript(
    displayName = "KotlinScript Plugin",
    fileExtension = "plugin.kts",
    compilationConfiguration = KotlinPluginConfiguration::class
)
abstract class KotlinPlugin(
    injector: Injector,
    eventBus: EventBus,
    actions: ActionBus,
    commands: CommandMap
) : Plugin(injector, eventBus, actions, commands)

object KotlinPluginConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        Inject::class.qualifiedName!!
    )
})
