package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.ParamMapBuilder
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType

@DslMarker private annotation class LocBuilderDsl

@LocBuilderDsl
public class LocPluginBuilder(public var internal: String? = null) {
    private val backing: LocTypeBuilder = LocTypeBuilder()

    // TODO: Change to wrapper class with ModelType and LocShape.
    private var models: CompactableIntArray by backing::model
    private var shapes: CompactableIntArray by backing::modelShape
    public var name: String? by backing::name
    public var desc: String? by backing::desc
    public var width: Int? by backing::width
    public var length: Int? by backing::length
    // TODO: Find proper names for different types and create an enum to use here.
    public var blockWalk: Int? by backing::blockWalk
    public var blockRange: Boolean? by backing::blockRange
    // TODO: Research to find when locs have different values for this field.
    public var active: Int? by backing::active
    public var hillSkew: Int? by backing::hillSkew
    public var shareLight: Boolean? by backing::shareLight
    public var occlude: Boolean? by backing::occlude
    public var wallWidth: Int? by backing::wallWidth
    public var ambient: Int? by backing::ambient
    public var contrast: Int? by backing::contrast
    private val op: Array<String?> by backing::op
    public var recolS: CompactableIntArray by backing::recolS
    public var recolD: CompactableIntArray by backing::recolD
    public var retexS: CompactableIntArray by backing::retexS
    public var mirror: Boolean? by backing::mirror
    public var shadow: Boolean? by backing::shadow
    public var resizeX: Int? by backing::resizeX
    public var resizeY: Int? by backing::resizeY
    public var resizeZ: Int? by backing::resizeZ
    public var mapscene: Int? by backing::mapscene
    // TODO: Figure out best way to have these declared. (i.e., forceApproach = north + south)
    public var forceApproachFlags: Int? by backing::forceApproachFlags
    public var offsetX: Int? by backing::offsetX
    public var offsetY: Int? by backing::offsetY
    public var offsetZ: Int? by backing::offsetZ
    public var forceDecor: Boolean? by backing::forceDecor
    public var breakRouteFinding: Boolean? by backing::breakRouteFinding
    public var raiseObject: Int? by backing::raiseObject
    public var anim: SeqType? by relay { backing.anim = it?.id }
    public var category: CategoryType? by relay { backing.category = it?.id }
    public var multiVarBit: VarBitType? by relay { backing.multiVarBit = it?.id }
    public var multiVarp: VarpType? by relay { backing.multiVarp = it?.id }
    public var multiLocDefault: LocType? by relay { backing.multiLocDefault = it?.id }
    public val multiLoc: RelayArray<LocType?> = relayIndexed { index, type ->
        backing.multiLoc[index] = type?.id ?: LocTypeBuilder.DEFAULT_MULTI_LOC_DEFAULT
    }
    public var bgsoundSound: SynthType? by relay { backing.bgsoundSound = it?.id }
    public var bgsoundRange: Int? by backing::bgsoundRange
    public var bgsoundSize: Int? by backing::bgsoundSize
    public var bgsoundMinDelay: Int? by backing::bgsoundMinDelay
    public var bgsoundMaxDelay: Int? by backing::bgsoundMaxDelay
    public val bgsoundRandomSound: RelayArray<SynthType> = relayIndexed { index, type ->
        backing.bgsoundRandomSound[index] = type.id
    }
    public var treeSkew: Int? by backing::treeSkew
    public var mapIcon: Int? by backing::mapIcon
    public var randomAnimFrame: Boolean? by backing::randomAnimFrame
    public var fixLocAnimAfterLocChange: Boolean? by backing::fixLocAnimAfterLocChange
    public var contentGroup: ContentGroupType? by relay { backing.contentGroup = it?.id }
    public var param: ParamMapBuilder = ParamMapBuilder()

    public var op1: String?
        get() = op[0]
        set(value) {
            op[0] = value
        }

    public var op2: String?
        get() = op[1]
        set(value) {
            op[1] = value
        }

    public var op3: String?
        get() = op[2]
        set(value) {
            op[2] = value
        }

    public var op4: String?
        get() = op[3]
        set(value) {
            op[3] = value
        }

    public var op5: String?
        get() = op[4]
        set(value) {
            op[4] = value
        }

    public fun build(id: Int): UnpackedLocType {
        backing.internal = internal
        if (param.isNotEmpty()) {
            backing.paramMap = param.toParamMap()
        }
        return backing.build(id)
    }
}
