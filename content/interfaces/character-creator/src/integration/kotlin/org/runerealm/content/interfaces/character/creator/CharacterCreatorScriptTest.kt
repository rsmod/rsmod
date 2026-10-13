package org.runerealm.content.interfaces.character.creator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.character.creator.configs.creator_components
import org.runerealm.content.interfaces.character.creator.configs.creator_interfaces
import org.runerealm.content.interfaces.character.creator.configs.creator_varbits
import org.runerealm.content.interfaces.character.creator.configs.creator_varps
import org.runerealm.game.entity.Player

class CharacterCreatorScriptTest {
    @Test
    fun GameTestState.`creator opens on login until design is confirmed`() =
        runGameTest(CharacterCreatorScript::class) {
            assertTrue(player.ui.containsModal(creator_interfaces.player_design))

            player.ifButton(creator_components.confirm)
            advance(ticks = 1)
            assertFalse(player.ui.containsModal(creator_interfaces.player_design))
            assertEquals(TUTORIAL_COMPLETE, player.vars[creator_varps.tutorial])

            val returning = Player()
            VarPlayerIntMapSetter.set(returning, creator_varps.tutorial, TUTORIAL_COMPLETE)
            registerPlayer(player = returning)
            assertFalse(returning.ui.containsModal(creator_interfaces.player_design))
        }

    @Test
    fun GameTestState.`body part arrows cycle through selectable kits`() =
        runGameTest(CharacterCreatorScript::class) {
            val identKits = cacheTypes.identKits
            val torsos = identKits.selectable(DesignBodyPart.Torso.part)
            val start = player.appearance.identKitSnapshot()[DesignBodyPart.Torso.part].toInt()
            val startIndex = torsos.indexOf(start)
            assertTrue(startIndex != -1)

            player.ifButton(creator_components.torso_right)
            advance(ticks = 1)
            val next = torsos[(startIndex + 1).mod(torsos.size)]
            assertEquals(
                next,
                player.appearance.identKitSnapshot()[DesignBodyPart.Torso.part].toInt(),
            )

            player.ifButton(creator_components.torso_left)
            player.ifButton(creator_components.torso_left)
            advance(ticks = 1)
            val prev = torsos[(startIndex - 1).mod(torsos.size)]
            assertEquals(
                prev,
                player.appearance.identKitSnapshot()[DesignBodyPart.Torso.part].toInt(),
            )
        }

    @Test
    fun GameTestState.`colour arrows wrap around the wiki palette`() =
        runGameTest(CharacterCreatorScript::class) {
            player.appearance.setColour(DesignColour.Feet.slot, 0)
            player.ifButton(creator_components.feet_col_left)
            advance(ticks = 1)
            assertEquals(5, player.appearance.coloursSnapshot()[DesignColour.Feet.slot].toInt())

            player.ifButton(creator_components.feet_col_right)
            advance(ticks = 1)
            assertEquals(0, player.appearance.coloursSnapshot()[DesignColour.Feet.slot].toInt())

            player.appearance.setColour(DesignColour.Skin.slot, 7)
            player.ifButton(creator_components.skin_right)
            advance(ticks = 1)
            assertEquals(0, player.appearance.coloursSnapshot()[DesignColour.Skin.slot].toInt())
        }

    @Test
    fun GameTestState.`body type b uses type b kits and she-her pronouns`() =
        runGameTest(CharacterCreatorScript::class) {
            val identKits = cacheTypes.identKits
            player.ifButton(creator_components.body_type_b)
            advance(ticks = 1)

            assertEquals(BODY_TYPE_B, player.appearance.bodyType)
            assertEquals(BODY_TYPE_B, player.vars[creator_varbits.body_type])
            assertEquals(PRONOUN_SHE_HER, player.appearance.pronoun)
            assertEquals(PRONOUN_SHE_HER, player.vars[creator_varbits.pronouns])

            val kits = player.appearance.identKitSnapshot()
            for (bodyPart in DesignBodyPart.entries) {
                val expected = identKits.selectable(bodyPart.part + BODY_PARTS_PER_TYPE).first()
                assertEquals(expected, kits[bodyPart.part].toInt(), "Body part: $bodyPart")
            }

            player.ifButton(creator_components.head_right)
            advance(ticks = 1)
            val hair = identKits.selectable(DesignBodyPart.Head.part + BODY_PARTS_PER_TYPE)
            assertEquals(hair[1], player.appearance.identKitSnapshot()[0].toInt())
        }

    @Test
    fun GameTestState.`they-them pronouns are kept when body type changes`() =
        runGameTest(CharacterCreatorScript::class) {
            player.ifButton(creator_components.pronoun_options, comsub = 3)
            advance(ticks = 1)
            assertEquals(PRONOUN_THEY_THEM, player.appearance.pronoun)

            player.ifButton(creator_components.body_type_b)
            advance(ticks = 1)
            assertEquals(BODY_TYPE_B, player.appearance.bodyType)
            assertEquals(PRONOUN_THEY_THEM, player.appearance.pronoun)

            player.ifButton(creator_components.pronoun_options, comsub = 1)
            advance(ticks = 1)
            assertEquals(PRONOUN_HE_HIM, player.appearance.pronoun)
            assertEquals(PRONOUN_HE_HIM, player.vars[creator_varbits.pronouns])
        }
}
