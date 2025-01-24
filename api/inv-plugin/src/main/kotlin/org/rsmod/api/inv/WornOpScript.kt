package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.enums
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class WornOpScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val enumResolver: EnumTypeMapResolver,
    private val interactions: WornInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startUp() {
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
