package org.rsmod.game.model

public sealed class GameEnv {

    public object Dev : GameEnv()
    public object Prod : GameEnv()
    public object Test : GameEnv()

    public override fun toString(): String {
        return javaClass.simpleName
    }
}
