@file:Suppress("konsist.properties are declared before functions")

package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.obj.Dummyitem
import org.rsmod.game.type.obj.ObjTypeBuilder
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.ObjWeight
import org.rsmod.game.type.util.ParamMapBuilder
import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType
import org.rsmod.game.type.varobjbit.VarObjBitType

@DslMarker private annotation class ObjBuilderDsl

@ObjBuilderDsl
public class ObjPluginBuilder(public var internal: String? = null) {
    private val backing: ObjTypeBuilder = ObjTypeBuilder()

    public var name: String? by backing::name
    public var desc: String? by backing::desc
    public var model: Int? by backing::model
    public var zoom2d: Int? by backing::zoom2d
    public var xan2d: Int? by backing::xan2d
    public var yan2d: Int? by backing::yan2d
    public var xof2d: Int? by backing::xof2d
    public var yof2d: Int? by backing::yof2d
    public var code9: Boolean? by backing::code9
    public var code10: Int? by backing::code10
    public var stackable: Boolean? by backing::stackable
    public var cost: Int? by backing::cost
    public var members: Boolean? by backing::members
    public var manwear: Int? by backing::manwear
    public var manwearOff: Int? by backing::manwearOff
    public var manwear2: Int? by backing::manwear2
    public var womanwear: Int? by backing::womanwear
    public var womanwearOff: Int? by backing::womanwearOff
    public var womanwear2: Int? by backing::womanwear2
    private var recolS: CompactableIntArray by backing::recolS
    private var recolD: CompactableIntArray by backing::recolD
    private var retexS: CompactableIntArray by backing::retexS
    private var retexD: CompactableIntArray by backing::retexD
    public var shiftclickiop: Int? by backing::shiftclickiop
    public var stockmarket: Boolean? by backing::stockmarket
    public var manwear3: Int? by backing::manwear3
    public var womanwear3: Int? by backing::womanwear3
    public var manhead: Int? by backing::manhead
    public var womanhead: Int? by backing::womanhead
    public var manhead2: Int? by backing::manhead2
    public var womanhead2: Int? by backing::womanhead2
    public var category: Int? by backing::category
    public var zan2d: Int? by backing::zan2d
    // TODO: find good way of defining countobjs within our builder
    private var countObj: CompactableIntArray by backing::countObj
    private var countCount: CompactableIntArray by backing::countCount
    public var resizeX: Int? by backing::resizeX
    public var resizeY: Int? by backing::resizeY
    public var resizeZ: Int? by backing::resizeZ
    public var ambient: Int? by backing::ambient
    public var contrast: Int? by backing::contrast
    public var team: Int? by backing::team
    public var boughtlink: Int? by backing::boughtlink
    public var boughttemplate: Int? by backing::boughttemplate
    public var param: ParamMapBuilder = ParamMapBuilder()
    public var playerCost: Int? by backing::playerCost
    public var playerCostDerived: Int? by backing::playerCostDerived
    public var playerCostDerivedConst: Int? by backing::playerCostDerivedConst
    public var stockMarketBuyLimit: Int? by backing::stockMarketBuyLimit
    public var stockMarketRecalcUsers: Int? by backing::stockMarketRecalcUsers
    public var tradeable: Boolean? by backing::tradeable
    public var respawnRate: Int? by backing::respawnRate
    private var objvarList: MutableList<VarObjBitType>? = null
    private var contentGroupId: Int? by backing::contentGroup

    public var objvar: MutableList<VarObjBitType>?
        get() = objvarList
        set(value) {
            objvarList = value
        }

    public var contentGroup: ContentGroupType?
        get() = null
        set(value) {
            contentGroupId = value?.id
        }

    public var dummyitem: Dummyitem?
        get() = Dummyitem[backing.dummyitem ?: 0]
        set(value) {
            backing.dummyitem = value?.id
        }

    public var certificate: Boolean
        get() = backing.generateCertificate == true
        set(value) {
            backing.generateCertificate = value
        }

    public var placeholder: Boolean
        get() = backing.generatePlaceholder == true
        set(value) {
            backing.generatePlaceholder = value
        }

    private val op: Array<String?>
        get() = backing.op

    private val iop: Array<String?>
        get() = backing.iop

    public val Int.oz: ObjWeight
        get() = ObjWeight.oz(this)

    public val Int.grams: ObjWeight
        get() = ObjWeight.grams(this)

    public val Int.kg: ObjWeight
        get() = ObjWeight.kg(this)

    public var weight: ObjWeight?
        get() = backing.weight?.let { ObjWeight(it) }
        set(value) {
            backing.weight = value?.grams
        }

    public var wearpos: Wearpos?
        get() = Wearpos.forSlot(backing.wearpos1 ?: -1)
        set(value) {
            backing.wearpos1 = value?.slot
        }

    public var wearpos2: Wearpos?
        get() = Wearpos.forSlot(backing.wearpos2 ?: -1)
        set(value) {
            backing.wearpos2 = value?.slot
        }

    public var wearpos3: Wearpos?
        get() = Wearpos.forSlot(backing.wearpos3 ?: -1)
        set(value) {
            backing.wearpos3 = value?.slot
        }

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

    public var iop1: String?
        get() = iop[0]
        set(value) {
            iop[0] = value
        }

    public var iop2: String?
        get() = iop[1]
        set(value) {
            iop[1] = value
        }

    public var iop3: String?
        get() = iop[2]
        set(value) {
            iop[2] = value
        }

    public var iop4: String?
        get() = iop[3]
        set(value) {
            iop[3] = value
        }

    public var iop5: String?
        get() = iop[4]
        set(value) {
            iop[4] = value
        }

    public var recol1s: Int
        get() = recolS[0]
        set(value) {
            recolS[0] = value
        }

    public var recol2s: Int
        get() = recolS[1]
        set(value) {
            recolS[1] = value
        }

    public var recol3s: Int
        get() = recolS[2]
        set(value) {
            recolS[2] = value
        }

    public var recol4s: Int
        get() = recolS[3]
        set(value) {
            recolS[3] = value
        }

    public var recol5s: Int
        get() = recolS[4]
        set(value) {
            recolS[4] = value
        }

    public var recol6s: Int
        get() = recolS[5]
        set(value) {
            recolS[5] = value
        }

    public var recol7s: Int
        get() = recolS[6]
        set(value) {
            recolS[6] = value
        }

    public var recol8s: Int
        get() = recolS[7]
        set(value) {
            recolS[7] = value
        }

    public var recol9s: Int
        get() = recolS[8]
        set(value) {
            recolS[8] = value
        }

    public var recol10s: Int
        get() = recolS[9]
        set(value) {
            recolS[9] = value
        }

    public var recol11s: Int
        get() = recolS[10]
        set(value) {
            recolS[10] = value
        }

    public var recol12s: Int
        get() = recolS[11]
        set(value) {
            recolS[11] = value
        }

    public var recol13s: Int
        get() = recolS[12]
        set(value) {
            recolS[12] = value
        }

    public var recol14s: Int
        get() = recolS[13]
        set(value) {
            recolS[13] = value
        }

    public var recol15s: Int
        get() = recolS[14]
        set(value) {
            recolS[14] = value
        }

    public var recol1d: Int
        get() = recolD[0]
        set(value) {
            recolD[0] = value
        }

    public var recol2d: Int
        get() = recolD[1]
        set(value) {
            recolD[1] = value
        }

    public var recol3d: Int
        get() = recolD[2]
        set(value) {
            recolD[2] = value
        }

    public var recol4d: Int
        get() = recolD[3]
        set(value) {
            recolD[3] = value
        }

    public var recol5d: Int
        get() = recolD[4]
        set(value) {
            recolD[4] = value
        }

    public var recol6d: Int
        get() = recolD[5]
        set(value) {
            recolD[5] = value
        }

    public var recol7d: Int
        get() = recolD[6]
        set(value) {
            recolD[6] = value
        }

    public var recol8d: Int
        get() = recolD[7]
        set(value) {
            recolD[7] = value
        }

    public var recol9d: Int
        get() = recolD[8]
        set(value) {
            recolD[8] = value
        }

    public var recol10d: Int
        get() = recolD[9]
        set(value) {
            recolD[9] = value
        }

    public var recol11d: Int
        get() = recolD[10]
        set(value) {
            recolD[10] = value
        }

    public var recol12d: Int
        get() = recolD[11]
        set(value) {
            recolD[11] = value
        }

    public var recol13d: Int
        get() = recolD[12]
        set(value) {
            recolD[12] = value
        }

    public var recol14d: Int
        get() = recolD[13]
        set(value) {
            recolD[13] = value
        }

    public var recol15d: Int
        get() = recolD[14]
        set(value) {
            recolD[14] = value
        }

    public var retex1s: Int
        get() = retexS[0]
        set(value) {
            retexS[0] = value
        }

    public var retex2s: Int
        get() = retexS[1]
        set(value) {
            retexS[1] = value
        }

    public var retex3s: Int
        get() = retexS[2]
        set(value) {
            retexS[2] = value
        }

    public var retex4s: Int
        get() = retexS[3]
        set(value) {
            retexS[3] = value
        }

    public var retex5s: Int
        get() = retexS[4]
        set(value) {
            retexS[4] = value
        }

    public var retex6s: Int
        get() = retexS[5]
        set(value) {
            retexS[5] = value
        }

    public var retex7s: Int
        get() = retexS[6]
        set(value) {
            retexS[6] = value
        }

    public var retex8s: Int
        get() = retexS[7]
        set(value) {
            retexS[7] = value
        }

    public var retex9s: Int
        get() = retexS[8]
        set(value) {
            retexS[8] = value
        }

    public var retex10s: Int
        get() = retexS[9]
        set(value) {
            retexS[9] = value
        }

    public var retex11s: Int
        get() = retexS[10]
        set(value) {
            retexS[10] = value
        }

    public var retex12s: Int
        get() = retexS[11]
        set(value) {
            retexS[11] = value
        }

    public var retex13s: Int
        get() = retexS[12]
        set(value) {
            retexS[12] = value
        }

    public var retex14s: Int
        get() = retexS[13]
        set(value) {
            retexS[13] = value
        }

    public var retex15s: Int
        get() = retexS[14]
        set(value) {
            retexS[14] = value
        }

    public var retex1d: Int
        get() = retexD[0]
        set(value) {
            retexD[0] = value
        }

    public var retex2d: Int
        get() = retexD[1]
        set(value) {
            retexD[1] = value
        }

    public var retex3d: Int
        get() = retexD[2]
        set(value) {
            retexD[2] = value
        }

    public var retex4d: Int
        get() = retexD[3]
        set(value) {
            retexD[3] = value
        }

    public var retex5d: Int
        get() = retexD[4]
        set(value) {
            retexD[4] = value
        }

    public var retex6d: Int
        get() = retexD[5]
        set(value) {
            retexD[5] = value
        }

    public var retex7d: Int
        get() = retexD[6]
        set(value) {
            retexD[6] = value
        }

    public var retex8d: Int
        get() = retexD[7]
        set(value) {
            retexD[7] = value
        }

    public var retex9d: Int
        get() = retexD[8]
        set(value) {
            retexD[8] = value
        }

    public var retex10d: Int
        get() = retexD[9]
        set(value) {
            retexD[9] = value
        }

    public var retex11d: Int
        get() = retexD[10]
        set(value) {
            retexD[10] = value
        }

    public var retex12d: Int
        get() = retexD[11]
        set(value) {
            retexD[11] = value
        }

    public var retex13d: Int
        get() = retexD[12]
        set(value) {
            retexD[12] = value
        }

    public var retex14d: Int
        get() = retexD[13]
        set(value) {
            retexD[13] = value
        }

    public var retex15d: Int
        get() = retexD[14]
        set(value) {
            retexD[14] = value
        }

    public fun build(id: Int): UnpackedObjType {
        val objvars = objvarList
        backing.internal = internal
        if (param.isNotEmpty()) {
            backing.paramMap = param.toParamMap()
        }
        if (objvars?.isNotEmpty() == true) {
            val filtered = objvars.filterIsInstance<UnpackedVarObjBitType>()
            val msb = filtered.maxOf { it.endBit }
            val overlapping =
                filtered
                    .sortedBy { it.startBit }
                    .zipWithNext()
                    .any { (a, b) -> a.endBit >= b.startBit }
            check(msb < Int.SIZE_BITS) { "Most-significant bit cannot be greater than 31." }
            check(!overlapping) { "Bits from `objvar` list must not overlap: $filtered" }
            val mapped = filtered.mapNotNull { TypeResolver[it] }
            backing.objvar = CompactableIntArray(mapped.toIntArray())
        }
        return backing.build(id)
    }

    public operator fun VarObjBitType.unaryMinus(): MutableList<VarObjBitType> = mutableListOf(this)

    public operator fun VarObjBitType.plus(other: VarObjBitType): MutableList<VarObjBitType> =
        mutableListOf(this, other)

    public operator fun MutableList<VarObjBitType>.plus(
        other: VarObjBitType
    ): MutableList<VarObjBitType> = apply { add(other) }

    public val hat: Wearpos = Wearpos.Hat
    public val back: Wearpos = Wearpos.Back
    public val front: Wearpos = Wearpos.Front
    public val righthand: Wearpos = Wearpos.RightHand
    public val torso: Wearpos = Wearpos.Torso
    public val lefthand: Wearpos = Wearpos.LeftHand
    public val arms: Wearpos = Wearpos.Arms
    public val legs: Wearpos = Wearpos.Legs
    public val head: Wearpos = Wearpos.Head
    public val hands: Wearpos = Wearpos.Hands
    public val feet: Wearpos = Wearpos.Feet
    public val jaw: Wearpos = Wearpos.Jaw
    public val ring: Wearpos = Wearpos.Ring
    public val quiver: Wearpos = Wearpos.Quiver
}
