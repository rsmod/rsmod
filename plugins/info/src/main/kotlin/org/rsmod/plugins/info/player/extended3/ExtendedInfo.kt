package org.rsmod.plugins.info.player.extended3

public enum class ExtendedInfo {
    MoveSpeed,
    FaceDirection,
    FaceEntity,
    Sequence,
    Spotanim,
    Tinting,
    ExactMove,
    Appearance,
    Prefix,
    Chat,
    Say,
    Hit;

    public val index: Int = ordinal

    public companion object {

        public val values: Array<ExtendedInfo> = enumValues()
    }
}
