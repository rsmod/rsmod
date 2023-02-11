package org.rsmod.plugins.info.player.extended2

import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.APPEARANCE_MAX_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.CHAT_MAX_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.EXACT_MOVE_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.FACE_DIRECTION_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.FACE_ENTITY_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.HIT_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.MOVE_SPEED_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.NAME_PREFIX_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.SAY_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.SEQUENCE_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.SPOTANIM_BYTE_SIZE
import org.rsmod.plugins.info.player.extended2.ExtendedInfoSizes.TINTING_BYTE_SIZE

public enum class ExtendedInfo(public val byteSize: Int) {
    MoveSpeed(MOVE_SPEED_BYTE_SIZE),
    FaceDirection(FACE_DIRECTION_BYTE_SIZE),
    FaceEntity(FACE_ENTITY_BYTE_SIZE),
    Sequence(SEQUENCE_BYTE_SIZE),
    Spotanim(SPOTANIM_BYTE_SIZE),
    Tinting(TINTING_BYTE_SIZE),
    ExactMove(EXACT_MOVE_BYTE_SIZE),
    Appearance(APPEARANCE_MAX_BYTE_SIZE),
    Prefix(NAME_PREFIX_BYTE_SIZE),
    Chat(CHAT_MAX_BYTE_SIZE),
    Say(SAY_BYTE_SIZE),
    Hit(HIT_BYTE_SIZE);

    public val index: Int = ordinal

    public companion object {

        public val values: Array<ExtendedInfo> = enumValues()
    }
}
