package org.rsmod.game.entity.player

public data class PublicMessage(
    val text: String,
    val colour: Int,
    val effect: Int,
    val clanType: Int?,
    val modIcon: Int,
    val autoTyper: Boolean,
    val pattern: ByteArray?,
)
