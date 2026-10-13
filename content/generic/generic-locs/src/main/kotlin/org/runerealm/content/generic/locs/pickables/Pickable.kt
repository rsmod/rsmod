package org.runerealm.content.generic.locs.pickables

import jakarta.inject.Inject
import org.runerealm.api.config.locParam
import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.synths
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.onOpLoc2
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class Pickable
@Inject
constructor(private val objRepo: ObjRepository, private val locRepo: LocRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {
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
        // The rate for cabbage seed is currently unknown.
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
