package org.runerealm.api.cheat

import com.github.michaelbull.logging.InlineLogger
import org.runerealm.api.player.output.mes
import org.runerealm.game.cheat.Cheat
import org.runerealm.game.cheat.CheatHandler
import org.runerealm.game.type.mod.ModLevelType

private val logger = InlineLogger()

@DslMarker private annotation class CheatBuilderDsl

@CheatBuilderDsl
public class CheatHandlerBuilder(public val command: String) {
    public var desc: String? = null
    public var modLevel: ModLevelType? = null
    public var invalidArgs: String? = null
    public var invalidModLevel: String? = null
    public var exception: String? = DEFAULT_EXCEPTION

    private var cheat: (Cheat.() -> Unit)? = null

    public fun build(): CheatHandler {
        val cheat = cheat ?: error("`cheat` must be set.")
        val desc = desc ?: error("`desc` must be set.")
        val argsErr = invalidArgs ?: DEFAULT_ARG_ERR
        val action = wrapCheat(argsErr, invalidModLevel, exception, modLevel, cheat)
        return CheatHandler(desc, action)
    }

    public fun cheat(cheat: Cheat.() -> Unit) {
        this.cheat = cheat
    }

    private fun wrapCheat(
        invalidArgsMsg: String,
        modLevelMsg: String?,
        exceptionMsg: String?,
        modLevel: ModLevelType?,
        cheat: Cheat.() -> Unit,
    ): Cheat.() -> Unit = action@{
        if (modLevel != null && !player.modLevel.hasAccessTo(modLevel)) {
            modLevelMsg?.let(player::mes)
            return@action
        }
        try {
            cheat()
        } catch (_: NumberFormatException) {
            player.mes(invalidArgsMsg)
        } catch (_: IndexOutOfBoundsException) {
            player.mes(invalidArgsMsg)
        } catch (e: Exception) {
            exceptionMsg?.let(player::mes)
            logger.error(e) { "Error executing command `$command` for player: $player." }
        }
    }

    public companion object {
        public const val DEFAULT_ARG_ERR: String = "Invalid arguments!"
        public const val DEFAULT_EXCEPTION: String =
            "Uncaught exception! Please report this to an Administrator."
    }
}
