package org.rsmod.plugins.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.rsmod.game.model.mob.Player
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface
import org.rsmod.plugins.api.model.ui.Gameframe
import org.rsmod.plugins.testing.simple.SimpleGameTestExtension
import org.rsmod.plugins.testing.simple.SimpleGameTestState
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

@ExtendWith(SimpleGameTestExtension::class)
class PlayerUITests {

    @Test
    fun SimpleGameTestState.testOpenTopLevel() = runGameTest {
        val topLevel = NamedInterface(10)
        withPlayer {
            openTopLevel(topLevel)
            assertEquals(1, ui.topLevel.size)
            assertEquals(topLevel.id, ui.topLevel.firstOrNull()?.id)
        }
    }

    @Test
    fun SimpleGameTestState.testCloseTopLevel() = runGameTest {
        val topLevel = NamedInterface(10)
        withPlayer {
            openTopLevel(topLevel)
            check(ui.topLevel.isNotEmpty())
            check(ui.topLevel.firstOrNull()?.id == topLevel.id)
            closeTopLevels()
            assertTrue(ui.topLevel.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenModal() = runGameTest {
        val modal = NamedInterface(10)
        withPlayer {
            openModal(modal, component.gameframe_target_attack)
            assertEquals(1, ui.modals.size)
            assertNotNull(ui.modals[component.gameframe_target_attack.toSimple()])
            assertEquals(modal.id, ui.modals[component.gameframe_target_attack.toSimple()]?.id)
        }
    }

    @Test
    fun SimpleGameTestState.testCloseModal() = runGameTest {
        val modal = NamedInterface(10)
        withPlayer {
            openModal(modal, component.gameframe_target_attack)
            check(ui.modals.isNotEmpty())
            check(ui.modals[component.gameframe_target_attack.toSimple()]?.id == modal.id)
            closeModal(component.gameframe_target_chatbox)
            assertEquals(1, ui.modals.size)
            closeModal(component.gameframe_target_attack)
            assertTrue(ui.modals.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenGameframeModal() = runGameTest {
        val modal = NamedInterface(10)
        withPlayer {
            openTestGameframe()
            openModal(modal, component.gameframe_target_attack)
            assertEquals(1, ui.modals.size)
            assertNotNull(ui.modals[TestGameframe.attack_tab.toSimple()])
            assertEquals(modal.id, ui.modals[TestGameframe.attack_tab.toSimple()]?.id)
        }
    }

    @Test
    fun SimpleGameTestState.testCloseGameframeModal() = runGameTest {
        val modal = NamedInterface(10)
        withPlayer {
            openTestGameframe()
            openModal(modal, component.gameframe_target_attack)
            check(ui.modals.isNotEmpty())
            check(ui.modals[TestGameframe.attack_tab.toSimple()]?.id == modal.id)
            closeModal(TestGameframe.skills_tab)
            assertEquals(1, ui.modals.size)
            closeModal(TestGameframe.attack_tab)
            assertTrue(ui.modals.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenOverlay() = runGameTest {
        val overlay = NamedInterface(10)
        withPlayer {
            openOverlay(overlay, component.gameframe_target_attack)
            assertEquals(1, ui.overlays.size)
            assertNotNull(ui.overlays[component.gameframe_target_attack.toSimple()])
            assertEquals(overlay.id, ui.overlays[component.gameframe_target_attack.toSimple()]?.id)
        }
    }

    @Test
    fun SimpleGameTestState.testCloseOverlay() = runGameTest {
        val overlay = NamedInterface(10)
        withPlayer {
            openOverlay(overlay, component.gameframe_target_attack)
            check(ui.overlays.isNotEmpty())
            check(ui.overlays[component.gameframe_target_attack.toSimple()]?.id == overlay.id)
            closeOverlay(component.gameframe_target_chatbox)
            assertEquals(1, ui.overlays.size)
            closeOverlay(component.gameframe_target_attack)
            assertTrue(ui.overlays.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenGameframeOverlay() = runGameTest {
        val overlay = NamedInterface(10)
        withPlayer {
            openTestGameframe()
            openOverlay(overlay, component.gameframe_target_attack)
            assertEquals(1, ui.overlays.size)
            assertNotNull(ui.overlays[TestGameframe.attack_tab.toSimple()])
            assertEquals(overlay.id, ui.overlays[TestGameframe.attack_tab.toSimple()]?.id)
        }
    }

    @Test
    fun SimpleGameTestState.testCloseGameframeOverlay() = runGameTest {
        val overlay = NamedInterface(10)
        withPlayer {
            openTestGameframe()
            openOverlay(overlay, component.gameframe_target_attack)
            check(ui.overlays.isNotEmpty())
            check(ui.overlays[TestGameframe.attack_tab.toSimple()]?.id == overlay.id)
            closeOverlay(TestGameframe.skills_tab)
            assertEquals(1, ui.overlays.size)
            closeOverlay(TestGameframe.attack_tab)
            assertTrue(ui.overlays.isEmpty())
        }
    }

    @Test
    fun SimpleGameTestState.testOpenGameframe() = runGameTest {
        withPlayer {
            openGameframe(TestGameframe)
            check(ui.topLevel.size == 1)
            assertEquals(TestGameframe.topLevel.id, ui.topLevel.first().id)
            assertEquals(TestGameframe.mappings, ui.gameframe.toNamedComponents())
            assertEquals(TestGameframe.overlays.size, ui.overlays.size)
            assertTrue(TestGameframe.overlays.all { it.first.toSimple() in ui.overlays.values })
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

    private fun NamedComponent.toSimple(): Component = Component(packed)

    private fun NamedInterface.toSimple(): UserInterface = UserInterface(id)

    private object TestGameframe : Gameframe {

        val attack_tab by lazy { topLevel.child(1) }
        val skills_tab by lazy { topLevel.child(2) }
        val quests_tab by lazy { topLevel.child(3) }

        override val topLevel: NamedInterface = NamedInterface(id = 500)

        override val mappings: Map<NamedComponent, NamedComponent> = mapOf(
            component.gameframe_target_attack to attack_tab,
            component.gameframe_target_skills to skills_tab,
            component.gameframe_target_quests to quests_tab
        )

        override val overlays: List<Pair<NamedInterface, NamedComponent>> = listOf(
            interf.attack_tab to component.gameframe_target_attack,
            interf.skills_tab to component.gameframe_target_skills,
            interf.quest_tab to component.gameframe_target_quests
        )
    }
}
