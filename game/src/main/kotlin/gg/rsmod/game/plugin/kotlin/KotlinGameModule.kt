package gg.rsmod.game.plugin.kotlin

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import kotlin.script.experimental.annotations.KotlinScript

private val logger = InlineLogger()

@KotlinScript(
    displayName = "KotlinScript Module",
    fileExtension = "module.kts"
)
abstract class KotlinGameModule(val scope: Scope) {

    val modules: MutableList<KotlinModule> = mutableListOf()

    fun register(init: GameModuleBuilder.() -> Unit) {
        GameModuleBuilder(modules).apply(init)
    }
}

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class GameModuleBuilder(
    private val modules: MutableList<KotlinModule> = mutableListOf()
) {
    operator fun KotlinModule.unaryMinus() {
        logger.debug { "Append module to builder (module=${this::class.simpleName})" }
        modules.add(this)
    }
}
