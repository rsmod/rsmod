package org.rsmod.game.plugins.module.branch

public sealed class ModuleBranch {

    public object Dev : ModuleBranch()
    public object Prod : ModuleBranch()
    public object Test : ModuleBranch()
}
