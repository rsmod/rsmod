package gg.rsmod.plugins.api.cache.config.obj

import gg.rsmod.game.model.obj.ObjectType

private const val DEFAULT_ID = -1
private const val DEFAULT_NAME = "null"
private const val DEFAULT_LENGTH = 1
private const val DEFAULT_WIDTH = 1
private const val DEFAULT_BLOCK_PATH = true
private const val DEFAULT_BLOCK_PROJ = true
private const val DEFAULT_INTERACT = false
private const val DEFAULT_OBSTRUCT = false
private const val DEFAULT_CLIP_MASK = 0
private const val DEFAULT_VARP = -1
private const val DEFAULT_VARBIT = -1
private const val DEFAULT_ANIMATION = -1
private const val DEFAULT_ROTATED = false
private val DEFAULT_OPTIONS = emptyArray<String?>()
private val DEFAULT_TRANSFORMS = emptyArray<Int>()

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class ObjectTypeBuilder(
    var id: Int = DEFAULT_ID,
    var name: String = DEFAULT_NAME,
    var length: Int = DEFAULT_LENGTH,
    var width: Int = DEFAULT_WIDTH,
    var blockPath: Boolean = DEFAULT_BLOCK_PATH,
    var blockProjectile: Boolean = DEFAULT_BLOCK_PROJ,
    var interact: Boolean = DEFAULT_INTERACT,
    var obstruct: Boolean = DEFAULT_OBSTRUCT,
    var clipMask: Int = DEFAULT_CLIP_MASK,
    var varp: Int = DEFAULT_VARP,
    var varbit: Int = DEFAULT_VARBIT,
    var animation: Int = DEFAULT_ANIMATION,
    var rotated: Boolean = DEFAULT_ROTATED,
    var options: Array<String?> = DEFAULT_OPTIONS,
    var transforms: Array<Int> = DEFAULT_TRANSFORMS
) {

    val defaultOptions: Boolean
        get() = options === DEFAULT_OPTIONS

    fun build(): ObjectType {
        check(id != DEFAULT_ID) { "Object type id has not been set." }
        return ObjectType(
            id = id,
            name = name,
            length = length,
            width = width,
            blockPath = blockPath,
            blockProjectile = blockProjectile,
            interact = interact,
            obstruct = obstruct,
            clipMask = clipMask,
            varp = varp,
            varbit = varbit,
            animation = animation,
            rotated = rotated,
            options = options.toList(),
            transforms = transforms.toList()
        )
    }
}
