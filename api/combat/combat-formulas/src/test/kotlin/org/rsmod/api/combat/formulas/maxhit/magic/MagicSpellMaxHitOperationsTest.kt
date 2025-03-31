package org.rsmod.api.combat.formulas.maxhit.magic

import java.util.EnumSet
import kotlin.math.round
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

private typealias SpellAttr = CombatSpellAttributes

class MagicSpellMaxHitOperationsTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate max hit with loadout`(loadout: Loadout) {
        val expectedModifiedMaxHit = loadout.modifiedMaxHit
        val expectedFinalMinHit = loadout.finalMinHit
        val expectedFinalMaxHit = loadout.finalMaxHit
        val magicLevel = loadout.magicLevel
        val bonusDmg = round(loadout.bonusDmgPercent * 10).toInt()
        val prayerDmg = round(loadout.prayerDmgPercent * 10).toInt()
        val spellAttributes = loadout.spellAttributes
        val npcAttributes = loadout.npcAttributes
        val elementalWeakness = loadout.elementalWeaknessPercent

        val modifiedMaxHit =
            MagicMaxHitOperations.modifySpellBaseDamage(
                baseDamage = loadout.spellMaxHit,
                sourceMagic = magicLevel,
                sourceBaseMagicDmgBonus = bonusDmg,
                sourceMagicPrayerBonus = prayerDmg,
                spellAttributes = spellAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(expectedModifiedMaxHit, modifiedMaxHit)

        val finalMaxHit =
            MagicMaxHitOperations.modifySpellPostSpec(
                modifiedDamage = modifiedMaxHit,
                baseDamage = loadout.spellMaxHit,
                attackRate = loadout.attackRate,
                targetWeaknessPercent = elementalWeakness,
                spellAttributes = spellAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(expectedFinalMaxHit, finalMaxHit.last)
        assertEquals(expectedFinalMinHit, finalMaxHit.first)
    }

    data class Loadout(
        val spellMaxHit: Int = 0,
        val modifiedMaxHit: Int = spellMaxHit,
        val finalMinHit: Int = 0,
        val finalMaxHit: Int = modifiedMaxHit,
        val magicLevel: Int = 99,
        val bonusDmgPercent: Double = 0.0,
        val prayerDmgPercent: Double = 0.0,
        val spellAttributes: EnumSet<SpellAttr> = EnumSet.noneOf(SpellAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
        val elementalWeaknessPercent: Int = 0,
        val attackRate: Int = 5,
    ) {
        fun withSaturatedHeart(): Loadout {
            val add = 4 + (magicLevel * 0.1).toInt()
            return copy(magicLevel = magicLevel + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (magicLevel * 0.16).toInt()
            return copy(magicLevel = magicLevel + add)
        }

        fun withMagicDmgBonus(bonusPercent: Double) = copy(bonusDmgPercent = bonusPercent)

        fun withMysticVigour() = copy(prayerDmgPercent = 3.0)

        fun withAugury() = copy(prayerDmgPercent = 4.0)

        fun withSpellAttributes(vararg attributes: SpellAttr) =
            copy(spellAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withElementalWeaknessPercent(percent: Int) = copy(elementalWeaknessPercent = percent)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                Loadout(spellMaxHit = 8, modifiedMaxHit = 8).withAugury(),
                Loadout(spellMaxHit = 8, modifiedMaxHit = 8, finalMaxHit = 12)
                    .withAugury()
                    .withElementalWeaknessPercent(percent = 50)
                    .withSpellAttributes(SpellAttr.FireSpell)
                    .withNpcAttributes(NpcAttr.FireWeakness),
                Loadout(spellMaxHit = 12, modifiedMaxHit = 13, finalMaxHit = 19)
                    .withAugury()
                    .withMagicDmgBonus(bonusPercent = 5.0)
                    .withElementalWeaknessPercent(percent = 50)
                    .withSpellAttributes(SpellAttr.FireSpell)
                    .withNpcAttributes(NpcAttr.FireWeakness),
                Loadout(spellMaxHit = 12, modifiedMaxHit = 15)
                    .withAugury()
                    .withMagicDmgBonus(bonusPercent = 5.5)
                    .withSpellAttributes(SpellAttr.FireSpell, SpellAttr.AmuletOfAvarice)
                    .withNpcAttributes(NpcAttr.FireWeakness, NpcAttr.Revenant),
                Loadout(spellMaxHit = 24, modifiedMaxHit = 29, finalMaxHit = 29)
                    .withAugury()
                    .withMagicDmgBonus(bonusPercent = 4.5)
                    .withSpellAttributes(SpellAttr.FireSpell, SpellAttr.BlackMaskI)
                    .withNpcAttributes(NpcAttr.SlayerTask),
                Loadout(spellMaxHit = 24, modifiedMaxHit = 30, finalMaxHit = 33)
                    .withSmellingSalts()
                    .withMysticVigour()
                    .withMagicDmgBonus(bonusPercent = 24.0)
                    .withSpellAttributes(SpellAttr.FireSpell, SpellAttr.FireTome),
                Loadout(spellMaxHit = 0, modifiedMaxHit = 39)
                    .withSaturatedHeart()
                    .withAugury()
                    .withMagicDmgBonus(bonusPercent = 24.0)
                    .withSpellAttributes(SpellAttr.MagicDart, SpellAttr.SlayerStaffE)
                    .withNpcAttributes(NpcAttr.SlayerTask),
            )
    }
}
