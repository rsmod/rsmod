package org.rsmod.content.interfaces.prayer.tab.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.content.interfaces.prayer.tab.Prayer
import org.rsmod.content.interfaces.prayer.tab.PrayerRepository
import org.rsmod.content.interfaces.prayer.tab.configs.prayer_queues
import org.rsmod.content.interfaces.prayer.tab.configs.prayer_sounds
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PrayerTabScript
@Inject
private constructor(
    private val repo: PrayerRepository,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        for ((component, prayer) in repo.prayerComponents) {
            onIfOverlayButton(component) { player.selectPrayer(prayer) }
        }
        onPlayerQueueWithArgs(prayer_queues.toggle) { togglePrayer(it.args) }
    }

    private fun Player.selectPrayer(prayer: Prayer) {
        ifClose(eventBus)
        if (isAccessProtected && !prayer.hasAllRequirements(this)) {
            val message = failedRequirementMessage(prayer).replace("<br>", " ")
            mes(message)
            resyncVar(prayer.enabled)
            soundSynth(prayer_sounds.disable)
            return
        }
        val toggled = protectedAccess.launch(this) { togglePrayer(prayer) }
        if (!toggled && canQueuePrayer()) {
            strongQueue(prayer_queues.toggle, 1, args = prayer)
        }
    }

    private suspend fun ProtectedAccess.togglePrayer(prayer: Prayer) {
        if (vars[prayer.enabled] != 0) {
            disablePrayer(prayer)
        } else {
            enablePrayer(prayer)
        }
    }

    private suspend fun ProtectedAccess.enablePrayer(prayer: Prayer) {
        if (!prayer.hasAllRequirements(player)) {
            val message = player.failedRequirementMessage(prayer)
            val lineHeight = if (message.contains("<br>")) 31 else 0
            player.resyncVar(prayer.enabled)
            mesbox(message, lineHeight)
            return
        }
        disableCollisions(prayer)
        vars[prayer.enabled] = 1
        soundSynth(prayer.sound)
        if (prayer.overhead != null) {
            player.overheadIcon = prayer.overhead
        }
    }

    private fun ProtectedAccess.disablePrayer(prayer: Prayer) {
        vars[prayer.enabled] = 0
        soundSynth(prayer_sounds.disable)
        if (prayer.overhead != null && player.overheadIcon == prayer.overhead) {
            player.overheadIcon = null
        }

        // When all prayers are manually disabled, the quick prayer flag should also be disabled.
        if (vars[varbits.enabled_prayers] == 0) {
            vars[varbits.quickprayer_active] = 0
        }
    }

    private fun ProtectedAccess.disableCollisions(collisions: Iterable<Prayer>) {
        for (collision in collisions) {
            if (player.vars[collision.enabled] == 0) {
                continue
            }
            vars[collision.enabled] = 0
        }
    }

    private fun ProtectedAccess.disableCollisions(prayer: Prayer) = disableCollisions(repo[prayer])

    private fun Player.failedRequirementMessage(prayer: Prayer): String =
        if (prayer.hasBaseRequirement(this)) {
            prayer.lockedMessage()
        } else {
            prayer.levelReqMessage()
        }

    private fun Player.canQueuePrayer(): Boolean = queueList.count(prayer_queues.toggle) < 10

    private fun Prayer.levelReqMessage(): String =
        "You need a <col=000080>Prayer</col> level of $level to use <col=000080>$name</col>."

    private fun Prayer.lockedMessage(): String = lockedMessage ?: levelReqMessage()
}
