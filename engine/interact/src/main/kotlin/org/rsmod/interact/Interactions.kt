package org.rsmod.interact

public object Interactions {
    public fun earlyStep(
        target: InteractionTarget,
        hasScriptOp: Boolean,
        validOpLine: Boolean,
        hasScriptAp: Boolean,
        validApLine: Boolean,
    ): InteractionStep =
        when {
            validOpLine && hasScriptOp && target == InteractionTarget.Pathing ->
                InteractionStep.TriggerScriptOp
            validApLine && hasScriptAp -> InteractionStep.TriggerScriptAp
            validApLine -> InteractionStep.TriggerEngineAp
            validOpLine && target == InteractionTarget.Pathing -> InteractionStep.TriggerEngineOp
            else -> InteractionStep.Continue
        }

    public fun lateStep(
        target: InteractionTarget,
        hasMoved: Boolean,
        hasScriptOp: Boolean,
        validOpLine: Boolean,
        hasScriptAp: Boolean,
        validApLine: Boolean,
    ): InteractionStep =
        when {
            validOpLine && hasScriptOp && (target == InteractionTarget.Pathing || !hasMoved) ->
                InteractionStep.TriggerScriptOp
            validApLine && hasScriptAp -> InteractionStep.TriggerScriptAp
            validApLine -> InteractionStep.TriggerEngineAp
            validOpLine && (target == InteractionTarget.Pathing || !hasMoved) ->
                InteractionStep.TriggerEngineOp
            else -> InteractionStep.Continue
        }
}
