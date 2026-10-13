package org.runerealm.content.interfaces.prayer.tab.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.isProtectionPrayer
import org.runerealm.api.player.isProtectionPrayerDisabled
import org.runerealm.api.player.output.ClientScripts
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.output.soundSynth
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.stat.prayerLvl
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.player.vars.resyncVar
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.content.interfaces.prayer.tab.Prayer
import org.runerealm.content.interfaces.prayer.tab.PrayerRepository
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_queues
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_sounds
import org.runerealm.content.interfaces.prayer.tab.util.PROTECTION_PRAYERS_DISABLED_MESSAGE
import org.runerealm.content.interfaces.prayer.tab.util.disablePrayerDrain
import org.runerealm.content.interfaces.prayer.tab.util.disablePrayerStatRegen
import org.runerealm.content.interfaces.prayer.tab.util.enablePrayerDrain
import org.runerealm.content.interfaces.prayer.tab.util.enablePrayerStatRegen
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class PrayerTabScript
@Inject
private constructor(
    private val repo: PrayerRepository,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startup() {
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
        if (player.prayerLvl == 0) {
            // Note: This is probably implicitly called by some other function, but as of now, we do
            // not know what that is.
            ClientScripts.pvpIconsComLevelRange(player, player.combatLevel)
            return
        }
        if (!prayer.hasAllRequirements(player)) {
            val message = player.failedRequirementMessage(prayer)
            player.resyncVar(prayer.enabled)
            mesbox(message)
            return
        }
        if (isProtectionPrayer(prayer.enabled) && player.isProtectionPrayerDisabled()) {
            player.resyncVar(prayer.enabled)
            mes(PROTECTION_PRAYERS_DISABLED_MESSAGE)
            return
        }
        disableCollisions(prayer)
        vars[prayer.enabled] = 1
        soundSynth(prayer.sound)
        enablePrayerDrain()
        enablePrayerStatRegen(prayer)
        if (prayer.overhead != null) {
            player.overheadIcon = prayer.overhead
        }
    }

    private fun ProtectedAccess.disablePrayer(prayer: Prayer) {
        vars[prayer.enabled] = 0
        soundSynth(prayer_sounds.disable)
        disablePrayerStatRegen(prayer)
        if (prayer.overhead != null && player.overheadIcon == prayer.overhead) {
            player.overheadIcon = null
        }

        // When all prayers are manually disabled, the quick prayer flag should also be disabled.
        if (vars[varbits.enabled_prayers] == 0) {
            vars[varbits.quickprayer_active] = 0
            disablePrayerDrain()
        }
    }

    private fun ProtectedAccess.disableCollisions(collisions: Iterable<Prayer>) {
        for (collision in collisions) {
            if (player.vars[collision.enabled] == 0) {
                continue
            }
            vars[collision.enabled] = 0
            disablePrayerStatRegen(collision)
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
