package org.rsmod.api.obj.charges

import jakarta.inject.Inject
import kotlin.contracts.contract
import kotlin.math.min
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType
import org.rsmod.utils.bits.bitMask
import org.rsmod.utils.bits.getBits
import org.rsmod.utils.bits.withBits

public class ObjChargeManager @Inject constructor(private val objTypes: ObjTypeList) {
    public fun getCharges(obj: InvObj?, varobj: UnpackedVarObjBitType): Int {
        return obj?.vars?.getBits(varobj.bits) ?: 0
    }

    /**
     * Adds charges to the obj in the given [slot] of the provided [inventory].
     *
     * If the obj has **no existing charges**, it is assumed to be the uncharged variant and will be
     * replaced with its charged counterpart if at least one charge is added. This requires the obj
     * type to define a `charged_variant` param; otherwise, an [IllegalStateException] is thrown.
     *
     * @param add The number of charges to attempt to add. This can exceed [max], but the final
     *   charge count will never go above [max].
     * @param max The maximum number of charges the obj can hold (e.g., `20_000` for Tumeken's
     *   Shadow). This must not exceed the capacity allowed by [varobj], or an
     *   [IllegalArgumentException] is thrown.
     * @return [Charge.Success] if at least one charge was added; otherwise, a [Charge.Failure]
     *   indicating the reason (e.g., obj already has [max] charges).
     * @throws IllegalStateException if the obj has no charges and does not define a
     *   `charged_variant` param (required to convert it into its charged form).
     * @throws IllegalArgumentException if [max] exceeds the representable value for [varobj].
     */
    public fun addCharges(
        inventory: Inventory,
        slot: Int,
        add: Int,
        varobj: UnpackedVarObjBitType,
        max: Int,
    ): Charge {
        val chargeRange = 0..varobj.bits.bitMask
        require(max in chargeRange) {
            "`max` charges ($max) must be within range [0..${varobj.bits.bitMask}]. (var=$varobj)"
        }
        val obj = inventory[slot] ?: return Charge.Failure.ObjNotFound
        val curr = getCharges(obj, varobj)
        val total = min(max, curr + add)
        if (curr == total) {
            return Charge.Failure.AlreadyFullCharges
        }
        val updatedVar = obj.vars.withBits(varobj.bits, total)
        val added = total - curr

        if (curr == 0) {
            // If the obj currently has no charges, we assume it is a _valid_ uncharged version
            // (i.e., it has the `charged_variant` param defined). If not, we throw an exception.
            // While someone with spawn permissions could technically have a "charged" obj with
            // 0 charges, we enforce strict correctness here: they should spawn the uncharged
            // variant and charge it properly. This helps avoid unintended oversights.
            val charged = objTypes[obj].paramOrNull(params.charged_variant)
            if (charged == null) {
                val message = "Obj missing `charged_variant` param: $obj (type=${objTypes[obj]})"
                throw IllegalStateException(message)
            }
            inventory[slot] = InvObj(charged, vars = updatedVar)
            return Charge.Success.AddChangeObj(added = added, total = total, charged = charged)
        }

        inventory[slot] = obj.copy(vars = updatedVar)
        return Charge.Success.AddSameObj(added = added, total = total)
    }

    /**
     * Reduces charges from the player's worn obj in the [wearpos] slot.
     *
     * If the obj's **current charges** are `0`, it will immediately be replaced with its uncharged
     * variant, returning [Uncharge.Failure.NotEnoughCharges]. If the remaining charge count **after
     * reduction** reaches `0`, the obj will also be replaced with its uncharged variant, but will
     * return [Uncharge.Success] instead.
     *
     * @return [Uncharge.Success] if exactly [decrement] charges were reduced; otherwise, a
     *   [Uncharge.Failure] indicating the reason for failure.
     * @throws IllegalStateException if the worn obj does not have a defined `uncharged_variant`
     *   param.
     */
    public fun reduceWornCharges(
        player: Player,
        wearpos: Wearpos,
        varobj: UnpackedVarObjBitType,
        decrement: Int,
    ): Uncharge {
        val obj = player.worn[wearpos.slot] ?: return Uncharge.Failure.ObjNotFound
        val type = objTypes[obj]

        // Always ensure that any obj used as a "charge" weapon has an uncharged variant defined.
        val uncharged = type.paramOrNull(params.uncharged_variant)
        if (uncharged == null) {
            val message = "Obj missing `uncharged_variant` param: $obj (type=$type)"
            throw IllegalStateException(message)
        }

        val currVarValue = obj.vars.getBits(varobj.bits)
        if (currVarValue < decrement) {
            if (currVarValue == 0) {
                player.worn[wearpos.slot] = InvObj(uncharged)
            }
            return Uncharge.Failure.NotEnoughCharges
        }

        val decrementedVarValue = currVarValue.withBits(varobj.bits, currVarValue - decrement)
        if (decrementedVarValue == 0) {
            player.worn[wearpos.slot] = InvObj(uncharged)
        } else {
            player.worn[wearpos.slot] = obj.copy(vars = decrementedVarValue)
        }

        return Uncharge.Success(decrementedVarValue)
    }

    /**
     * Removes all charges from the obj in the given [slot] of the provided [inventory].
     *
     * The obj will always be replaced with its uncharged variant, regardless of the current charge
     * value. This requires the obj type to define an `uncharged_variant` param; otherwise, an
     * [IllegalStateException] is thrown.
     *
     * This function assumes that a valid obj exists in the given [slot].
     *
     * _Note: Only the [varobj] var is reset to `0`. All other varobj values on the obj will
     * persist._
     *
     * @return The number of charges the obj had before being reset. This may be `0` if the obj was
     *   already uncharged.
     * @throws IllegalStateException if the obj does not define an `uncharged_variant` param.
     * @throws NoSuchElementException if no obj exists in the given [inventory] slot.
     */
    public fun removeAllCharges(
        inventory: Inventory,
        slot: Int,
        varobj: UnpackedVarObjBitType,
    ): Int {
        val obj = inventory.getValue(slot) // Should not call this without a valid obj in `slot`.
        val type = objTypes[obj]

        val uncharged = type.paramOrNull(params.uncharged_variant)
        if (uncharged == null) {
            val message = "Obj missing `uncharged_variant` param: $obj (type=$type)"
            throw IllegalStateException(message)
        }

        val previousCharges = obj.vars.getBits(varobj.bits)

        val resetVarValue = obj.vars.withBits(varobj.bits, 0)
        inventory[slot] = InvObj(uncharged, vars = resetVarValue)

        return previousCharges
    }

    public sealed class Charge {
        public sealed class Success : Charge() {
            public abstract val added: Int
            public abstract val total: Int

            /** Indicates that charges were successfully added to an already charged obj. */
            public data class AddSameObj(override val added: Int, override val total: Int) :
                Success()

            /**
             * Indicates that charges were added to an "uncharged" variant of an obj, resulting in
             * the inventory obj being changed to the [charged] variant.
             */
            public data class AddChangeObj(
                override val added: Int,
                override val total: Int,
                public val charged: ObjType,
            ) : Success()
        }

        public sealed class Failure : Charge() {
            public data object ObjNotFound : Failure()

            public data object AlreadyFullCharges : Failure()
        }
    }

    public sealed class Uncharge {
        public data class Success(val chargesLeft: Int) : Uncharge() {
            public val fullyUncharged: Boolean
                get() = chargesLeft == 0
        }

        public sealed class Failure : Uncharge() {
            public data object ObjNotFound : Failure()

            public data object NotEnoughCharges : Failure()
        }
    }

    public companion object {
        // Important: These contracts assume the only valid `Charge` results are `Success` or
        // `Failure`. Do not introduce new base result types. Subclasses of `Success` and `Failure`
        // are fine.
        public fun Charge.isFailure(): Boolean {
            contract {
                returns(true) implies (this@isFailure is Charge.Failure)
                returns(false) implies (this@isFailure is Charge.Success)
            }
            return this is Charge.Failure
        }

        // Important: These contracts assume the only valid `Uncharge` results are `Success` or
        // `Failure`. Do not introduce new base result types. Subclasses of `Success` and `Failure`
        // are fine.
        public fun Uncharge.isFailure(): Boolean {
            contract {
                returns(true) implies (this@isFailure is Uncharge.Failure)
                returns(false) implies (this@isFailure is Uncharge.Success)
            }
            return this is Uncharge.Failure
        }
    }
}
