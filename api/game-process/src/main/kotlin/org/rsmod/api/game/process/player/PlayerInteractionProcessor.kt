package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.config.Constants
import org.rsmod.api.npc.isValidTarget
import org.rsmod.api.player.clearInteractionRoute
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.interact.LocTInteractions
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.NpcTInteractions
import org.rsmod.api.player.interact.ObjInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.interact.PlayerTInteractions
import org.rsmod.api.player.isValidTarget
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.clearMapFlag
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
import org.rsmod.game.interact.InteractionLocOp
import org.rsmod.game.interact.InteractionLocT
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.interact.InteractionNpcOp
import org.rsmod.game.interact.InteractionNpcT
import org.rsmod.game.interact.InteractionObj
import org.rsmod.game.interact.InteractionPlayer
import org.rsmod.game.interact.InteractionPlayerOp
import org.rsmod.game.interact.InteractionPlayerT
import org.rsmod.game.movement.RouteRequestPathingEntity
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
    private val locTInteractions: LocTInteractions,
    private val npcInteractions: NpcInteractions,
    private val npcTInteractions: NpcTInteractions,
    private val objInteractions: ObjInteractions,
    private val playerInteractions: PlayerInteractions,
    private val playerTInteractions: PlayerTInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
    private val movement: PlayerMovementProcessor,
) {
    public fun process(player: Player) {
        // Store the current interaction at this stage to ensure that if an interaction triggers a
        // new one (e.g., combat calling `opnpc2`), the original interaction completes before the
        // new one is processed.
        val interaction = player.interaction
        var interacted = false

        if (interaction != null && !player.isAccessProtected) {
            val cancel = player.shouldCancelInteraction(interaction)
            if (cancel) {
                player.clearInteractionRoute()
                return
            }
            player.preMovementInteraction(interaction)
            interacted = interaction.interacted
        }

        if (!interacted) {
            val reroute = interaction != null && player.routeDestination.size <= 1
            if (reroute) {
                player.routeToPathingTarget(interaction)
            }

            val routeRequest = player.routeRequest
            val pendingLogout = player.isPendingLogout()
            if (routeRequest != null && !pendingLogout) {
                movement.consumeRequest(player, routeRequest)
            }

            if (pendingLogout) {
                player.abortRoute()
            }

            movement.process(player)

            val didNotMove = !player.hasMovedThisCycle && interaction != null
            if (didNotMove && player.routeDestination.isEmpty()) {
                player.clearMapFlag()
            }

            if (interaction != null && !player.isAccessProtected) {
                player.postMovementInteraction(interaction)
            }
        } else if (!player.hasMovedThisCycle && player.routeDestination.isEmpty()) {
            player.clearMapFlag()
        }

        // Important that this uses the most up-to-date `interaction` reference and not the one
        // stored locally in this function. This allows for newly set interactions to not get
        // cleared if the local reference has "completed."
        player.clearFinishedInteraction()
    }

    private fun Player.preMovementInteraction(interaction: Interaction): Unit =
        with(interaction) {
            interacted = false
            apRangeCalled = false
            val step = determinePreMovementStep(this)
            processInteractionStep(interaction, step)
        }

    private fun Player.postMovementInteraction(interaction: Interaction): Unit =
        with(interaction) {
            val step = determinePostMovementStep(this)
            processInteractionStep(this, step)

            if (!interaction.interacted && routeDestination.isEmpty() && !hasMovedThisCycle) {
                clearInteractionRoute()
                mes(Constants.dm_reach, ChatType.Engine)
            }
        }

    private fun Player.processInteractionStep(
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
                    val cachedWaypoints = routeDestination.toList()
                    abortRoute()

                    apRangeCalled = false
                    triggerAp(interaction)
                    interacted = true

                    val newInteractionSet = this@processInteractionStep.interaction != interaction
                    if (newInteractionSet) {
                        abortRoute()
                    } else if (apRangeCalled) {
                        routeDestination.addAll(cachedWaypoints)
                        interacted = false
                    }
                }
                InteractionStep.TriggerEngineAp -> {
                    apRange = -1
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
        if (interaction.isCompleted()) {
            clearInteraction()
            clearMapFlag()
        }
    }

    private fun Interaction.isCompleted(): Boolean = interacted && !apRangeCalled

    private fun Player.routeToPathingTarget(interaction: Interaction) =
        when (interaction) {
            is InteractionNpc -> routeTo(interaction)
            is InteractionPlayer -> routeTo(interaction)
            is InteractionLoc -> {
                /* no-op */
            }
            is InteractionObj -> {
                /* no-op */
            }
        }

    private fun Player.determinePreMovementStep(interaction: Interaction): InteractionStep =
        when (interaction) {
            is InteractionLoc -> preMovementStep(interaction)
            is InteractionNpc -> preMovementStep(interaction)
            is InteractionObj -> preMovementStep(interaction)
            is InteractionPlayer -> preMovementStep(interaction)
        }

    private fun Player.determinePostMovementStep(interaction: Interaction): InteractionStep =
        when (interaction) {
            is InteractionLoc -> postMovementStep(interaction)
            is InteractionNpc -> postMovementStep(interaction)
            is InteractionObj -> postMovementStep(interaction)
            is InteractionPlayer -> postMovementStep(interaction)
        }

    private fun Player.triggerOp(interaction: Interaction): Unit =
        when (interaction) {
            is InteractionLocOp -> triggerOp(this, interaction)
            is InteractionLocT -> triggerOp(this, interaction)
            is InteractionNpcOp -> triggerOp(this, interaction)
            is InteractionNpcT -> triggerOp(this, interaction)
            is InteractionObj -> triggerOp(this, interaction)
            is InteractionPlayerOp -> triggerOp(this, interaction)
            is InteractionPlayerT -> triggerOp(this, interaction)
        }

    private fun Player.triggerAp(interaction: Interaction): Unit =
        when (interaction) {
            is InteractionLocOp -> triggerAp(this, interaction)
            is InteractionLocT -> triggerAp(this, interaction)
            is InteractionNpcOp -> triggerAp(this, interaction)
            is InteractionNpcT -> triggerAp(this, interaction)
            is InteractionObj -> triggerAp(this, interaction)
            is InteractionPlayerOp -> triggerAp(this, interaction)
            is InteractionPlayerT -> triggerAp(this, interaction)
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

    private fun Player.isWithinApRange(interaction: InteractionNpc): Boolean {
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

    private fun Player.routeTo(interaction: InteractionNpc) {
        if (isWithinOpRange(interaction)) {
            return
        }
        val routeRequest = RouteRequestPathingEntity(interaction.target.avatar)
        this.routeRequest = routeRequest
    }

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

    /* Player interactions */
    private fun Player.preMovementStep(interaction: InteractionPlayer): InteractionStep =
        Interactions.earlyStep(
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.postMovementStep(interaction: InteractionPlayer): InteractionStep =
        Interactions.lateStep(
            hasMoved = hasMovedThisCycle,
            target = InteractionTarget.Pathing,
            hasScriptOp = interaction.hasOpTrigger,
            hasScriptAp = interaction.hasApTrigger,
            validOpLine = isWithinOpRange(interaction),
            validApLine = isWithinApRange(interaction),
        )

    private fun Player.isWithinOpRange(interaction: InteractionPlayer): Boolean =
        boundValidator.touches(source = avatar, target = interaction.target.avatar)

    private fun Player.isWithinApRange(interaction: InteractionPlayer): Boolean {
        val isUnderTarget = boundValidator.collides(avatar, interaction.target.avatar)
        if (isUnderTarget) {
            return false
        }
        val distance = interaction.apRange
        val isSourceWithinApRange =
            isValidApRange(
                target = interaction.target.coords,
                width = interaction.target.size,
                length = interaction.target.size,
                distance = distance,
            )
        if (!isSourceWithinApRange) {
            return false
        }
        val isTargetWithinApRange =
            interaction.target.isValidApRange(
                target = coords,
                width = size,
                length = size,
                distance = distance,
            )
        return isTargetWithinApRange
    }

    private fun Player.routeTo(interaction: InteractionPlayer) {
        if (isWithinOpRange(interaction)) {
            return
        }
        val routeRequest = RouteRequestPathingEntity(interaction.target.avatar)
        this.routeRequest = routeRequest
    }

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
            is InteractionPlayer -> !interaction.isValid()
        }

    private fun InteractionLoc.isValid(): Boolean {
        return locRegistry.isValid(target.coords, target.id)
    }

    private fun InteractionNpc.isValid(): Boolean {
        return target.isValidTarget() && uid == target.uid
    }

    private fun InteractionObj.isValid(observer: Player): Boolean {
        return objRegistry.isValid(observer, target)
    }

    private fun InteractionPlayer.isValid(): Boolean {
        return target.isValidTarget() && uid == target.uid
    }

    /* Interaction event launch functions */
    public fun triggerOp(player: Player, interaction: InteractionLocOp) {
        val op = locInteractions.opTrigger(player, interaction.target, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionLocOp) {
        val ap = locInteractions.apTrigger(player, interaction.target, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(player: Player, interaction: InteractionLocT) {
        val loc = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val op = locTInteractions.opTrigger(player, loc, objType, component, comsub)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionLocT) {
        val loc = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val ap = locTInteractions.apTrigger(player, loc, objType, component, comsub)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(player: Player, interaction: InteractionNpcOp) {
        val op = npcInteractions.opTrigger(player, interaction.target, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionNpcOp) {
        val ap = npcInteractions.apTrigger(player, interaction.target, interaction.op)
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
        val op = objInteractions.opTrigger(interaction.target, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionObj) {
        val ap = objInteractions.apTrigger(interaction.target, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(player: Player, interaction: InteractionPlayerOp) {
        val op = playerInteractions.opTrigger(interaction.target, interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionPlayerOp) {
        val ap = playerInteractions.apTrigger(interaction.target, interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    public fun triggerOp(player: Player, interaction: InteractionPlayerT) {
        val target = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val op = playerTInteractions.opTrigger(target, component, comsub, objType)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    public fun triggerAp(player: Player, interaction: InteractionPlayerT) {
        val target = interaction.target
        val comsub = interaction.comsub
        val component = interaction.component
        val objType = interaction.objType
        val ap = playerTInteractions.apTrigger(target, component, comsub, objType)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }
}
