package org.runerealm.content.interfaces.prayer.tab.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.isProtectionPrayer
import org.runerealm.api.player.isProtectionPrayerDisabled
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.stat.prayerLvl
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.player.ui.ifOpenSub
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.player.vars.resyncVar
import org.runerealm.api.script.onIfClose
import org.runerealm.api.script.onIfOpen
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerQueue
import org.runerealm.content.interfaces.prayer.tab.Prayer
import org.runerealm.content.interfaces.prayer.tab.PrayerRepository
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_components
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_interfaces
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_queues
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_sounds
import org.runerealm.content.interfaces.prayer.tab.configs.prayer_varbits
import org.runerealm.content.interfaces.prayer.tab.util.PROTECTION_PRAYERS_DISABLED_MESSAGE
import org.runerealm.content.interfaces.prayer.tab.util.disablePrayerDrain
import org.runerealm.content.interfaces.prayer.tab.util.disablePrayerStatRegen
import org.runerealm.content.interfaces.prayer.tab.util.enablePrayerDrain
import org.runerealm.content.interfaces.prayer.tab.util.enablePrayerStatRegen
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.interf.IfSubType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class QuickPrayerScript
@Inject
constructor(
    private val repo: PrayerRepository,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerLogin { player.disableQuickPrayers() }
        onIfOverlayButton(prayer_components.quick_prayers_orb) { player.selectQuickPrayerOrb(op) }
        onPlayerQueue(prayer_queues.quick_prayer) { toggleQuickPrayers() }
        onIfOpen(prayer_interfaces.quickprayer) { player.onOpenQuickPrayerSetUp() }
        onIfClose(prayer_interfaces.quickprayer) { player.onCloseQuickPrayerSetUp() }
        onIfOverlayButton(prayer_components.quick_prayers_setup) {
            player.toggleQuickPrayer(comsub)
        }
        onIfOverlayButton(prayer_components.quick_prayers_close) { player.closeQuickPrayerSetUp() }
    }

    private fun Player.disableQuickPrayers() {
        usingQuickPrayers = false
    }

    private fun Player.selectQuickPrayerOrb(op: IfButtonOp) {
        if (op == IfButtonOp.Op1) {
            selectToggleQuickPrayers()
        } else if (op == IfButtonOp.Op2) {
            selectSetUpQuickPrayers()
        }
    }

    private fun Player.selectToggleQuickPrayers() {
        if (prayer_queues.quick_prayer in queueList) {
            return
        }
        ifClose(eventBus)
        val toggled = protectedAccess.launch(this) { toggleQuickPrayers() }
        if (!toggled) {
            strongQueue(prayer_queues.quick_prayer, 1)
        }
    }

    private fun ProtectedAccess.toggleQuickPrayers() {
        if (vars[varbits.quickprayer_active] != 0) {
            disableQuickPrayers()
        } else {
            enableQuickPrayers()
        }
    }

    private fun ProtectedAccess.enableQuickPrayers() {
        val selectedQuickPrayerVars = vars[prayer_varbits.quickprayer_selected]

        if (selectedQuickPrayerVars == 0) {
            mes("You haven't selected any quick-prayers.")
            player.resyncVar(varbits.quickprayer_active)
            return
        }

        if (player.prayerLvl == 0) {
            mes("You've run out of prayer points.")
            player.resyncVar(varbits.quickprayer_active)
            return
        }

        val quickPrayerVars = allowedQuickPrayers(selectedQuickPrayerVars)
        if (quickPrayerVars == 0) {
            mes(PROTECTION_PRAYERS_DISABLED_MESSAGE)
            player.resyncVar(varbits.quickprayer_active)
            return
        }

        val enabledPrayers = repo.toPrayerList(vars[varbits.enabled_prayers])
        for (prayer in enabledPrayers) {
            disablePrayerStatRegen(prayer)
        }
        disableOverhead()
        vars[varbits.enabled_prayers] = quickPrayerVars
        vars[varbits.quickprayer_active] = 1

        val quickPrayers = repo.toPrayerList(quickPrayerVars)
        for (prayer in quickPrayers) {
            vars[prayer.enabled] = 1
            soundSynth(prayer.sound)
            enablePrayerStatRegen(prayer)
            if (prayer.overhead != null) {
                player.overheadIcon = prayer.overhead
            }
        }
        enablePrayerDrain()
    }

    /** Removes protection prayers from [selected] while they are disabled (e.g., by Sever). */
    private fun ProtectedAccess.allowedQuickPrayers(selected: Int): Int {
        if (!player.isProtectionPrayerDisabled()) {
            return selected
        }
        var allowed = selected
        for (prayer in repo.prayerList) {
            if (isProtectionPrayer(prayer.enabled)) {
                allowed = allowed and (1 shl prayer.id).inv()
            }
        }
        return allowed
    }

    private fun ProtectedAccess.disableQuickPrayers() {
        val enabledPrayers = repo.toPrayerList(vars[varbits.enabled_prayers])
        for (prayer in enabledPrayers) {
            disablePrayerStatRegen(prayer)
        }
        disablePrayerDrain()
        disableOverhead()
        vars[varbits.enabled_prayers] = 0
        vars[varbits.quickprayer_active] = 0
        soundSynth(prayer_sounds.disable)
    }

    private fun ProtectedAccess.disableOverhead() {
        val overhead = player.overheadIcon ?: return
        if (constants.isOverhead(overhead)) {
            player.overheadIcon = null
        }
    }

    private fun Player.selectSetUpQuickPrayers() {
        val setUp = protectedAccess.launch(this) { setUpQuickPrayers() }
        if (!setUp) {
            mes("You can't set up your prayers at the moment.")
        }
    }

    private fun ProtectedAccess.setUpQuickPrayers() {
        ifOpenOverlay(prayer_interfaces.quickprayer, components.toplevel_target_side5)
        toplevelSidebuttonSwitch(constants.toplevel_prayer)
    }

    private fun Player.toggleQuickPrayer(comsub: Int) {
        val prayer = repo.prayerList.getOrNull(comsub)
        if (prayer == null) {
            throw IllegalArgumentException("Invalid quick prayer comsub: $comsub")
        }
        val enabled = selectedQuickPrayers and (1 shl prayer.id) != 0
        if (enabled) {
            disableQuickPrayer(prayer)
        } else {
            enableQuickPrayer(prayer)
        }
    }

    private fun Player.enableQuickPrayer(prayer: Prayer) {
        if (!prayer.hasAllRequirements(this)) {
            val message = failedRequirementMessage(prayer)
            mes(message)
            return
        }
        disableCollisions(prayer)
        selectedQuickPrayers = selectedQuickPrayers or (1 shl prayer.id)
    }

    private fun Player.disableQuickPrayer(prayer: Prayer) {
        selectedQuickPrayers = selectedQuickPrayers and (1 shl prayer.id).inv()
    }

    private fun Player.disableCollisions(collisions: Iterable<Prayer>) {
        for (collision in collisions) {
            selectedQuickPrayers = selectedQuickPrayers and (1 shl collision.id).inv()
        }
    }

    private fun Player.disableCollisions(prayer: Prayer) = disableCollisions(repo[prayer])

    private fun Player.onOpenQuickPrayerSetUp() {
        ifSetEvents(prayer_components.quick_prayers_setup, repo.prayerList.indices, IfEvent.Op1)
    }

    private fun Player.closeQuickPrayerSetUp() {
        ifCloseOverlay(prayer_interfaces.quickprayer, eventBus)
    }

    private fun Player.onCloseQuickPrayerSetUp() {
        ifOpenSub(
            interfaces.prayerbook,
            components.toplevel_target_side5,
            IfSubType.Overlay,
            eventBus,
        )
    }

    private fun Player.failedRequirementMessage(prayer: Prayer): String =
        if (prayer.hasBaseRequirement(this)) {
            prayer.lockedMessage()
        } else {
            prayer.levelReqMessage()
        }

    private fun Prayer.levelReqMessage(): String = "You need a Prayer level of $level to use $name."

    private fun Prayer.lockedMessage(): String = plainLockedMessage ?: levelReqMessage()
}

private var Player.selectedQuickPrayers by intVarBit(prayer_varbits.quickprayer_selected)
private var Player.usingQuickPrayers by boolVarBit(varbits.quickprayer_active)
