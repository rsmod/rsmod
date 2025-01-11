package org.rsmod.api.registry.obj

import org.rsmod.game.obj.Obj

public sealed class ObjRegistryResult {
    public class Merge(public val merged: Obj) : ObjRegistryResult()

    public class Split(public val split: List<Obj>) : ObjRegistryResult()

    public data object Stack : ObjRegistryResult()
}
