package org.rsmod.api.type.script.dsl

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.hunt.HuntCheckNotTooStrong
import org.rsmod.game.type.hunt.HuntCondition
import org.rsmod.game.type.hunt.HuntModeTypeBuilder
import org.rsmod.game.type.hunt.HuntNobodyNear
import org.rsmod.game.type.hunt.HuntType
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.hunt.UnpackedHuntModeType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.varn.VarnType
import org.rsmod.game.type.varp.VarpType

@DslMarker private annotation class HuntModeBuilderDsl

@HuntModeBuilderDsl
public class HuntModePluginBuilder(public var internal: String? = null) {
    private val backing: HuntModeTypeBuilder = HuntModeTypeBuilder()

    public var type: HuntType? by backing::type
    public var checkVis: HuntVis? by backing::checkVis
    public var checkNotTooStrong: HuntCheckNotTooStrong? by backing::checkNotTooStrong
    public var checkAfk: Boolean? by backing::checkAfk
    public var checkNotBusy: Boolean? by backing::checkNotBusy
    public var findKeepHunting: Boolean? by backing::findKeepHunting
    public var findNewMode: NpcMode? by backing::findNewMode
    public var nobodyNear: HuntNobodyNear? by backing::nobodyNear
    public var rate: Int? by backing::rate

    public var checkNotCombat: VarpType? = null
        set(value) {
            field = value
            backing.checkNotCombat = value?.id
        }

    public var checkNotCombatSelf: VarnType? = null
        set(value) {
            field = value
            backing.checkNotCombatSelf = value?.id
        }

    private var checkInvObj: HuntCondition.InvCondition? by backing::checkInvObj
    private var checkInvParam: HuntCondition.InvCondition? by backing::checkInvParam
    private var checkLoc: HuntCondition.LocCondition? by backing::checkLoc
    private var checkNpc: HuntCondition.NpcCondition? by backing::checkNpc
    private var checkObj: HuntCondition.ObjCondition? by backing::checkObj
    private var checkVar1: HuntCondition.VarCondition? by backing::checkVar1
    private var checkVar2: HuntCondition.VarCondition? by backing::checkVar2
    private var checkVar3: HuntCondition.VarCondition? by backing::checkVar3

    public fun checkInvObj(init: ConditionInvObj.() -> Unit) {
        val builder = ConditionInvObj().apply(init)
        checkInvObj = builder.build()
    }

    public fun checkInvParam(init: ConditionInvParam.() -> Unit) {
        val builder = ConditionInvParam().apply(init)
        checkInvParam = builder.build()
    }

    public fun checkLoc(init: ConditionLoc.() -> Unit) {
        val builder = ConditionLoc().apply(init)
        checkLoc = builder.build()
    }

    public fun checkNpc(init: ConditionNpc.() -> Unit) {
        val builder = ConditionNpc().apply(init)
        checkNpc = builder.build()
    }

    public fun checkObj(init: ConditionObj.() -> Unit) {
        val builder = ConditionObj().apply(init)
        checkObj = builder.build()
    }

    public fun checkVar1(init: ConditionVar.() -> Unit) {
        val builder = ConditionVar().apply(init)
        checkVar1 = builder.build()
    }

    public fun checkVar2(init: ConditionVar.() -> Unit) {
        val builder = ConditionVar().apply(init)
        checkVar2 = builder.build()
    }

    public fun checkVar3(init: ConditionVar.() -> Unit) {
        val builder = ConditionVar().apply(init)
        checkVar3 = builder.build()
    }

    public fun build(id: Int): UnpackedHuntModeType {
        evaluateRate()
        backing.internal = internal
        return backing.build(id)
    }

    private fun evaluateRate() {
        val min = getMinRate()
        val rate = this.rate
        if (rate == null) {
            this.rate = min
            return
        }
        check(rate >= min) { "`rate` cannot be lower than `$min` for hunt type: $type" }
    }

    private fun getMinRate(): Int = if (type == HuntType.Player) 1 else 3

    @HuntModeBuilderDsl
    public class ConditionInvObj {
        public lateinit var inv: InvType
        public lateinit var obj: ObjType
        public lateinit var operator: HuntCondition.Operator
        public var required: Int? = null

        public val greaterThan: HuntCondition.Operator
            get() = HuntCondition.Operator.GreaterThan

        public val lessThan: HuntCondition.Operator
            get() = HuntCondition.Operator.LessThan

        public val equals: HuntCondition.Operator
            get() = HuntCondition.Operator.Equals

        public val notEquals: HuntCondition.Operator
            get() = HuntCondition.Operator.NotEquals

        internal fun build(): HuntCondition.InvCondition {
            check(::inv.isInitialized) { "`inv` must be set." }
            check(::obj.isInitialized) { "`obj` must be set." }
            check(::operator.isInitialized) { "`operator` must be set." }
            val required = checkNotNull(required) { "`required` must be set." }
            return HuntCondition.InvCondition(inv.id, obj.id, operator, required)
        }
    }

    @HuntModeBuilderDsl
    public class ConditionInvParam {
        public lateinit var inv: InvType
        public lateinit var param: ParamType<Int>
        public lateinit var operator: HuntCondition.Operator
        public var required: Int? = null

        public val greaterThan: HuntCondition.Operator
            get() = HuntCondition.Operator.GreaterThan

        public val lessThan: HuntCondition.Operator
            get() = HuntCondition.Operator.LessThan

        public val equals: HuntCondition.Operator
            get() = HuntCondition.Operator.Equals

        public val notEquals: HuntCondition.Operator
            get() = HuntCondition.Operator.NotEquals

        internal fun build(): HuntCondition.InvCondition {
            check(::inv.isInitialized) { "`inv` must be set." }
            check(::param.isInitialized) { "`param` must be set." }
            check(::operator.isInitialized) { "`operator` must be set." }
            val required = checkNotNull(required) { "`required` must be set." }
            return HuntCondition.InvCondition(inv.id, param.id, operator, required)
        }
    }

    @HuntModeBuilderDsl
    public class ConditionLoc {
        public var loc: LocType? = null
        public var category: CategoryType? = null

        internal fun build(): HuntCondition.LocCondition {
            if (loc == null && category == null) {
                throw IllegalStateException("`loc` or `category` must be set.")
            }
            return HuntCondition.LocCondition(loc?.id, category?.id)
        }
    }

    @HuntModeBuilderDsl
    public class ConditionNpc {
        public var npc: NpcType? = null
        public var category: CategoryType? = null

        internal fun build(): HuntCondition.NpcCondition {
            if (npc == null && category == null) {
                throw IllegalStateException("`npc` or `category` must be set.")
            }
            return HuntCondition.NpcCondition(npc?.id, category?.id)
        }
    }

    @HuntModeBuilderDsl
    public class ConditionObj {
        public var obj: ObjType? = null
        public var category: CategoryType? = null

        internal fun build(): HuntCondition.ObjCondition {
            if (obj == null && category == null) {
                throw IllegalStateException("`obj` or `category` must be set.")
            }
            return HuntCondition.ObjCondition(obj?.id, category?.id)
        }
    }

    @HuntModeBuilderDsl
    public class ConditionVar {
        public lateinit var varp: VarpType
        public lateinit var operator: HuntCondition.Operator
        public var required: Int? = null

        public val greaterThan: HuntCondition.Operator
            get() = HuntCondition.Operator.GreaterThan

        public val lessThan: HuntCondition.Operator
            get() = HuntCondition.Operator.LessThan

        public val equals: HuntCondition.Operator
            get() = HuntCondition.Operator.Equals

        public val notEquals: HuntCondition.Operator
            get() = HuntCondition.Operator.NotEquals

        internal fun build(): HuntCondition.VarCondition {
            check(::varp.isInitialized) { "`varp` must be set." }
            check(::operator.isInitialized) { "`operator` must be set." }
            val required = checkNotNull(required) { "`required` must be set." }
            return HuntCondition.VarCondition(varp.id, operator, required)
        }
    }
}
