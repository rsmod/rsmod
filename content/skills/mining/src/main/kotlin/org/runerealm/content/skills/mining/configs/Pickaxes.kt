package org.runerealm.content.skills.mining.configs

import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.seq.SeqType

/**
 * A pickaxe usable for Mining.
 *
 * @param level the Mining level required to mine with the pickaxe.
 * @param ticks the number of game ticks between each mining roll.
 * @param fastRollChance a `1/n` chance for a roll to take one tick less than [ticks] (dragon tier
 *   and crystal pickaxes), or `0` if the pickaxe has no such effect.
 *
 * Values are taken from the OSRS Wiki ([Pickaxe](https://oldschool.runescape.wiki/w/Pickaxe)).
 */
data class Pickaxe(
    val obj: ObjType,
    val level: Int,
    val ticks: Int,
    val anim: SeqType,
    val fastRollChance: Int = 0,
)

object Pickaxes {
    private val objs = mining_objs
    private val seqs = mining_seqs

    val all: List<Pickaxe> =
        listOf(
            Pickaxe(objs.bronze_pickaxe, level = 1, ticks = 8, anim = seqs.bronze_pickaxe),
            Pickaxe(objs.iron_pickaxe, level = 1, ticks = 7, anim = seqs.iron_pickaxe),
            Pickaxe(objs.steel_pickaxe, level = 6, ticks = 6, anim = seqs.steel_pickaxe),
            Pickaxe(objs.black_pickaxe, level = 11, ticks = 5, anim = seqs.black_pickaxe),
            Pickaxe(objs.mithril_pickaxe, level = 21, ticks = 5, anim = seqs.mithril_pickaxe),
            Pickaxe(objs.adamant_pickaxe, level = 31, ticks = 4, anim = seqs.adamant_pickaxe),
            Pickaxe(objs.rune_pickaxe, level = 41, ticks = 3, anim = seqs.rune_pickaxe),
            dragon(objs.dragon_pickaxe, seqs.dragon_pickaxe),
            dragon(objs.dragon_pickaxe_upgraded, seqs.dragon_pickaxe_upgraded),
            dragon(objs.dragon_pickaxe_or_zalcano, seqs.dragon_pickaxe),
            dragon(objs.dragon_pickaxe_or_trailblazer, seqs.dragon_pickaxe_or_trailblazer),
            dragon(objs.infernal_pickaxe, seqs.infernal_pickaxe),
            dragon(objs.infernal_pickaxe_uncharged, seqs.infernal_pickaxe),
            dragon(objs.infernal_pickaxe_or, seqs.infernal_pickaxe_or),
            dragon(objs.infernal_pickaxe_or_uncharged, seqs.infernal_pickaxe_or),
            dragon(objs.third_age_pickaxe, seqs.third_age_pickaxe),
            Pickaxe(
                objs.crystal_pickaxe,
                level = 71,
                ticks = 3,
                anim = seqs.crystal_pickaxe,
                fastRollChance = 4,
            ),
            Pickaxe(
                objs.crystal_pickaxe_inactive,
                level = 71,
                ticks = 3,
                anim = seqs.crystal_pickaxe,
                fastRollChance = 4,
            ),
        )

    private val byObj: Map<Int, Pickaxe> = all.associateBy { it.obj.id }

    operator fun get(obj: Int): Pickaxe? = byObj[obj]

    private fun dragon(obj: ObjType, anim: SeqType): Pickaxe =
        Pickaxe(obj, level = 61, ticks = 3, anim = anim, fastRollChance = 6)
}
