package org.rsmod.content.generic.locs.pickables

import jakarta.inject.Inject
import org.rsmod.api.config.locParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc2
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Pickable
@Inject
constructor(private val objRepo: ObjRepository, private val locRepo: LocRepository) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc2(content.pickable_crop) { pickCrop(it.loc, it.type) }
        onOpLoc2(pickable_locs.cabbage) { pickCabbage(it.loc, it.type) }
    }

    private suspend fun ProtectedAccess.pickCrop(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        takeObj: ObjType = type.takeObjType,
        takeMessage: String = type.takeMessage,
    ) {
        arriveDelay()

        if (inv.isFull()) {
            playerWalk(loc.coords)
            mes(type.invFullMessage)
            return
        }

        anim(seqs.human_pickupfloor)
        playerWalkWithMinDelay(loc.coords)

        locRepo.del(loc, type.respawnTime)

        mes(takeMessage)
        soundSynth(synths.pick)
        invAddOrDrop(objRepo, takeObj)
    }

    private suspend fun ProtectedAccess.pickCabbage(loc: BoundLocInfo, type: UnpackedLocType) {
        // Rate for cabbage seed is currently unknown.
        if (random.randomBoolean(25)) {
            pickCrop(loc, type, objs.cabbage_seed, "You pick a cabbage seed.")
        } else {
            pickCrop(loc, type)
        }
    }
}

private val UnpackedLocType.takeMessage by locParam(params.game_message)
private val UnpackedLocType.invFullMessage by locParam(params.game_message2)
private val UnpackedLocType.takeObjType by locParam(params.rewarditem)
private val UnpackedLocType.respawnTime by locParam(params.respawn_time)
