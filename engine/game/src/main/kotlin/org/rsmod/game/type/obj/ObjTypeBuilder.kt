package org.rsmod.game.type.obj

import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectIntArray
import org.rsmod.game.type.util.GenericPropertySelector.selectParamMap
import org.rsmod.game.type.util.GenericPropertySelector.selectPredicate
import org.rsmod.game.type.util.GenericPropertySelector.selectShortArray
import org.rsmod.game.type.util.ParamMap

@DslMarker private annotation class ObjBuilderDsl

@ObjBuilderDsl
public class ObjTypeBuilder(public var internal: String? = null) {
    public var name: String? = null
    public var desc: String? = null
    public var model: Int? = null
    public var zoom2d: Int? = null
    public var xan2d: Int? = null
    public var yan2d: Int? = null
    public var xof2d: Int? = null
    public var yof2d: Int? = null
    public var code9: Boolean? = null
    public var code10: Int? = null
    public var stackable: Boolean? = null
    public var cost: Int? = null
    public var wearpos1: Int? = null
    public var wearpos2: Int? = null
    public var members: Boolean? = null
    public var manwear: Int? = null
    public var manwearOff: Int? = null
    public var manwear2: Int? = null
    public var womanwear: Int? = null
    public var womanwearOff: Int? = null
    public var womanwear2: Int? = null
    public var wearpos3: Int? = null
    public val op: Array<String?> = arrayOfNulls(OP_CAPACITY)
    public val iop: Array<String?> = arrayOfNulls(IOP_CAPACITY)
    public var recolS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var recolD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexS: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var retexD: CompactableIntArray = CompactableIntArray(RECOL_CAPACITY)
    public var shiftclickiop: Int? = null
    public var isubop1: Array<String>? = null
    public var isubop2: Array<String>? = null
    public var isubop3: Array<String>? = null
    public var isubop4: Array<String>? = null
    public var isubop5: Array<String>? = null
    public var stockmarket: Boolean? = null
    public var weight: Int? = null
    public var manwear3: Int? = null
    public var womanwear3: Int? = null
    public var manhead: Int? = null
    public var womanhead: Int? = null
    public var manhead2: Int? = null
    public var womanhead2: Int? = null
    public var category: Int? = null
    public var zan2d: Int? = null
    public var certlink: Int? = null
    public var certtemplate: Int? = null
    public var countObj: CompactableIntArray = CompactableIntArray(COUNT_CAPACITY)
    public var countCount: CompactableIntArray = CompactableIntArray(COUNT_CAPACITY)
    public var resizeX: Int? = null
    public var resizeY: Int? = null
    public var resizeZ: Int? = null
    public var ambient: Int? = null
    public var contrast: Int? = null
    public var team: Int? = null
    public var boughtlink: Int? = null
    public var boughttemplate: Int? = null
    public var placeholderlink: Int? = null
    public var placeholdertemplate: Int? = null
    public var paramMap: ParamMap? = null
    public var generateCertificate: Boolean? = false
    public var generatePlaceholder: Boolean? = false
    public var objvar: CompactableIntArray = CompactableIntArray()
    public var playerCost: Int? = null
    public var playerCostDerived: Int? = null
    public var playerCostDerivedConst: Int? = null
    public var stockMarketBuyLimit: Int? = null
    public var stockMarketRecalcUsers: Int? = null
    public var tradeable: Boolean? = null
    public var respawnRate: Int? = null
    public var dummyitem: Int? = null
    public var contentGroup: Int? = null
    public var weaponCategory: Int? = null
    public var transformlink: Int? = null
    public var transformtemplate: Int? = null

    public fun build(id: Int): UnpackedObjType {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val name = name ?: DEFAULT_NAME
        val desc = desc ?: ""
        val model = model ?: 0
        val zoom2d = zoom2d ?: DEFAULT_ZOOM2D
        val xan2d = xan2d ?: 0
        val yan2d = yan2d ?: 0
        val xof2d = xof2d ?: 0
        val yof2d = yof2d ?: 0
        val stackable = stackable == true
        val cost = cost ?: DEFAULT_COST
        val wearpos1 = wearpos1 ?: DEFAULT_WEARPOS
        val wearpos2 = wearpos2 ?: DEFAULT_WEARPOS2
        val wearpos3 = wearpos3 ?: DEFAULT_WEARPOS3
        val members = members == true
        val manwear = manwear ?: DEFAULT_MANWEAR
        val manwearOff = manwearOff ?: 0
        val manwear2 = manwear2 ?: DEFAULT_MANWEAR2
        val womanwear = womanwear ?: DEFAULT_WOMANWEAR
        val womanwearOff = womanwearOff ?: 0
        val womanwear2 = womanwear2 ?: DEFAULT_WOMANWEAR2
        val manwear3 = manwear3 ?: DEFAULT_MANWEAR3
        val womanwear3 = womanwear3 ?: DEFAULT_WOMANWEAR3
        val manhead = manhead ?: DEFAULT_MANHEAD
        val womanhead = womanhead ?: DEFAULT_WOMANHEAD
        val manhead2 = manhead2 ?: DEFAULT_MANHEAD2
        val womanhead2 = womanhead2 ?: DEFAULT_WOMANHEAD2
        val category = category ?: DEFAULT_CATEGORY
        val zan2d = zan2d ?: 0
        val certlink = certlink ?: 0
        val certtemplate = certtemplate ?: 0
        val resizeX = resizeX ?: DEFAULT_RESIZE_X
        val resizeY = resizeY ?: DEFAULT_RESIZE_Y
        val resizeZ = resizeZ ?: DEFAULT_RESIZE_Z
        val ambient = ambient ?: 0
        val contrast = contrast ?: 0
        val team = team ?: 0
        val boughtlink = boughtlink ?: 0
        val boughttemplate = boughttemplate ?: 0
        val placeholderlink = placeholderlink ?: 0
        val placeholdertemplate = placeholdertemplate ?: 0
        val shiftclickiop = shiftclickiop ?: DEFAULT_SHIFTCLICKIOP
        val stockmarket = stockmarket == true
        val weight = weight ?: 0
        val generateCertificate = generateCertificate == true
        val generatePlaceholder = generatePlaceholder == true
        val playerCost = playerCost ?: 0
        val playerCostDerived = playerCostDerived ?: 0
        val playerCostDerivedConst = playerCostDerivedConst ?: 0
        val stockMarketBuyLimit = stockMarketBuyLimit ?: 0
        val stockMarketRecalcUsers = stockMarketRecalcUsers ?: 0
        val tradeable = tradeable ?: DEFAULT_TRADEABLE
        val respawnRate = respawnRate ?: DEFAULT_RESPAWN_RATE
        val dummyitem = dummyitem ?: DEFAULT_DUMMYITEM
        val contentGroup = contentGroup ?: DEFAULT_CONTENT_GROUP
        val weaponCategory = weaponCategory ?: defaultWeaponCategory().id
        val transformlink = transformlink ?: 0
        val transformtemplate = transformtemplate ?: 0
        return UnpackedObjType(
            name = name,
            desc = desc,
            model = model,
            zoom2d = zoom2d,
            xan2d = xan2d,
            yan2d = yan2d,
            xof2d = xof2d,
            yof2d = yof2d,
            stackable = stackable,
            cost = cost,
            wearpos1 = wearpos1,
            wearpos2 = wearpos2,
            members = members,
            manwear = manwear,
            manwearOff = manwearOff,
            manwear2 = manwear2,
            womanwear = womanwear,
            womanwearOff = womanwearOff,
            womanwear2 = womanwear2,
            wearpos3 = wearpos3,
            op = op.copyOf(),
            iop = iop.copyOf(),
            recolS = recolS.toShortArray(),
            recolD = recolD.toShortArray(),
            retexS = retexS.toShortArray(),
            retexD = retexD.toShortArray(),
            shiftclickiop = shiftclickiop,
            isubop1 = isubop1,
            isubop2 = isubop2,
            isubop3 = isubop3,
            isubop4 = isubop4,
            isubop5 = isubop5,
            stockmarket = stockmarket,
            weight = weight,
            manwear3 = manwear3,
            womanwear3 = womanwear3,
            manhead = manhead,
            womanhead = womanhead,
            manhead2 = manhead2,
            womanhead2 = womanhead2,
            category = category,
            zan2d = zan2d,
            certlink = certlink,
            certtemplate = certtemplate,
            countObj = countObj.toIntArray(),
            countCount = countCount.toIntArray(),
            resizeX = resizeX,
            resizeY = resizeY,
            resizeZ = resizeZ,
            ambient = ambient,
            contrast = contrast,
            team = team,
            boughtlink = boughtlink,
            boughttemplate = boughttemplate,
            placeholderlink = placeholderlink,
            placeholdertemplate = placeholdertemplate,
            paramMap = paramMap,
            generateCertificate = generateCertificate,
            generatePlaceholder = generatePlaceholder,
            objvar = objvar.toIntArray(),
            playerCost = playerCost,
            playerCostDerived = playerCostDerived,
            playerCostDerivedConst = playerCostDerivedConst,
            stockMarketBuyLimit = stockMarketBuyLimit,
            stockMarketRecalcUsers = stockMarketRecalcUsers,
            tradeable = tradeable,
            respawnRate = respawnRate,
            dummyitem = dummyitem,
            contentGroup = contentGroup,
            weaponCategory = weaponCategory,
            transformlink = transformlink,
            transformtemplate = transformtemplate,
            internalId = id,
            internalName = internal,
        )
    }

    private fun defaultWeaponCategory(): WeaponCategory {
        if (wearpos1 != Wearpos.RightHand.slot) {
            return WeaponCategory.Unarmed
        }
        return when (category) {
            21 -> WeaponCategory.SlashSword
            25 -> WeaponCategory.StabSword
            24 -> WeaponCategory.Thrown
            37 -> WeaponCategory.Crossbow
            567 -> WeaponCategory.Crossbow
            26 -> WeaponCategory.Blunt
            55 -> WeaponCategory.Blunt
            64 -> WeaponCategory.Bow
            106 -> WeaponCategory.Bow
            36 -> WeaponCategory.Spear
            67 -> WeaponCategory.Pickaxe
            61 -> WeaponCategory.TwoHandedSword
            35 -> WeaponCategory.Axe
            1 -> WeaponCategory.Staff
            1193 -> WeaponCategory.Scythe
            39 -> WeaponCategory.Spiked
            65 -> WeaponCategory.Claw
            66 -> WeaponCategory.Polearm
            42 -> WeaponCategory.Banner
            150 -> WeaponCategory.Whip
            96 -> WeaponCategory.Gun
            92 -> WeaponCategory.Polestaff
            572 -> WeaponCategory.Chinchompas
            586 -> WeaponCategory.Salamander
            1014 -> WeaponCategory.Bulwark
            else -> WeaponCategory.Unarmed
        }
    }

    public companion object {
        public const val DEFAULT_NAME: String = "null"
        public const val DEFAULT_ZOOM2D: Int = 2000
        public const val DEFAULT_COST: Int = 1
        public const val DEFAULT_MANWEAR: Int = -1
        public const val DEFAULT_MANWEAR2: Int = -1
        public const val DEFAULT_WOMANWEAR: Int = -1
        public const val DEFAULT_WOMANWEAR2: Int = -1
        public const val DEFAULT_MANWEAR3: Int = -1
        public const val DEFAULT_WOMANWEAR3: Int = -1
        public const val DEFAULT_MANHEAD: Int = -1
        public const val DEFAULT_MANHEAD2: Int = -1
        public const val DEFAULT_WOMANHEAD: Int = -1
        public const val DEFAULT_WOMANHEAD2: Int = -1
        public const val DEFAULT_CATEGORY: Int = -1
        public const val DEFAULT_WEARPOS: Int = -1
        public const val DEFAULT_WEARPOS2: Int = -1
        public const val DEFAULT_WEARPOS3: Int = -1
        public const val DEFAULT_RESIZE_X: Int = 128
        public const val DEFAULT_RESIZE_Y: Int = 128
        public const val DEFAULT_RESIZE_Z: Int = 128
        public const val DEFAULT_SHIFTCLICKIOP: Int = -1
        public const val DEFAULT_TRADEABLE: Boolean = true
        public const val DEFAULT_RESPAWN_RATE: Int = 100
        public const val DEFAULT_DUMMYITEM: Int = -1
        public const val DEFAULT_CONTENT_GROUP: Int = -1

        public const val OP_CAPACITY: Int = 5
        public const val IOP_CAPACITY: Int = 5
        public const val COUNT_CAPACITY: Int = 10
        public const val RECOL_CAPACITY: Int = 15

        public fun merge(edit: UnpackedObjType, base: UnpackedObjType): UnpackedObjType {
            val name = select(edit, base, DEFAULT_NAME) { name }
            val desc = selectPredicate(edit.desc, base.desc) { edit.desc.isNotBlank() }
            val model = select(edit, base, default = 0) { model }
            val zoom2d = select(edit, base, DEFAULT_ZOOM2D) { zoom2d }
            val xan2d = select(edit, base, default = 0) { xan2d }
            val yan2d = select(edit, base, default = 0) { yan2d }
            val xof2d = select(edit, base, default = 0) { xof2d }
            val yof2d = select(edit, base, default = 0) { yof2d }
            val stackable = select(edit, base, default = false) { stackable }
            val cost = select(edit, base, DEFAULT_COST) { cost }
            val wearpos1 = select(edit, base, DEFAULT_WEARPOS) { wearpos1 }
            val wearpos2 = select(edit, base, DEFAULT_WEARPOS) { wearpos2 }
            val members = select(edit, base, default = false) { members }
            val manwear = select(edit, base, DEFAULT_MANWEAR) { manwear }
            val manwearOff = select(edit, base, default = 0) { manwearOff }
            val manwear2 = select(edit, base, DEFAULT_MANWEAR2) { manwear2 }
            val womanwear = select(edit, base, DEFAULT_WOMANWEAR) { womanwear }
            val womanwearOff = select(edit, base, default = 0) { womanwearOff }
            val womanwear2 = select(edit, base, DEFAULT_WOMANWEAR2) { womanwear2 }
            val wearpos3 = select(edit, base, DEFAULT_WEARPOS3) { wearpos3 }
            val op = selectPredicate(edit.op, base.op) { edit.op.any { it != null } }
            val iop = selectPredicate(edit.iop, base.iop) { edit.iop.any { it != null } }
            val recolS = selectShortArray(edit, base) { recolS }
            val recolD = selectShortArray(edit, base) { recolD }
            val retexS = selectShortArray(edit, base) { retexS }
            val retexD = selectShortArray(edit, base) { retexD }
            val shiftclickiop = select(edit, base, DEFAULT_SHIFTCLICKIOP) { shiftclickiop }
            val isubop1 = select(edit, base, default = null) { isubop1 }
            val isubop2 = select(edit, base, default = null) { isubop2 }
            val isubop3 = select(edit, base, default = null) { isubop3 }
            val isubop4 = select(edit, base, default = null) { isubop4 }
            val isubop5 = select(edit, base, default = null) { isubop5 }
            val stockmarket = select(edit, base, default = false) { stockmarket }
            val weight = select(edit, base, default = 0) { weight }
            val manwear3 = select(edit, base, DEFAULT_MANWEAR3) { manwear3 }
            val womanwear3 = select(edit, base, DEFAULT_WOMANWEAR3) { womanwear3 }
            val manhead = select(edit, base, DEFAULT_MANHEAD) { manhead }
            val womanhead = select(edit, base, DEFAULT_WOMANHEAD) { womanhead }
            val manhead2 = select(edit, base, DEFAULT_MANHEAD2) { manhead2 }
            val womanhead2 = select(edit, base, DEFAULT_WOMANHEAD2) { womanhead2 }
            val category = select(edit, base, DEFAULT_CATEGORY) { category }
            val zan2d = select(edit, base, default = 0) { zan2d }
            val certlink = select(edit, base, default = 0) { certlink }
            val certtemplate = select(edit, base, default = 0) { certtemplate }
            val countObj = selectIntArray(edit, base) { countObj }
            val countCount = selectIntArray(edit, base) { countCount }
            val resizeX = select(edit, base, DEFAULT_RESIZE_X) { resizeX }
            val resizeY = select(edit, base, DEFAULT_RESIZE_Y) { resizeY }
            val resizeZ = select(edit, base, DEFAULT_RESIZE_Z) { resizeZ }
            val ambient = select(edit, base, default = 0) { ambient }
            val contrast = select(edit, base, default = 0) { contrast }
            val team = select(edit, base, default = 0) { team }
            val boughtlink = select(edit, base, default = 0) { boughtlink }
            val boughttemplate = select(edit, base, default = 0) { boughttemplate }
            val placeholderlink = select(edit, base, default = 0) { placeholderlink }
            val placeholdertemplate = select(edit, base, default = 0) { placeholdertemplate }
            val paramMap = selectParamMap(edit, base) { paramMap }
            val objvar = selectIntArray(edit, base) { objvar }
            val generateCertificate = select(edit, base, default = false) { generateCertificate }
            val generatePlaceholder = select(edit, base, default = false) { generatePlaceholder }
            val playerCost = select(edit, base, default = 0) { playerCost }
            val playerCostDerived = select(edit, base, default = 0) { playerCostDerived }
            val playerCostDerivedConst = select(edit, base, default = 0) { playerCostDerivedConst }
            val stockMarketBuyLimit = select(edit, base, default = 0) { stockMarketBuyLimit }
            val stockMarketRecalcUsers = select(edit, base, default = 0) { stockMarketRecalcUsers }
            val tradeable = select(edit, base, DEFAULT_TRADEABLE) { tradeable }
            val respawnRate = select(edit, base, DEFAULT_RESPAWN_RATE) { respawnRate }
            val dummyitem = select(edit, base, DEFAULT_DUMMYITEM) { dummyitem }
            val contentGroup = select(edit, base, DEFAULT_CONTENT_GROUP) { contentGroup }
            val weaponCategory = select(edit, base, default = 0) { weaponCategory }
            val transformlink = select(edit, base, default = 0) { transformlink }
            val transformtemplate = select(edit, base, default = 0) { transformtemplate }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedObjType(
                name = name,
                desc = desc,
                model = model,
                zoom2d = zoom2d,
                xan2d = xan2d,
                yan2d = yan2d,
                xof2d = xof2d,
                yof2d = yof2d,
                stackable = stackable,
                cost = cost,
                wearpos1 = wearpos1,
                wearpos2 = wearpos2,
                members = members,
                manwear = manwear,
                manwearOff = manwearOff,
                manwear2 = manwear2,
                womanwear = womanwear,
                womanwearOff = womanwearOff,
                womanwear2 = womanwear2,
                wearpos3 = wearpos3,
                op = op,
                iop = iop,
                recolS = recolS,
                recolD = recolD,
                retexS = retexS,
                retexD = retexD,
                shiftclickiop = shiftclickiop,
                isubop1 = isubop1,
                isubop2 = isubop2,
                isubop3 = isubop3,
                isubop4 = isubop4,
                isubop5 = isubop5,
                stockmarket = stockmarket,
                weight = weight,
                manwear3 = manwear3,
                womanwear3 = womanwear3,
                manhead = manhead,
                womanhead = womanhead,
                manhead2 = manhead2,
                womanhead2 = womanhead2,
                category = category,
                zan2d = zan2d,
                certlink = certlink,
                certtemplate = certtemplate,
                countObj = countObj,
                countCount = countCount,
                resizeX = resizeX,
                resizeY = resizeY,
                resizeZ = resizeZ,
                ambient = ambient,
                contrast = contrast,
                team = team,
                boughtlink = boughtlink,
                boughttemplate = boughttemplate,
                placeholderlink = placeholderlink,
                placeholdertemplate = placeholdertemplate,
                paramMap = paramMap,
                generateCertificate = generateCertificate,
                generatePlaceholder = generatePlaceholder,
                objvar = objvar,
                playerCost = playerCost,
                playerCostDerived = playerCostDerived,
                playerCostDerivedConst = playerCostDerivedConst,
                stockMarketBuyLimit = stockMarketBuyLimit,
                stockMarketRecalcUsers = stockMarketRecalcUsers,
                tradeable = tradeable,
                respawnRate = respawnRate,
                dummyitem = dummyitem,
                contentGroup = contentGroup,
                weaponCategory = weaponCategory,
                transformlink = transformlink,
                transformtemplate = transformtemplate,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
