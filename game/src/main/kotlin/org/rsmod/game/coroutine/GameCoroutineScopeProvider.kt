package org.rsmod.game.coroutine

import org.rsmod.game.coroutines.GameCoroutineScope
import javax.inject.Provider

public class GameCoroutineScopeProvider : Provider<GameCoroutineScope> {

    override fun get(): GameCoroutineScope {
        // TODO: add game config to set this flag by default
        return GameCoroutineScope(superviseCoroutines = false)
    }
}
