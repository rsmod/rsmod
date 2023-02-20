package org.rsmod.game.scripts.module

public sealed class ModuleBranch {

    public object Dev : ModuleBranch()
    public object Prod : ModuleBranch()
    public object Test : ModuleBranch()
}
