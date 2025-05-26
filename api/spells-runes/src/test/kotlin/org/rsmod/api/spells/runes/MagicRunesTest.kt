package org.rsmod.api.spells.runes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.spells.runes.combo.ComboRune
import org.rsmod.api.spells.runes.combo.ComboRuneRepository
import org.rsmod.api.spells.runes.compact.CompactRuneRepository
import org.rsmod.api.spells.runes.fake.FakeRuneRepository
import org.rsmod.api.spells.runes.staves.StaffSubstituteRepository
import org.rsmod.api.spells.runes.subs.RuneSubstituteRepository
import org.rsmod.api.spells.runes.unlimited.UnlimitedRuneRepository
import org.rsmod.api.testing.factory.invFactory
import org.rsmod.api.testing.factory.objTypeFactory
import org.rsmod.api.testing.factory.varBitTypeFactory
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.varbit.VarBitType

class MagicRunesTest {
    @Test
    fun `cast spell with required rune in rune pouch`() {
        val inv = invFactory.createInv()
        val worn = invFactory.createWorn()

        val requirements = requirementsOf(requirement(air_rune, 1), requirement(mind_rune, 1))

        val compact = createCompactRepo()
        val pouch = createRunePouch(compact, Rune(air_rune, 1))
        inv[0] = InvObj(mind_rune, 1)

        val validation =
            MagicRunes.validateRequirements(
                inv = inv,
                worn = worn,
                pouch = pouch,
                requirements = requirements,
                useFakeRunes = false,
                runeFountain = false,
                compact = createCompactRepo(),
                unlimited = createUnlimitedRepo(),
                combos = createComboRepo(),
                fakes = createFakeRepo(),
                runeSubs = createRuneSubRepo(),
                staffSubs = createStaffSubRepo(),
            )

        val expected =
            listOf(
                hasEnough(varBitSource(pouch_count1, 1)),
                hasEnough(invSource(mind_rune, slot = 0)),
            )

        assertEquals(expected, validation)
    }

    @Test
    fun `cast spell with substitute rune in rune pouch`() {
        val inv = invFactory.createInv()
        val worn = invFactory.createWorn()

        val requirements =
            requirementsOf(
                requirement(wrath_rune, 1),
                requirement(fire_rune, 10),
                requirement(air_rune, 7),
            )

        val compact = createCompactRepo()
        val pouch = createRunePouch(compact, Rune(sunfire_rune, 5))
        inv[0] = InvObj(wrath_rune, 1)
        inv[1] = InvObj(fire_rune, 7)
        inv[2] = InvObj(air_rune, 7)

        val validation =
            MagicRunes.validateRequirements(
                inv = inv,
                worn = worn,
                pouch = pouch,
                requirements = requirements,
                useFakeRunes = false,
                runeFountain = false,
                compact = createCompactRepo(),
                unlimited = createUnlimitedRepo(),
                combos = createComboRepo(),
                fakes = createFakeRepo(),
                runeSubs = createRuneSubRepo(),
                staffSubs = createStaffSubRepo(),
            )

        val expected =
            listOf(
                hasEnough(invSource(wrath_rune, slot = 0, count = 1)),
                hasEnough(invSource(fire_rune, slot = 1, count = 7), varBitSource(pouch_count1, 3)),
                hasEnough(invSource(air_rune, slot = 2, count = 7)),
            )

        assertEquals(expected, validation)
    }

    @Test
    fun `fail cast with incorrect rune in rune pouch`() {
        val inv = invFactory.createInv()
        val worn = invFactory.createWorn()

        val requirements = requirementsOf(requirement(air_rune, 1), requirement(mind_rune, 1))

        val compact = createCompactRepo()
        val pouch = createRunePouch(compact, Rune(fire_rune, 1))
        inv[0] = InvObj(mind_rune, 1)

        val validation =
            MagicRunes.validateRequirements(
                inv = inv,
                worn = worn,
                pouch = pouch,
                requirements = requirements,
                useFakeRunes = false,
                runeFountain = false,
                compact = createCompactRepo(),
                unlimited = createUnlimitedRepo(),
                combos = createComboRepo(),
                fakes = createFakeRepo(),
                runeSubs = createRuneSubRepo(),
                staffSubs = createStaffSubRepo(),
            )

        val expected =
            listOf(
                hasEnough(invSource(mind_rune, slot = 0)),
                // Since air rune check is delegated to ensure it cannot be covered by a combo
                // rune, its validation is last.
                notEnough(air_rune),
            )

        assertEquals(expected, validation)
    }

    @TestWithArgs(ValidCastProvider::class)
    fun `spell cast passes validation`(cast: SpellCast) {
        val inv = cast.inv
        val worn = cast.worn
        val requirements = cast.requirementList()

        val validation =
            MagicRunes.validateRequirements(
                inv = inv,
                worn = worn,
                pouch = null,
                requirements = requirements,
                useFakeRunes = false,
                runeFountain = false,
                compact = createCompactRepo(),
                unlimited = createUnlimitedRepo(),
                combos = createComboRepo(),
                fakes = createFakeRepo(),
                runeSubs = createRuneSubRepo(),
                staffSubs = createStaffSubRepo(),
            )

        assertEquals(cast.expected, validation)
    }

    @TestWithArgs(InvalidCastProvider::class)
    fun `spell cast fails validation`(cast: SpellCast) {
        val inv = cast.inv
        val worn = cast.worn
        val requirements = cast.requirementList()

        val validation =
            MagicRunes.validateRequirements(
                inv = inv,
                worn = worn,
                pouch = null,
                requirements = requirements,
                useFakeRunes = false,
                runeFountain = false,
                compact = createCompactRepo(),
                unlimited = createUnlimitedRepo(),
                combos = createComboRepo(),
                fakes = createFakeRepo(),
                runeSubs = createRuneSubRepo(),
                staffSubs = createStaffSubRepo(),
            )

        assertEquals(cast.expected, validation)
    }

    data class SpellCast(
        val description: String,
        val inv: Inventory = invFactory.createInv(),
        val worn: Inventory = invFactory.createWorn(),
        var expected: MutableList<MagicRunes.Validation> = mutableListOf(),
    ) {
        internal val requirements = mutableListOf<MagicRunes.RequirementList.Requirement>()

        var weapon: InvObj?
            get() = worn[Wearpos.RightHand.slot]
            set(value) {
                worn[Wearpos.RightHand.slot] = value
            }

        internal fun require(init: MutableList<MagicRunes.RequirementList.Requirement>.() -> Unit) =
            apply {
                requirements.init()
            }

        fun expect(init: MutableList<MagicRunes.Validation>.() -> Unit) = apply { expected.init() }

        fun setup(init: SpellCast.() -> Unit) = apply(init)

        fun requirementList() = MagicRunes.RequirementList(requirements)

        override fun toString(): String = description

        companion object {
            internal fun create(description: String, init: SpellCast.() -> Unit): SpellCast {
                return SpellCast(description).apply(init)
            }
        }
    }

    private object ValidCastProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                SpellCast.create("Wind Strike") {
                    require {
                        this += requirement(air_rune, 1)
                        this += requirement(mind_rune, 1)
                    }

                    setup {
                        inv[0] = InvObj(air_rune)
                        inv[1] = InvObj(mind_rune)
                    }

                    expect {
                        this += hasEnough(invSource(air_rune, slot = 0, count = 1))
                        this += hasEnough(invSource(mind_rune, slot = 1, count = 1))
                    }
                },
                SpellCast.create("Wind Strike with Staff of air") {
                    require {
                        this += requirement(air_rune, 1)
                        this += requirement(mind_rune, 1)
                    }

                    setup {
                        weapon = InvObj(air_staff)
                        inv[1] = InvObj(mind_rune)
                    }

                    expect {
                        this += unlimited()
                        this += hasEnough(invSource(mind_rune, slot = 1, count = 1))
                    }
                },
                SpellCast.create("Wind Strike with Mist battlestaff") {
                    require {
                        this += requirement(air_rune, 1)
                        this += requirement(mind_rune, 1)
                    }

                    setup {
                        weapon = InvObj(mist_staff)
                        inv[1] = InvObj(mind_rune)
                    }

                    expect {
                        this += unlimited()
                        this += hasEnough(invSource(mind_rune, slot = 1, count = 1))
                    }
                },
                SpellCast.create("Wind Strike with Dust rune") {
                    require {
                        this += requirement(air_rune, 1)
                        this += requirement(mind_rune, 1)
                    }

                    setup {
                        inv[0] = InvObj(dust_rune)
                        inv[1] = InvObj(mind_rune)
                    }

                    expect {
                        this += hasEnough(invSource(mind_rune, slot = 1, count = 1))

                        // Since dust rune has "low priority" here, it will be taken last.
                        this += hasEnough(invSource(dust_rune, slot = 0, count = 1))
                    }
                },
                // If combo rune only covers the cost of 1 rune it has "low priority."
                SpellCast.create("Wind Strike prioritizing Air rune") {
                    require {
                        this += requirement(air_rune, 1)
                        this += requirement(mind_rune, 1)
                    }

                    setup {
                        inv[0] = InvObj(dust_rune)
                        inv[1] = InvObj(mind_rune)
                        inv[2] = InvObj(air_rune)
                    }

                    expect {
                        this += hasEnough(invSource(air_rune, slot = 2, count = 1))
                        this += hasEnough(invSource(mind_rune, slot = 1, count = 1))
                    }
                },
                SpellCast.create("Earth Bolt with Dust rune") {
                    require {
                        this += requirement(chaos_rune, 1)
                        this += requirement(earth_rune, 3)
                        this += requirement(air_rune, 2)
                    }

                    setup {
                        inv[0] = InvObj(air_rune, 10)
                        inv[1] = InvObj(earth_rune, 10)
                        inv[5] = InvObj(mind_rune, 10)
                        inv[6] = InvObj(chaos_rune, 10)
                        inv[27] = InvObj(dust_rune, 10)
                    }

                    expect {
                        this += hasEnough(invSource(dust_rune, slot = 27, count = 2))
                        this += hasEnough(invSource(chaos_rune, slot = 6, count = 1))
                        this += hasEnough(invSource(earth_rune, slot = 1, count = 1))
                    }
                },
                SpellCast.create("Earth Bolt with Mud rune") {
                    require {
                        this += requirement(chaos_rune, 1)
                        this += requirement(earth_rune, 3)
                        this += requirement(air_rune, 2)
                    }

                    setup {
                        inv[0] = InvObj(air_rune, 10)
                        inv[6] = InvObj(chaos_rune, 10)
                        inv[27] = InvObj(mud_rune, 10)
                    }

                    expect {
                        this += hasEnough(invSource(chaos_rune, slot = 6, count = 1))
                        this += hasEnough(invSource(air_rune, slot = 0, count = 2))
                        this += hasEnough(invSource(mud_rune, slot = 27, count = 3))
                    }
                },
                SpellCast.create("Earth Bolt with Dust battlestaff") {
                    require {
                        this += requirement(chaos_rune, 1)
                        this += requirement(earth_rune, 3)
                        this += requirement(air_rune, 2)
                    }

                    setup {
                        weapon = InvObj(dust_staff)
                        inv[0] = InvObj(earth_rune, 5)
                        inv[6] = InvObj(chaos_rune, 5)
                        inv[27] = InvObj(smoke_rune, 5)
                    }

                    expect {
                        this += hasEnough(invSource(chaos_rune, slot = 6, count = 1))
                        // Both air and earth runes have an unlimited source from the staff.
                        this += unlimited()
                        this += unlimited()
                    }
                },
                SpellCast.create("Fire Surge with Smoke rune") {
                    require {
                        this += requirement(wrath_rune, 1)
                        this += requirement(fire_rune, 10)
                        this += requirement(air_rune, 7)
                    }

                    setup {
                        inv[0] = InvObj(smoke_rune, 10)
                        inv[2] = InvObj(wrath_rune, 1)
                    }

                    expect {
                        // Smoke rune is split into both high and low priority since it will cover
                        // the first 7 air and fire runes. (air rune only requires 7)
                        this += hasEnough(invSource(smoke_rune, slot = 0, count = 7))
                        this += hasEnough(invSource(wrath_rune, slot = 2, count = 1))
                        // Since air rune is not found in inv, smoke rune is used as fallback.
                        this += hasEnough(invSource(smoke_rune, slot = 0, count = 3))
                    }
                },
                SpellCast.create("Fire Surge with partial Sunfire rune") {
                    require {
                        this += requirement(wrath_rune, 1)
                        this += requirement(fire_rune, 10)
                        this += requirement(air_rune, 7)
                    }

                    setup {
                        inv[0] = InvObj(wrath_rune, 10)
                        inv[1] = InvObj(fire_rune, 5)
                        inv[2] = InvObj(air_rune, 10)
                        inv[3] = InvObj(sunfire_rune, 10)
                    }

                    expect {
                        this += hasEnough(invSource(wrath_rune, slot = 0, count = 1))
                        this +=
                            hasEnough(
                                invSource(fire_rune, slot = 1, count = 5),
                                invSource(sunfire_rune, slot = 3, count = 5),
                            )
                        this += hasEnough(invSource(air_rune, slot = 2, count = 7))
                    }
                },
                SpellCast.create("Saradomin Strike") {
                    require {
                        this += staffRequirement(saradomin_staff)
                        this += requirement(blood_rune, 2)
                        this += requirement(air_rune, 4)
                        this += requirement(fire_rune, 2)
                    }

                    setup {
                        weapon = InvObj(saradomin_staff)
                        inv[0] = InvObj(blood_rune, 10)
                        inv[1] = InvObj(air_rune, 10)
                        inv[2] = InvObj(lava_rune, 10)
                    }

                    expect {
                        this += unlimited()
                        this += hasEnough(invSource(blood_rune, slot = 0, count = 2))
                        this += hasEnough(invSource(air_rune, slot = 1, count = 4))
                        this += hasEnough(invSource(lava_rune, slot = 2, count = 2))
                    }
                },
                SpellCast.create("Lumbridge Teleport with Dust rune") {
                    require {
                        this += requirement(law_rune, 1)
                        this += requirement(air_rune, 3)
                        this += requirement(earth_rune, 1)
                    }

                    setup {
                        inv[0] = InvObj(air_rune, 10)
                        inv[1] = InvObj(earth_rune, 10)
                        inv[5] = InvObj(mind_rune, 10)
                        inv[6] = InvObj(law_rune, 10)
                        inv[27] = InvObj(dust_rune, 10)
                    }

                    expect {
                        this += hasEnough(invSource(dust_rune, slot = 27, count = 1))
                        this += hasEnough(invSource(law_rune, slot = 6, count = 1))
                        this += hasEnough(invSource(air_rune, slot = 0, count = 2))
                    }
                },
            )
        }
    }

    private object InvalidCastProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                SpellCast.create("Wind Strike missing Mind rune") {
                    require {
                        this += requirement(air_rune, 1)
                        this += requirement(mind_rune, 1)
                    }

                    setup {
                        inv[0] = InvObj(air_rune)
                        inv[1] = InvObj(fire_rune)
                    }

                    expect {
                        this += hasEnough(invSource(air_rune, slot = 0, count = 1))
                        this += notEnough(mind_rune)
                    }
                },
                SpellCast.create("Saradomin Strike with Saradomin staff in inv") {
                    require {
                        this += staffRequirement(saradomin_staff)
                        this += requirement(blood_rune, 2)
                        this += requirement(air_rune, 4)
                        this += requirement(fire_rune, 2)
                    }

                    setup {
                        inv[0] = InvObj(blood_rune, 10)
                        inv[1] = InvObj(air_rune, 10)
                        inv[2] = InvObj(lava_rune, 10)
                        inv[5] = InvObj(saradomin_staff)
                    }

                    expect {
                        this += notWorn(saradomin_staff)
                        this += hasEnough(invSource(blood_rune, slot = 0, count = 2))
                        this += hasEnough(invSource(air_rune, slot = 1, count = 4))
                        this += hasEnough(invSource(lava_rune, slot = 2, count = 2))
                    }
                },
            )
        }
    }

    private data class Rune(val type: ObjType, val count: Int)

    private companion object {
        val pouch_count1 = varBitTypeFactory.create(1)
        val pouch_count2 = varBitTypeFactory.create(2)
        val pouch_count3 = varBitTypeFactory.create(3)
        val pouch_count4 = varBitTypeFactory.create(4)

        val air_rune = createObjType(1, "Air rune")
        val water_rune = createObjType(2, "Water rune")
        val earth_rune = createObjType(3, "Earth rune")
        val fire_rune = createObjType(4, "Fire rune")
        val mind_rune = createObjType(5, "Mind rune")
        val chaos_rune = createObjType(6, "Chaos rune")
        val death_rune = createObjType(7, "Death rune")
        val blood_rune = createObjType(8, "Blood rune")
        val cosmic_rune = createObjType(9, "Cosmic rune")
        val nature_rune = createObjType(10, "Nature rune")
        val law_rune = createObjType(11, "Law rune")
        val body_rune = createObjType(12, "Body rune")
        val soul_rune = createObjType(13, "Soul rune")
        val astral_rune = createObjType(14, "Astral rune")
        val mist_rune = createObjType(15, "Mist rune")
        val mud_rune = createObjType(16, "Mud rune")
        val dust_rune = createObjType(17, "Dust rune")
        val lava_rune = createObjType(18, "Lava rune")
        val steam_rune = createObjType(19, "Steam rune")
        val smoke_rune = createObjType(20, "Smoke rune")
        val wrath_rune = createObjType(21, "Wrath rune")
        val sunfire_rune = createObjType(22, "Sunfire rune")

        val air_staff = createObjType(23, "Staff of air")
        val water_staff = createObjType(24, "Staff of water")
        val earth_staff = createObjType(25, "Staff of earth")
        val fire_staff = createObjType(26, "Staff of fire")

        val dust_staff = createObjType(27, "Dust battlestaff")
        val lava_staff = createObjType(28, "Lava battlestaff")
        val steam_staff = createObjType(29, "Steam battlestaff")
        val smoke_staff = createObjType(30, "Smoke battlestaff")
        val mud_staff = createObjType(31, "Mud battlestaff")
        val mist_staff = createObjType(32, "Mist battlestaff")

        val saradomin_staff = createObjType(33, "Saradomin staff")

        val air_rune_nz = createObjType(40, "Air rune (nz)")
        val water_rune_nz = createObjType(41, "Water rune (nz)")
        val earth_rune_nz = createObjType(42, "Earth rune (nz)")
        val fire_rune_nz = createObjType(43, "Fire rune (nz)")
        val chaos_rune_nz = createObjType(44, "Chaos rune (nz)")
        val death_rune_nz = createObjType(45, "Death rune (nz)")
        val blood_rune_nz = createObjType(46, "Blood rune (nz)")

        private fun createCompactRepo(): CompactRuneRepository {
            val compact: Map<ObjType, Int> =
                mapOf(
                    air_rune to 1,
                    water_rune to 2,
                    earth_rune to 3,
                    fire_rune to 4,
                    mind_rune to 5,
                    chaos_rune to 6,
                    death_rune to 7,
                    blood_rune to 8,
                    cosmic_rune to 9,
                    nature_rune to 10,
                    law_rune to 11,
                    body_rune to 12,
                    soul_rune to 13,
                    astral_rune to 14,
                    mist_rune to 15,
                    mud_rune to 16,
                    dust_rune to 17,
                    lava_rune to 18,
                    steam_rune to 19,
                    smoke_rune to 20,
                    wrath_rune to 21,
                    sunfire_rune to 22,
                )
            val repo = CompactRuneRepository()
            repo.init(compact)
            return repo
        }

        private fun createUnlimitedRepo(): UnlimitedRuneRepository {
            val staves =
                mapOf(
                    air_rune to setOf(air_staff, dust_staff, smoke_staff, mist_staff),
                    water_rune to setOf(water_staff, steam_staff, mud_staff, mist_staff),
                    earth_rune to setOf(earth_staff, dust_staff, lava_staff, mud_staff),
                    fire_rune to setOf(fire_staff, lava_staff, steam_staff, smoke_staff),
                )
            val highPriority =
                staves.entries.associate { it.key.id to it.value.map(ObjType::id).toSet() }
            val repo = UnlimitedRuneRepository()
            repo.init(highPriority, emptyMap())
            return repo
        }

        private fun createComboRepo(): ComboRuneRepository {
            val combos =
                listOf(
                    ComboRune(mist_rune, air_rune, water_rune),
                    ComboRune(dust_rune, air_rune, earth_rune),
                    ComboRune(mud_rune, water_rune, earth_rune),
                    ComboRune(smoke_rune, air_rune, fire_rune),
                    ComboRune(steam_rune, water_rune, fire_rune),
                    ComboRune(lava_rune, earth_rune, fire_rune),
                )
            val repo = ComboRuneRepository()
            repo.init(combos.associateBy { it.rune })
            return repo
        }

        // For now, we will not be testing fake runes.
        private fun createFakeRepo(): FakeRuneRepository = FakeRuneRepository()

        // For now, we will not be testing staff substitutes.
        private fun createStaffSubRepo(): StaffSubstituteRepository = StaffSubstituteRepository()

        private fun createRuneSubRepo(): RuneSubstituteRepository {
            val substitutes =
                mapOf(
                    air_rune to listOf(air_rune_nz),
                    water_rune to listOf(water_rune_nz),
                    earth_rune to listOf(earth_rune_nz),
                    fire_rune to listOf(fire_rune_nz, sunfire_rune),
                    chaos_rune to listOf(chaos_rune_nz),
                    death_rune to listOf(death_rune_nz),
                    blood_rune to listOf(blood_rune_nz),
                )
            val mapped = substitutes.entries.associate { it.key.id to it.value }
            val repo = RuneSubstituteRepository()
            repo.init(mapped)
            return repo
        }

        private fun createRunePouch(
            compact: CompactRuneRepository,
            vararg runes: Rune,
        ): MagicRunes.RunePouch {
            val mappedRunes = runes.mapNotNull { compact[it.type] }
            check(mappedRunes.size == runes.size) {
                "Expected ${mappedRunes.size} mapped runes in `compact` repository."
            }
            val compactId1 = mappedRunes.getOrNull(0) ?: 0
            val compactId2 = mappedRunes.getOrNull(1) ?: 0
            val compactId3 = mappedRunes.getOrNull(2) ?: 0
            val compactId4 = mappedRunes.getOrNull(3) ?: 0
            val count1 = runes.getOrNull(0)?.count ?: 0
            val count2 = runes.getOrNull(1)?.count ?: 0
            val count3 = runes.getOrNull(2)?.count ?: 0
            val count4 = runes.getOrNull(3)?.count ?: 0
            return MagicRunes.RunePouch(
                compactId1 = compactId1,
                compactId2 = compactId2,
                compactId3 = compactId3,
                compactId4 = compactId4,
                countVarBit1 = pouch_count1,
                countVarBit2 = pouch_count2,
                countVarBit3 = pouch_count3,
                countVarBit4 = pouch_count4,
                count1 = count1,
                count2 = count2,
                count3 = count3,
                count4 = count4,
            )
        }

        private fun requirement(type: ObjType, count: Int) =
            MagicRunes.RequirementList.Requirement(type, count, null)

        private fun requirementsOf(
            vararg requirements: MagicRunes.RequirementList.Requirement
        ): MagicRunes.RequirementList = MagicRunes.RequirementList(requirements.toList())

        private fun staffRequirement(type: ObjType) =
            MagicRunes.RequirementList.Requirement(
                type,
                remaining = 1,
                wornSlot = Wearpos.RightHand.slot,
            )

        fun unlimited() = MagicRunes.Validation.Valid.Unlimited

        fun hasEnough(vararg sources: MagicRunes.Source) =
            MagicRunes.Validation.Valid.HasEnough(sources.toList())

        fun invSource(type: ObjType, slot: Int, count: Int = 1) =
            MagicRunes.Source.InvSource(type, slot, count)

        fun varBitSource(type: VarBitType, count: Int = 1) =
            MagicRunes.Source.VarBitSource(type, count)

        fun notWorn(staff: ObjType) = MagicRunes.Validation.Invalid.NotWearing(staff)

        fun notEnough(rune: ObjType) = MagicRunes.Validation.Invalid.NotEnoughRunes(rune)

        private fun createObjType(id: Int, name: String): ObjType =
            objTypeFactory
                .create(id) {
                    this.internal = name.lowercase().replace(' ', '_')
                    this.name = name
                }
                .toHashedType()
    }
}
