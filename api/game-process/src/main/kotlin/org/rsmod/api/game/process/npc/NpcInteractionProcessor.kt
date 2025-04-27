package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.npc.access.StandardNpcAccessLauncher
import org.rsmod.api.npc.clearInteractionRoute
import org.rsmod.api.npc.interact.AiLocInteractions
import org.rsmod.api.npc.interact.AiLocTInteractions
import org.rsmod.api.npc.interact.AiNpcInteractions
import org.rsmod.api.npc.interact.AiNpcTInteractions
import org.rsmod.api.npc.interact.AiObjInteractions
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.player.isValidTarget
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastValidator
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.interact.Interaction
import org.rsmod.game.interact.InteractionLoc
import org.rsmod.game.interact.InteractionLocOp
import org.rsmod.game.interact.InteractionLocT
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.interact.InteractionNpcOp
import org.rsmod.game.interact.InteractionNpcT
import org.rsmod.game.interact.InteractionObj
import org.rsmod.game.interact.InteractionPlayer
import org.rsmod.game.interact.InteractionPlayerOp
import org.rsmod.interact.InteractionStep
import org.rsmod.interact.InteractionTarget
import org.rsmod.interact.Interactions
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.flag.CollisionFlag

public class NpcInteractionProcessor
@Inject
constructor(
    private val eventBus: EventBus,
    private val locRegistry: LocRegistry,
    private val objRegistry: ObjRegistry,
    private val boundValidator: BoundValidator,
    private val rayCastValidator: RayCastValidator,
    private val locInteractions: AiLocInteractions,
    private val locTInteractions: AiLocTInteractions,
    private val npcInteractions: AiNpcInteractions,
    private val npcTInteractions: AiNpcTInteractions,
    private val objInteractions: AiObjInteractions,
    private val playerInteractions: AiPlayerInteractions,
    private val accessLauncher: StandardNpcAccessLauncher,
    private val movement: NpcMovementProcessor,
) {
    public fun process(npc: Npc) {
        // Store the current interaction at this stage to ensure that if an interaction triggers
        // a new one, the original interaction completes before the new one is processed.
        val interaction = npc.interaction
        var interacted = false

        if (interaction != null && !npc.isBusy) {
            val cancel = npc.shouldCancelInteraction(interaction)
            if (cancel) {
                npc.clearInteractionRoute()
                npc.defaultMode()
                return
            }
            npc.preMovementInteraction(interaction)
            interacted = interaction.interacted
        }

        if (!interacted) {
            // Note: Rerouting to target is handled via the npc's ai mode as it gets re-applied
            // implicitly every cycle.
            movement.process(npc)

            if (npc.shouldCancelChase()) {
                npc.defaultMode()
                return
            }

            if (interaction != null && !npc.isBusy) {
                npc.postMovementInteraction(interaction)
            }
        }
    }

    private fun Npc.preMovementInteraction(interaction: Interaction): Unit =
        with(interaction) {
            interacted = false
            apRangeCalled = false

            val step = determinePreMovementStep(this)
            processInteractionStep(interaction, step)
        }

    private fun Npc.postMovementInteraction(interaction: Interaction): Unit =
        with(interaction) {
            val step = determinePostMovementStep(this)
            processInteractionStep(this, step)
        }

    private fun Npc.processInteractionStep(interaction: Interaction, step: InteractionStep): Any =
        with(interaction) {
            when (step) {
                InteractionStep.TriggerScriptOp -> {
                    triggerOp(interaction)
                    interacted = true
                }
                InteractionStep.TriggerScriptAp -> {
                    abortRoute()
                    triggerAp(interaction)
                    interacted = true
                }
                InteractionStep.TriggerEngineAp -> {
                    /* no-op */
                }
                InteractionStep.TriggerEngineOp -> {
                    defaultMode()
                    interacted = true
                }
                InteractionStep.Continue -> {
                    /* no-op */
                }
            }
        }

    private fun Npc.determinePreMovementStep(interaction: Interaction): InteractionStep =
        when (interaction) {
            is InteractionLoc -> preMovementStep(interaction)
            is InteractionNpc -> preMovementStep(interaction)
            is InteractionObj -> preMovementStep(interaction)
            is InteractionPlayer -> preMovementStep(interaction)
        }

    private fun Npc.determinePostMovementStep(interaction: Interaction): InteractionStep =
        when (interaction) {
            is InteractionLoc -> postMovementStep(interaction)
            is InteractionNpc -> postMovementStep(interaction)
            is InteractionObj -> postMovementStep(interaction)
            is InteractionPlayer -> postMovementStep(interaction)
        }

    private fun Npc.triggerOp(interaction: Interaction): Unit =
        when (interaction) {
            is InteractionLocOp -> triggerOp(this, interaction)
            is InteractionLocT -> triggerOp(this, interaction)
            is InteractionNpcOp -> triggerOp(this, interaction)
            is InteractionNpcT -> triggerOp(this, interaction)
            is InteractionObj -> triggerOp(this, interaction)
            is InteractionPlayerOp -> triggerOp(this, interaction)
        }

    private fun Npc.triggerAp(interaction: Interaction): Unit =
        when (interaction) {
            is InteractionLocOp -> triggerAp(this, interaction)
            is InteractionLocT -> triggerAp(this, interaction)
            is InteractionNpcOp -> triggerAp(this, interaction)
            is InteractionNpcT -> triggerAp(this, interaction)
            is InteractionObj -> triggerAp(this, interaction)
            is InteractionPlayerOp -> triggerAp(this, interaction)
        }

    /* Loc interactions */
    private fun Npc.preMovementStep(interaction: InteractionLoc): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.postMovementStep(interaction: InteractionLoc): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.isWithinOpRange(interaction: InteractionLoc): Boolean =
        boundValidator.collides(source = avatar, target = interaction.target) ||
            boundValidator.touches(source = avatar, target = interaction.target)

    private fun Npc.isWithinApRange(interaction: InteractionLoc): Boolean =
        isValidApRange(
            target = interaction.target.coords,
            width = interaction.target.adjustedWidth,
            length = interaction.target.adjustedLength,
            distance = interaction.apRange,
        )

    /* Npc interactions */
    private fun Npc.preMovementStep(interaction: InteractionNpc): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.postMovementStep(interaction: InteractionNpc): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.isWithinOpRange(interaction: InteractionNpc): Boolean =
        boundValidator.touches(source = avatar, target = interaction.target.avatar)

    private fun Npc.isWithinApRange(interaction: InteractionNpc): Boolean {
        val isUnderTarget = boundValidator.collides(avatar, interaction.target.avatar)
        if (isUnderTarget) {
            return false
        }
        val isWithinApRange =
            isValidApRange(
                target = interaction.target.coords,
                width = interaction.target.size,
                length = interaction.target.size,
                distance = interaction.apRange,
            )
        return isWithinApRange
    }

    /* Obj interactions */
    private fun Npc.preMovementStep(interaction: InteractionObj): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.postMovementStep(interaction: InteractionObj): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.isWithinOpRange(interaction: InteractionObj): Boolean =
        boundValidator.touches(source = avatar, target = interaction.target)

    private fun Npc.isWithinApRange(interaction: InteractionObj): Boolean =
        isValidApRange(
            target = interaction.target.coords,
            width = 1,
            length = 1,
            distance = interaction.apRange,
        )

    /* Player interactions */
    private fun Npc.preMovementStep(interaction: InteractionPlayer): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.postMovementStep(interaction: InteractionPlayer): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Npc.isWithinOpRange(interaction: InteractionPlayer): Boolean =
        boundValidator.touches(source = avatar, target = interaction.target.avatar)

    private fun Npc.isWithinApRange(interaction: InteractionPlayer): Boolean {
        val isUnderTarget = boundValidator.collides(avatar, interaction.target.avatar)
        if (isUnderTarget) {
            return false
        }
        val isWithinApRange =
            isValidApRange(
                target = interaction.target.coords,
                width = interaction.target.size,
                length = interaction.target.size,
                distance = interaction.apRange,
            )
        return isWithinApRange
    }

    /* Utility functions */
    private fun Npc.isValidApRange(
        target: CoordGrid,
        width: Int,
        length: Int,
        distance: Int,
    ): Boolean {
        val withinDistance = isWithinDistance(target, distance, width, length)
        if (!withinDistance) {
            return false
        }
        // Note: We intentionally use the same line-of-sight validation that player -> npc
        // interactions use. This avoids possible one-way safe-spots for npcs targeting players.
        val hasLos =
            rayCastValidator.hasLineOfSight(
                source = target,
                destination = coords,
                destWidth = width,
                destLength = length,
                extraFlag = CollisionFlag.BLOCK_PLAYERS,
            )
        return hasLos
    }

    private fun Npc.shouldCancelInteraction(interaction: Interaction): Boolean =
        when (interaction) {
            is InteractionLoc -> !interaction.isValid(this)
            is InteractionNpc -> !interaction.isValid(this)
            is InteractionObj -> !interaction.isValid(this)
            is InteractionPlayer -> !interaction.isValid(this)
        }

    private fun InteractionLoc.isValid(npc: Npc): Boolean {
        if (!npc.isWithinMaxOpRange(target.coords)) {
            return false
        }
        return locRegistry.isValid(target.coords, target.id)
    }

    private fun InteractionNpc.isValid(npc: Npc): Boolean {
        if (!npc.isWithinMaxOpRange(target)) {
            return false
        }
        return target.isValidTarget() && uid == target.uid
    }

    private fun InteractionObj.isValid(npc: Npc): Boolean {
        if (!npc.isWithinMaxOpRange(target.coords)) {
            return false
        }
        return objRegistry.isPublicAndValid(target)
    }

    private fun InteractionPlayer.isValid(npc: Npc): Boolean {
        if (!npc.isWithinMaxOpRange(target)) {
            return false
        }
        return target.isValidTarget() && uid == target.uid
    }

    private fun Npc.isWithinMaxOpRange(target: CoordGrid): Boolean {
        if (level != target.level) {
            return false
        }
        return spawnCoords.chebyshevDistance(target) <= type.maxRange
    }

    private fun Npc.isWithinMaxOpRange(target: PathingEntity): Boolean {
        if (level != target.level) {
            return false
        }
        return spawnCoords.chebyshevDistance(target.coords) <= type.maxRange + type.attackRange
    }

    private fun Npc.shouldCancelChase(): Boolean {
        return hasMovedThisCycle && !visType.giveChase
    }

    /* Interaction event launch functions */
    public fun triggerOp(npc: Npc, interaction: InteractionLocOp) {
        val op = locInteractions.opTrigger(interaction.target, interaction.op)
        if (op != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(npc: Npc, interaction: InteractionLocOp) {
        val ap = locInteractions.apTrigger(interaction.target, interaction.op)
        if (ap != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(npc: Npc, interaction: InteractionLocT) {
        val loc = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val op = locTInteractions.opTrigger(loc, objType, component, comsub)
        if (op != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(npc: Npc, interaction: InteractionLocT) {
        val loc = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val ap = locTInteractions.apTrigger(loc, objType, component, comsub)
        if (ap != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(npc: Npc, interaction: InteractionNpcOp) {
        val op = npcInteractions.opTrigger(interaction.target, interaction.op)
        if (op != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(npc: Npc, interaction: InteractionNpcOp) {
        val ap = npcInteractions.apTrigger(interaction.target, interaction.op)
        if (ap != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(npc: Npc, interaction: InteractionNpcT) {
        val target = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val op = npcTInteractions.opTrigger(target, component, comsub, objType)
        if (op != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(npc: Npc, interaction: InteractionNpcT) {
        val target = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val ap = npcTInteractions.apTrigger(target, component, comsub, objType)
        if (ap != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, ap) }
        }
    }

    private fun triggerOp(npc: Npc, interaction: InteractionObj) {
        val op = objInteractions.opTrigger(interaction.target, interaction.op)
        if (op != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(npc: Npc, interaction: InteractionObj) {
        val ap = objInteractions.apTrigger(interaction.target, interaction.op)
        if (ap != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(npc: Npc, interaction: InteractionPlayerOp) {
        val op = playerInteractions.opTrigger(npc, interaction.target, interaction.op)
        if (op != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(npc: Npc, interaction: InteractionPlayerOp) {
        val ap = playerInteractions.apTrigger(npc, interaction.target, interaction.op)
        if (ap != null) {
            accessLauncher.launch(npc) { eventBus.publish(this, ap) }
        }
    }
}
