package org.rsmod.api.combat.formulas

import org.rsmod.api.config.refs.npcs
import org.rsmod.api.config.refs.params
import org.rsmod.api.testing.factory.npcTypeFactory
import org.rsmod.game.type.util.ParamMap
import org.rsmod.game.type.util.ParamMapBuilder

typealias test_npcs = FormulaTestNpcs

object FormulaTestNpcs {
    val man =
        npcTypeFactory.create {
            name = "Man"
            hitpoints = 7
            paramMap = buildParams {
                this[params.defence_stab] = -21
                this[params.defence_slash] = -21
                this[params.defence_crush] = -21
                this[params.defence_magic] = -21
                this[params.defence_light] = -21
                this[params.defence_standard] = -21
                this[params.defence_heavy] = -21
            }
        }

    val gnome_archer =
        npcTypeFactory.create {
            name = "Gnome Archer"
            hitpoints = 10
            ranged = 6
            paramMap = buildParams {
                this[params.defence_stab] = -30
                this[params.defence_slash] = -30
                this[params.defence_crush] = -30
                this[params.defence_magic] = -30
                this[params.defence_light] = -30
                this[params.defence_standard] = -30
                this[params.defence_heavy] = -30
            }
        }

    val corporeal_beast =
        npcTypeFactory.create(npcs.corp_beast.id) {
            name = "Corporeal Beast"
            size = 5
            hitpoints = 2000
            attack = 320
            strength = 320
            defence = 310
            magic = 350
            ranged = 150
            paramMap = buildParams {
                this[params.attack_melee] = 50
                this[params.defence_stab] = 25
                this[params.defence_slash] = 200
                this[params.defence_crush] = 100
                this[params.defence_magic] = 150
                this[params.defence_light] = 230
                this[params.defence_standard] = 230
                this[params.defence_heavy] = 100
            }
        }

    val abyssal_demon =
        npcTypeFactory.create {
            name = "Abyssal demon"
            hitpoints = 150
            attack = 97
            strength = 67
            defence = 135
            paramMap = buildParams {
                this[params.defence_stab] = 20
                this[params.defence_slash] = 20
                this[params.defence_crush] = 20
                this[params.defence_light] = 20
                this[params.defence_standard] = 20
                this[params.defence_heavy] = 20
            }
        }

    val dagannoth_rex =
        npcTypeFactory.create {
            name = "Dagannoth Rex"
            hitpoints = 255
            attack = 255
            strength = 255
            defence = 255
            magic = 0
            ranged = 255
            paramMap = buildParams {
                this[params.defence_stab] = 255
                this[params.defence_slash] = 255
                this[params.defence_crush] = 255
                this[params.defence_magic] = 10
                this[params.defence_light] = 255
                this[params.defence_standard] = 255
                this[params.defence_heavy] = 255
            }
        }

    val dagannoth_supreme =
        npcTypeFactory.create {
            name = "Dagannoth Supreme"
            hitpoints = 255
            attack = 255
            strength = 255
            defence = 128
            magic = 255
            ranged = 255
            paramMap = buildParams {
                this[params.defence_stab] = 10
                this[params.defence_slash] = 10
                this[params.defence_crush] = 10
                this[params.defence_magic] = 255
                this[params.defence_light] = 550
                this[params.defence_standard] = 550
                this[params.defence_heavy] = 550
            }
        }

    val dagannoth_prime =
        npcTypeFactory.create {
            name = "Dagannoth Prime"
            hitpoints = 255
            attack = 255
            strength = 255
            defence = 255
            magic = 255
            ranged = 0
            paramMap = buildParams {
                this[params.defence_stab] = 255
                this[params.defence_slash] = 255
                this[params.defence_crush] = 255
                this[params.defence_magic] = 255
                this[params.defence_light] = 10
                this[params.defence_standard] = 10
                this[params.defence_heavy] = 10
            }
        }

    val glod =
        npcTypeFactory.create {
            name = "Glod (Hard)"
            hitpoints = 255
            attack = 230
            strength = 240
            defence = 110
            paramMap = buildParams {
                this[params.defence_stab] = 105
                this[params.defence_slash] = 110
                this[params.defence_crush] = 130
                this[params.defence_magic] = 125
                this[params.defence_light] = 100
                this[params.defence_standard] = 100
                this[params.defence_heavy] = 100
            }
        }

    val abyssal_walker =
        npcTypeFactory.create {
            name = "Abyssal walker"
            hitpoints = 95
            attack = 5
            strength = 100
            defence = 95
            paramMap = buildParams {
                this[params.attack_melee] = 5
                this[params.melee_strength] = 10
                this[params.defence_stab] = 75
                this[params.defence_slash] = 75
                this[params.defence_crush] = 10
                this[params.defence_magic] = 75
                this[params.defence_light] = 75
                this[params.defence_standard] = 75
                this[params.defence_heavy] = 75
            }
        }

    val giant_rat =
        npcTypeFactory.create {
            name = "Giant rat (Scurrius)"
            hitpoints = 15
            attack = 100
            strength = 25
            defence = 10
            paramMap = buildParams {
                this[params.attack_melee] = 68
                this[params.melee_strength] = 5
                this[params.rat] = 1
            }
        }

    val chicken =
        npcTypeFactory.create {
            name = "Chicken"
            hitpoints = 3
            paramMap = buildParams {
                this[params.attack_melee] = -47
                this[params.melee_strength] = -42
                this[params.defence_stab] = -42
                this[params.defence_slash] = -42
                this[params.defence_crush] = -42
                this[params.defence_magic] = -42
                this[params.defence_light] = -42
                this[params.defence_standard] = -42
                this[params.defence_heavy] = -42
            }
        }

    val nex =
        npcTypeFactory.create {
            name = "Nex"
            size = 3
            hitpoints = 3400
            attack = 315
            strength = 200
            defence = 260
            magic = 230
            ranged = 350
            paramMap = buildParams {
                this[params.attack_melee] = 200
                this[params.melee_strength] = 20
                this[params.attack_magic] = 100
                this[params.npc_magic_damage_bonus] = 22
                this[params.attack_ranged] = 150
                this[params.ranged_strength] = 5
                this[params.defence_stab] = 40
                this[params.defence_slash] = 140
                this[params.defence_crush] = 60
                this[params.defence_magic] = 300
                this[params.defence_light] = 190
                this[params.defence_standard] = 190
                this[params.defence_heavy] = 150
            }
        }

    val vorkath =
        npcTypeFactory.create {
            name = "Vorkath"
            size = 7
            hitpoints = 750
            attack = 560
            strength = 308
            defence = 214
            magic = 150
            ranged = 308
            paramMap = buildParams {
                this[params.attack_melee] = 16
                this[params.melee_strength] = 0
                this[params.attack_magic] = 150
                this[params.npc_magic_damage_bonus] = 56
                this[params.attack_ranged] = 78
                this[params.ranged_strength] = 0
                this[params.defence_stab] = 26
                this[params.defence_slash] = 108
                this[params.defence_crush] = 108
                this[params.defence_magic] = 240
                this[params.defence_light] = 26
                this[params.defence_standard] = 26
                this[params.defence_heavy] = 26
                this[params.undead] = 1
                this[params.draconic] = 1
            }
        }

    val abyssal_sire =
        npcTypeFactory.create {
            name = "Abyssal Sire"
            size = 6
            hitpoints = 400
            attack = 180
            strength = 136
            defence = 250
            magic = 200
            paramMap = buildParams {
                this[params.attack_melee] = 65
                this[params.defence_stab] = 40
                this[params.defence_slash] = 60
                this[params.defence_crush] = 50
                this[params.defence_magic] = 20
                this[params.defence_light] = 60
                this[params.defence_standard] = 60
                this[params.defence_heavy] = 60
            }
        }

    val flockleader_geerin =
        npcTypeFactory.create {
            name = "Flockleader Geerin"
            size = 2
            hitpoints = 132
            attack = 80
            strength = 80
            defence = 175
            magic = 50
            ranged = 150
            paramMap = buildParams {
                this[params.attack_ranged] = 60
                this[params.ranged_strength] = 35
            }
        }

    val iorwerth_archer =
        npcTypeFactory.create {
            name = "Iorwerth Archer"
            hitpoints = 105
            attack = 10
            strength = 10
            defence = 80
            ranged = 90
            paramMap = buildParams {
                this[params.ranged_strength] = 8
                this[params.defence_stab] = 50
                this[params.defence_slash] = 50
                this[params.defence_crush] = 50
                this[params.defence_magic] = 60
                this[params.defence_light] = 70
                this[params.defence_standard] = 70
                this[params.defence_heavy] = 70
            }
        }

    private fun buildParams(init: ParamMapBuilder.() -> Unit): ParamMap {
        return ParamMapBuilder().apply(init).toParamMap()
    }
}
