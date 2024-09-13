package org.rsmod.api.player.output

import net.rsprot.protocol.common.game.outgoing.inv.InventoryObject
import net.rsprot.protocol.game.outgoing.camera.CamLookAt
import net.rsprot.protocol.game.outgoing.camera.CamMoveTo
import net.rsprot.protocol.game.outgoing.camera.CamReset
import net.rsprot.protocol.game.outgoing.inv.UpdateInvFull
import net.rsprot.protocol.game.outgoing.inv.UpdateInvPartial
import net.rsprot.protocol.game.outgoing.inv.UpdateInvStopTransmit
import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import net.rsprot.protocol.game.outgoing.misc.player.RunClientScript
import net.rsprot.protocol.game.outgoing.misc.player.SetMapFlag
import net.rsprot.protocol.game.outgoing.sound.SynthSound
import net.rsprot.protocol.game.outgoing.varp.VarpLarge
import net.rsprot.protocol.game.outgoing.varp.VarpSmall
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.map.CoordGrid

public fun Player.camReset() {
    client.write(CamReset)
}

public fun Player.camLookAt(dest: CoordGrid, camHeight: Int, camRate: Int, camRate2: Int) {
    // TODO: Add require assertion to make sure coords is valid within build area.
    val dx = dest.x - buildArea.x
    val dz = dest.z - buildArea.z
    client.write(CamLookAt(dx, dz, camHeight, camRate, camRate2))
}

public fun Player.camMoveTo(dest: CoordGrid, camHeight: Int, camRate: Int, camRate2: Int) {
    // TODO: Add require assertion to make sure coords is valid within build area.
    val dx = dest.x - buildArea.x
    val dz = dest.z - buildArea.z
    client.write(CamMoveTo(dx, dz, camHeight, camRate, camRate2))
}

/** @see [SynthSound] */
public fun Player.soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0) {
    client.write(SynthSound(synth.id, loops, delay))
}

// TODO: type-safe clientscript
public fun Player.runClientScript(id: Int, vararg args: Any) {
    runClientScript(id, args.toList())
}

public fun Player.runClientScript(id: Int, args: List<Any>) {
    client.write(RunClientScript(id, args))
}

/** @see [UpdateInvFull] */
public fun Player.updateInvFull(inv: Inventory) {
    val highestSlot = inv.indexOfLast { it != null } + 1
    val provider = RspObjProvider(inv.objs)
    val message = UpdateInvFull(inv.type.id, highestSlot, provider)
    client.write(message)
}

/** @see [UpdateInvPartial] */
public fun Player.updateInvPartial(inv: Inventory, vararg updateSlots: Int) {
    val provider = RspIndexedObjProvider(inv.objs, updateSlots.iterator())
    val message = UpdateInvPartial(inv.type.id, provider)
    client.write(message)
}

/** @see [UpdateInvStopTransmit] */
public fun Player.updateInvStopTransmit(inv: Inventory) {
    client.write(UpdateInvStopTransmit(inv.type.id))
}

public fun Player.setMapFlag(coords: CoordGrid) {
    setMapFlag(coords.x, coords.z)
}

public fun Player.setMapFlag(x: Int, z: Int) {
    val dx = x - buildArea.x
    val dz = z - buildArea.z
    client.write(SetMapFlag(dx, dz))
}

public fun Player.clearMapFlag() {
    client.write(SetMapFlag(255, 255))
}

/** Calls [mes] with [text] as the message and [ChatType.Spam] as the type of chat. */
public fun Player.spam(text: String): Unit = mes(text, ChatType.Spam)

/** @see [MessageGame] */
public fun Player.mes(text: String, type: ChatType = ChatType.GameMessage) {
    val message = MessageGame(type.id, text)
    client.write(message)
}

/** @see [MessageGame] */
public fun Player.requestMes(text: String, name: String, type: ChatType = ChatType.ChalReqTrade) {
    val message = MessageGame(type.id, name, text)
    client.write(message)
}

/** Calling this function directly will bypass [VarpType.transmit] and [VarpType.protect] flags. */
public fun Player.writeVarp(varp: VarpType, value: Int) {
    val message =
        if (value in Byte.MIN_VALUE..Byte.MAX_VALUE) {
            VarpSmall(varp.id, value)
        } else {
            VarpLarge(varp.id, value)
        }
    client.write(message)
}

private class RspObjProvider(private val objs: Array<InvObj?>) : UpdateInvFull.ObjectProvider {
    override fun provide(slot: Int): InventoryObject {
        val obj = objs.getOrNull(slot) ?: return InventoryObject.NULL
        return InventoryObject(slot, obj.id, obj.count)
    }
}

private class RspIndexedObjProvider(private val objs: Array<InvObj?>, updateSlots: Iterator<Int>) :
    UpdateInvPartial.IndexedObjectProvider(updateSlots) {
    override fun provide(slot: Int): InventoryObject {
        val obj = objs.getOrNull(slot) ?: return InventoryObject(slot, -1, -1)
        return InventoryObject(slot, obj.id, obj.count)
    }
}
