package org.rsmod.plugins.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.rsmod.game.model.mob.Player
import org.rsmod.game.ui.Component
import org.rsmod.plugins.api.model.event.DownstreamEvent
import org.rsmod.plugins.api.model.ui.Gameframe
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.testing.assertions.assertAny
import org.rsmod.plugins.testing.assertions.assertLast
import org.rsmod.plugins.testing.simple.SimpleGameTestExtension
import org.rsmod.plugins.testing.simple.SimpleGameTestState
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

@ExtendWith(SimpleGameTestExtension::class)
class PlayerUITests {

    @Test
    fun SimpleGameTestState.testOpenModal() = runGameTest {
        val modal = NamedInterface(10)
        val attackTab = component.gameframe_target_attack
        val skillsTab = component.gameframe_target_skills
        withPlayer {
            openModal(modal, attackTab)
            assertEquals(1, ui.modals.size)
            assertTrue(ui.modals.any { it.key.packed == attackTab.packed })
            openModal(modal, skillsTab)
            assertEquals(2, ui.modals.size)
            assertTrue(ui.modals.any { it.key.packed == skillsTab.packed })
        }
    }

    @Test
    fun SimpleGameTestState.testCloseModal() = runGameTest {
        val modal = NamedInterface(10)
        val target = component.gameframe_target_attack
        withPlayer {
            openModal(modal, target)
            check(ui.modals.size == 1)
            check(ui.modals.any { it.key.packed == target.packed })
            closeModal(NamedComponent(300, 1))
            assertEquals(1, ui.modals.size)
            closeModal(target)
            assertTrue(ui.modals.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenOverlay() = runGameTest {
        val overlay = NamedInterface(10)
        val attackTab = component.gameframe_target_attack
        val skillsTab = component.gameframe_target_skills
        withPlayer {
            openOverlay(overlay, attackTab)
            assertEquals(1, ui.overlays.size)
            assertTrue(ui.overlays.any { it.key.packed == attackTab.packed })
            openOverlay(overlay, skillsTab)
            assertEquals(2, ui.overlays.size)
            assertTrue(ui.overlays.any { it.key.packed == skillsTab.packed })
        }
    }

    @Test
    fun SimpleGameTestState.testCloseOverlay() = runGameTest {
        val modal = NamedInterface(10)
        val target = component.gameframe_target_attack
        withPlayer {
            openOverlay(modal, target)
            check(ui.overlays.size == 1)
            check(ui.overlays.any { it.key.packed == target.packed })
            closeOverlay(NamedComponent(300, 1))
            assertEquals(1, ui.overlays.size)
            closeOverlay(target)
            assertTrue(ui.overlays.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testCloseInterface() = runGameTest {
        val interf = NamedInterface(10)
        val target = component.gameframe_target_attack
        withPlayer {
            openModal(interf, target)
            check(ui.modals.size == 1)
            check(ui.modals.any { it.key.packed == target.packed })
            closeInterface(NamedInterface(300))
            assertEquals(1, ui.modals.size)
            closeInterface(interf)
            assertTrue(ui.modals.isEmpty())
        }
        withPlayer {
            openOverlay(interf, target)
            check(ui.overlays.size == 1)
            check(ui.overlays.any { it.key.packed == target.packed })
            closeInterface(NamedInterface(300))
            assertEquals(1, ui.overlays.size)
            closeInterface(interf)
            assertTrue(ui.overlays.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenGameframe() = runGameTest {
        withPlayer {
            openGameframe(TestGameframe)
            assertEquals(1, ui.topLevel.size)
            assertEquals(TestGameframe.topLevel.id, ui.topLevel.first().id)
            assertEquals(TestGameframe.references.size, ui.gameframe.size)
            assertLast(DownstreamEvent.IfOpenTop::class) { it.topLevel == TestGameframe.topLevel }
            assertLast(IfOpenTop::class) { it.interfaceId == TestGameframe.topLevel.id }
            assertEquals(TestGameframe.references.size, ui.overlays.size)
            assertEquals(TestGameframe.references, ui.gameframe.toNamedComponents())
            interf.attack_tab.let { tab ->
                assertAny(DownstreamEvent.IfOpenSub::class) { it.sub.id == tab.id }
                assertAny(IfOpenSub::class) { it.interfaceId == tab.id }
            }
            interf.skills_tab.let { tab ->
                assertAny(DownstreamEvent.IfOpenSub::class) { it.sub.id == tab.id }
                assertAny(IfOpenSub::class) { it.interfaceId == tab.id }
            }
            interf.quest_tab.let { tab ->
                assertAny(DownstreamEvent.IfOpenSub::class) { it.sub.id == tab.id }
                assertAny(IfOpenSub::class) { it.interfaceId == tab.id }
            }
        }
    }

    @Test
    fun SimpleGameTestState.testGameframeModalConversion() = runGameTest {
        val modal = NamedInterface(10)
        val originalAttackTarget = component.gameframe_target_attack
        val originalSkillsTarget = component.gameframe_target_skills
        val currentAttackTarget = TestGameframe.attack_tab
        val currentSkillsTarget = TestGameframe.skills_tab
        withPlayer {
            openTestGameframe()
            openModal(modal, originalAttackTarget)
            // Test that _a_ modal has been opened.
            assertEquals(1, ui.modals.size)
            // Test that the standard gameframe attack tab was _not_ opened.
            assertFalse(ui.modals.any { it.key.packed == originalAttackTarget.packed })
            // Test that the converted gameframe (TestGameframe) attack tab _is_ open.
            assertTrue(ui.modals.any { it.key.packed == currentAttackTarget.packed })
            // Open a secondary modal.
            openModal(modal, originalSkillsTarget)
            // Test that there are now two open modals.
            assertEquals(2, ui.modals.size)
            // Test that the standard gameframe skills tab was _not_ opened.
            assertFalse(ui.modals.any { it.key.packed == originalSkillsTarget.packed })
            // Test that the converted gameframe (TestGameframe) skills tab _is_ open.
            assertTrue(ui.modals.any { it.key.packed == currentSkillsTarget.packed })
            // Close first modal.
            closeModal(originalAttackTarget)
            assertTrue(ui.modals.none { it.value.id == currentAttackTarget.interfaceId })
            assertEquals(1, ui.modals.size)
            assertEquals(ui.modals.entries.first().key.packed, currentSkillsTarget.packed)
            // Close second and last modal.
            closeModal(originalSkillsTarget)
            assertTrue(ui.modals.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testGameframeOverlayConversion() = runGameTest {
        val overlay = NamedInterface(10)
        val originalAttackTarget = component.gameframe_target_attack
        val originalSkillsTarget = component.gameframe_target_skills
        val currentAttackTarget = TestGameframe.attack_tab
        val currentSkillsTarget = TestGameframe.skills_tab
        withPlayer {
            openTestGameframe()
            openOverlay(overlay, originalAttackTarget)
            // Test that _an_ overlay  has been opened.
            assertEquals(1, ui.overlays.size)
            // Test that the standard gameframe attack tab was _not_ opened.
            assertFalse(ui.overlays.any { it.key.packed == originalAttackTarget.packed })
            // Test that the converted gameframe (TestGameframe) attack tab _is_ open.
            assertTrue(ui.overlays.any { it.key.packed == currentAttackTarget.packed })
            // Open a secondary modal.
            openOverlay(overlay, originalSkillsTarget)
            // Test that there are now two open overlays.
            assertEquals(2, ui.overlays.size)
            // Test that the standard gameframe skills tab was _not_ opened.
            assertFalse(ui.overlays.any { it.key.packed == originalSkillsTarget.packed })
            // Test that the converted gameframe (TestGameframe) skills tab _is_ open.
            assertTrue(ui.overlays.any { it.key.packed == currentSkillsTarget.packed })
            // Close first overlay.
            closeOverlay(originalAttackTarget)
            assertTrue(ui.overlays.none { it.value.id == currentAttackTarget.interfaceId })
            assertEquals(1, ui.overlays.size)
            assertEquals(ui.overlays.entries.first().key.packed, currentSkillsTarget.packed)
            // Close second and last overlay.
            closeOverlay(originalSkillsTarget)
            assertTrue(ui.overlays.isEmpty())
        }
    }

    private fun Player.openTestGameframe() {
        openGameframe(TestGameframe)
        check(ui.topLevel.any { it.id == TestGameframe.topLevel.id })
        check(ui.gameframe.isNotEmpty())
        // Clear any modals and overlays that can interfere with test results.
        ui.modals.clear()
        ui.overlays.clear()
    }

    private fun Map<Component, Component>.toNamedComponents(): Map<NamedComponent, NamedComponent> {
        return entries.associate { NamedComponent(it.key.packed) to NamedComponent(it.value.packed) }
    }

    private object TestGameframe : Gameframe {

        val attack_tab = interf.attack_tab.child(1)
        val skills_tab = interf.skills_tab.child(2)
        val quests_tab = interf.quest_tab.child(3)

        override val topLevel: NamedInterface = NamedInterface(id = 500)

        override val references: Map<NamedComponent, NamedComponent> = mapOf(
            component.gameframe_target_attack to attack_tab,
            component.gameframe_target_skills to skills_tab,
            component.gameframe_target_quests to quests_tab
        )
    }
}
