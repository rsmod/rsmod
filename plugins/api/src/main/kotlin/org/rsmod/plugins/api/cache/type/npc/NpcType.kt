package org.rsmod.plugins.api.cache.type.npc

import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.param.ParamMap

public data class NpcType(
    override val id: Int,
    val name: String,
    val size: Int,
    val level: Int,
    val category: Int,
    val interact: Boolean,
    val minimapVisible: Boolean,
    val clickable: Boolean,
    val renderPriority: Boolean,
    val options: List<String?>,
    val readyAnim: Int,
    val walkAnim: Int,
    val walkLeftAnim: Int,
    val walkRightAnim: Int,
    val walkBackAnim: Int,
    val turnLeftAnim: Int,
    val turnRightAnim: Int,
    val varp: Int,
    val varbit: Int,
    val transforms: List<Int>,
    val defaultTransform: Int,
    val rotation: Int,
    val headIcon: Int,
    val models: List<Int>,
    val headModels: List<Int>,
    val recolorSrc: List<Int>,
    val recolorDest: List<Int>,
    val retextureSrc: List<Int>,
    val retextureDest: List<Int>,
    val resizeX: Int,
    val resizeY: Int,
    val contrast: Int,
    val ambient: Int,
    val isPet: Boolean,
    val params: ParamMap?
) : ConfigType
