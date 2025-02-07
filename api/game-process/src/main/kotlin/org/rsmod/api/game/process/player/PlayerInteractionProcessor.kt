package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.config.Constants
import org.rsmod.api.player.clearInteractionRoute
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.NpcTInteractions
import org.rsmod.api.player.interact.ObjInteractions
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastValidator
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.Interaction
import org.rsmod.game.interact.InteractionLoc
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.interact.InteractionNpcOp
import org.rsmod.game.interact.InteractionNpcT
import org.rsmod.game.interact.InteractionObj
import org.rsmod.interact.InteractionStep
import org.rsmod.interact.InteractionTarget
import org.rsmod.interact.Interactions
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.flag.CollisionFlag

public class PlayerInteractionProcessor
@Inject
constructor(
    private val eventBus: EventBus,
    private val locRegistry: LocRegistry,
    private val objRegistry: ObjRegistry,
    private val boundValidator: BoundValidator,
    private val rayCastValidator: RayCastValidator,
    private val locInteractions: LocInteractions,
    private val npcInteractions: NpcInteractions,
    private val npcTInteractions: NpcTInteractions,
    private val objInteractions: ObjInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) {
    public fun processPreMovement(player: Player, interaction: Interaction) {
        if (player.isBusy) {
            return
        }
        if (player.shouldCancelInteraction(interaction)) {
            player.clearInteractionRoute()
            return
        }
        player.preMovementInteraction(interaction)
    }

    public fun isMovementCancellationRequired(interaction: Interaction): Boolean =
        interaction.interacted && !interaction.apRangeCalled

    public fun processPostMovement(player: Player, interaction: Interaction) {
        if (player.isBusy) {
            player.clearFinishedInteraction()
            return
        }
        if (player.shouldCancelInteraction(interaction)) {
            player.clearInteractionRoute()
            return
        }
        player.postMovementInteraction(interaction)
        player.clearFinishedInteraction()
    }

    private fun Player.preMovementInteraction(interaction: Interaction): Unit =
        with(interaction) {
            interacted = false
            apRangeCalled = false
            val step = determinePreMovementStep(this)
            processPreMovementStep(interaction, step)
        }

    private fun Player.postMovementInteraction(interaction: Interaction): Unit =
        with(interaction) {
            if (!interacted || apRangeCalled) {
                // `apRangeCalled` being reset here is _not_ part of the original interaction model.
                // It is _a_ solution to the scenario where `TriggerEngineAp` pre-movement step is
                // followed-up by a `TriggerEngineOp` post-movement step. In said situation,
                // `apRangeCalled` would be true; `TriggerEngineOp` would set `interacted` to true;
                // `dm_default` engine message would be sent. However, since the `apRangeCalled`
                // flag is true, the interaction would not be reset that cycle. It would instead be
                // reset the following cycle, but not before sending an extra `dm_default` message
                // due to the next steps: `Continue` and `TriggerEngineOp`.
                // Ex:
                // - Player is next to a door without a defined script op/ap
                // - Player operates door
                // - Pre-movement step = TriggerEngineAp > `apRange = -1; apRangeCalled = true`
                // - Post-movement step = TriggerEngineOp > `mes(dm_default); interacted = true`
                // - Condition to clear interaction = `interacted && !apRangeCalled`
                // - Condition fails due to `apRangeCalled = true`
                interacted = false
                apRangeCalled = false
                val step = determinePostMovementStep(this)
                processPostMovementStep(this, step)
            }
            if (!interaction.interacted && routeDestination.isEmpty() && !hasMovedThisCycle) {
                mes(Constants.dm_reach, ChatType.Engine)
                clearInteractionRoute()
            }
        }

    private fun Player.processPreMovementStep(
        interaction: Interaction,
        step: InteractionStep,
    ): Any =
        with(interaction) {
            when (step) {
                InteractionStep.TriggerScriptOp -> {
                    triggerOp(interaction)
                    interacted = true
                }
                InteractionStep.TriggerScriptAp -> {
                    triggerAp(interaction)
                    interacted = true
                }
                InteractionStep.TriggerEngineAp -> {
                    apRange = -1
                    apRangeCalled = true
                }
                InteractionStep.TriggerEngineOp -> {
                    mes(Constants.dm_default, ChatType.Engine)
                    interacted = true
                }
                InteractionStep.Continue -> {
                    /* no-op */
                }
            }
        }

    private fun Player.processPostMovementStep(
        interaction: Interaction,
        step: InteractionStep,
    ): Any =
        with(interaction) {
            when (step) {
                InteractionStep.TriggerScriptOp -> {
                    triggerOp(interaction)
                    interacted = true
                }
                InteractionStep.TriggerScriptAp -> {
                    triggerAp(interaction)
                    interacted = true
                }
                InteractionStep.TriggerEngineAp -> {
                    apRange = -1
                    apRangeCalled = true
                    interacted = true
                }
                InteractionStep.TriggerEngineOp -> {
                    mes(Constants.dm_default, ChatType.Engine)
                    interacted = true
                }
                InteractionStep.Continue -> {
                    /* no-op */
                }
            }
        }

    private fun Player.clearFinishedInteraction() {
        val interaction = interaction ?: return
        if (interaction.interacted && !interaction.apRangeCalled && !interaction.persistent) {
            clearInteractionRoute()
        }
    }

    private fun Player.determinePreMovementStep(interaction: Interaction): InteractionStep =
        when (interaction) {
            is InteractionLoc -> preMovementStep(interaction)
            is InteractionNpc -> preMovementStep(interaction)
            is InteractionObj -> preMovementStep(interaction)
        }

    private fun Player.determinePostMovementStep(interaction: Interaction): InteractionStep =
        when (interaction) {
            is InteractionLoc -> postMovementStep(interaction)
            is InteractionNpc -> postMovementStep(interaction)
            is InteractionObj -> postMovementStep(interaction)
        }

    private fun Player.triggerOp(interaction: Interaction): Unit =
        when (interaction) {
            is InteractionLoc -> triggerOp(this, interaction)
            is InteractionNpcOp -> triggerOp(this, interaction)
            is InteractionNpcT -> triggerOp(this, interaction)
            is InteractionObj -> triggerOp(this, interaction)
        }

    private fun Player.triggerAp(interaction: Interaction): Unit =
        when (interaction) {
            is InteractionLoc -> triggerAp(this, interaction)
            is InteractionNpcOp -> triggerAp(this, interaction)
            is InteractionNpcT -> triggerAp(this, interaction)
            is InteractionObj -> triggerAp(this, interaction)
        }

    /* Loc interactions */
    private fun Player.preMovementStep(interaction: InteractionLoc): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.postMovementStep(interaction: InteractionLoc): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.isWithinOpRange(interaction: InteractionLoc): Boolean =
        boundValidator.collides(source = avatar, target = interaction.target) ||
            boundValidator.touches(source = avatar, target = interaction.target)

    private fun Player.isWithinApRange(interaction: InteractionLoc): Boolean =
        isValidApRange(
            target = interaction.target.coords,
            width = interaction.target.adjustedWidth,
            length = interaction.target.adjustedLength,
            distance = interaction.apRange,
        )

    /* Npc interactions */
    private fun Player.preMovementStep(interaction: InteractionNpc): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.postMovementStep(interaction: InteractionNpc): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.isWithinOpRange(interaction: InteractionNpc): Boolean =
        boundValidator.touches(source = avatar, target = interaction.target.avatar)

    private fun Player.isWithinApRange(interaction: InteractionNpc): Boolean =
        isValidApRange(
            target = interaction.target.coords,
            width = interaction.target.size,
            length = interaction.target.size,
            distance = interaction.apRange,
        )

    /* Obj interactions */
    private fun Player.preMovementStep(interaction: InteractionObj): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.postMovementStep(interaction: InteractionObj): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Static,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.isWithinOpRange(interaction: InteractionObj): Boolean =
        boundValidator.touches(source = avatar, target = interaction.target)

    private fun Player.isWithinApRange(interaction: InteractionObj): Boolean =
        isValidApRange(
            target = interaction.target.coords,
            width = 1,
            length = 1,
            distance = interaction.apRange,
        )

    /* Utility functions */
    private fun Player.isValidApRange(
        target: CoordGrid,
        width: Int,
        length: Int,
        distance: Int,
    ): Boolean {
        val withinDistance = isWithinDistance(target, distance, width, length)
        if (!withinDistance) {
            return false
        }
        val hasLos =
            rayCastValidator.hasLineOfSight(
                source = coords,
                destination = target,
                destWidth = width,
                destLength = length,
                extraFlag = CollisionFlag.BLOCK_PLAYERS,
            )
        return hasLos
    }

    private fun Player.shouldCancelInteraction(interaction: Interaction): Boolean =
        when (interaction) {
            is InteractionLoc -> !interaction.isValid()
            is InteractionNpc -> !interaction.isValid()
            is InteractionObj -> !interaction.isValid(this)
        }

    private fun InteractionLoc.isValid(): Boolean {
        return locRegistry.isValid(target.coords, target.id)
    }

    private fun InteractionNpc.isValid(): Boolean {
        return target.isValidTarget && target.isNotDelayed && type == target.visType.id
    }

    private fun InteractionObj.isValid(observer: Player): Boolean {
        return objRegistry.isValid(observer, target)
    }

    /* Interaction event launch functions */
    public fun triggerOp(player: Player, interaction: InteractionLoc) {
        val loc = interaction.target
        val op = locInteractions.opTrigger(player, loc, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionLoc) {
        val loc = interaction.target
        val ap = locInteractions.apTrigger(player, loc, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(player: Player, interaction: InteractionNpcOp) {
        val npc = interaction.target
        val op = npcInteractions.opTrigger(player, npc, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionNpcOp) {
        val npc = interaction.target
        val ap = npcInteractions.apTrigger(player, npc, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(player: Player, interaction: InteractionNpcT) {
        val npc = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val op = npcTInteractions.opTrigger(player, npc, component, comsub, objType)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionNpcT) {
        val npc = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val ap = npcTInteractions.apTrigger(player, npc, component, comsub, objType)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    private fun triggerOp(player: Player, interaction: InteractionObj) {
        val obj = interaction.target
        val op = objInteractions.opTrigger(obj, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionObj) {
        val obj = interaction.target
        val ap = objInteractions.apTrigger(obj, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }
}
