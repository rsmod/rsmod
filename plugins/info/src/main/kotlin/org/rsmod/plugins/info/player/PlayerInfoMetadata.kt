package org.rsmod.plugins.info.player

public data class PlayerInfoMetadata(
    public var highResolutionCount: Int = 0,
    public var highResolutionSkip: Int = 0,
    public var lowResolutionCount: Int = 0,
    public var lowResolutionSkip: Int = 0,
    public var extendedInfoCount: Int = 0,
    public var extendedInfoLength: Int = 0
)
