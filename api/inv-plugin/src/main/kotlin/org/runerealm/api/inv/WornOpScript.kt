package org.runerealm.api.inv

import jakarta.inject.Inject
import org.runerealm.api.config.refs.enums
import org.runerealm.api.player.interact.WornInteractions
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.ui.IfOverlayButton
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.obj.Wearpos
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class WornOpScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val enumResolver: EnumTypeMapResolver,
    private val interactions: WornInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startup() {
        val mappedComponents = mappedComponents()
        for ((wearpos, component) in mappedComponents) {
            onIfOverlayButton(component) { opWornButton(wearpos.slot, op) }
        }
    }

    private fun Player.opWorn(wornSlot: Int, op: IfButtonOp) {
        ifClose(eventBus)
        if (isAccessProtected) {
            resendSlot(worn, 0)
            return
        }
        protectedAccess.launch(this) { interactions.interact(this, worn, wornSlot, op) }
    }

    private fun IfOverlayButton.opWornButton(wornSlot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            interactions.examine(player, player.worn, wornSlot)
            return
        }
        player.opWorn(wornSlot, op)
    }

    private fun mappedComponents(): Map<Wearpos, ComponentType> {
        val resolver = enumResolver[enums.equipment_tab_to_slots_map]
        check(resolver.isNotEmpty) { "Equipment component enum must not be empty: $resolver" }

        val invalidWearpos = resolver.keys.filter { Wearpos[it] == null }
        check(invalidWearpos.isEmpty()) { "Invalid wearpos for keys: $invalidWearpos" }

        val invalidComponent = resolver.values.filter { it == null }
        check(invalidComponent.isEmpty()) { "Equipment enum must not have null values: $resolver" }

        return resolver.associate { checkNotNull(Wearpos[it.key]) to checkNotNull(it.value) }
    }
}
