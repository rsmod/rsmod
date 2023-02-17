package org.rsmod.plugins.info.model

public sealed class ExtendedInfoBlock {

    /**
     * Extended info that will be sent the first time
     * a player views another.
     */
    public object InitStatic : ExtendedInfoBlock()

    /**
     * Extended info that will be sent if player has
     * seen other player before and may need to update
     * the info.
     */
    public object InitDynamic : ExtendedInfoBlock()
}
