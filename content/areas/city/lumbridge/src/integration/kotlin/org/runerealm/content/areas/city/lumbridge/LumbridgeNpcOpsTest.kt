package org.runerealm.content.areas.city.lumbridge

import jakarta.inject.Inject
import kotlin.reflect.KClass
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.player.interact.NpcInteractions
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.generic.npcs.banker.Banker
import org.runerealm.content.generic.npcs.cow.Cow
import org.runerealm.content.generic.npcs.cow.CowCalf
import org.runerealm.content.generic.npcs.cow.DairyCow
import org.runerealm.content.generic.npcs.ducks.Ducks
import org.runerealm.content.generic.npcs.person.GenericPerson
import org.runerealm.content.generic.npcs.sheep.Sheep
import org.runerealm.content.other.ironman.IronmanTutor
import org.runerealm.content.skills.fishing.scripts.Fishing
import org.runerealm.content.skills.thieving.Pickpocketing
import org.runerealm.game.interact.InteractionOp
import org.runerealm.game.type.npc.UnpackedNpcType
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript

/**
 * Audits every npc spawned in Lumbridge (`npcs.toml`): each right-click op the client shows for the
 * npc's visible type (after resolving multinpcs with a fresh player's vars) must have a handler.
 * Npcs hidden by their multinpc var for a fresh player (quest-only npcs) are skipped.
 */
class LumbridgeNpcOpsTest {
    @Test
    fun GameTestState.`every lumbridge npc op has a handler`() =
        runInjectedGameTest(OpsTestDeps::class, null, *SCRIPTS) { deps ->
            val byName = npcTypes.values.associateBy { it.internalName }
            val names = spawnedNpcNames()
            assertTrue(names.size > 50) { "Expected the Lumbridge spawn list, got: $names" }

            val coords = CoordGrid(0, 50, 50, 20, 20)
            allocZoneCollision(coords)
            val checker = registerPlayer(coords)
            val missing = mutableListOf<String>()
            var checked = 0
            for (name in names) {
                val type = byName[name] ?: error("Unknown npc type: $name")
                val visible = visibleType(deps.interactions, type, checker.vars) ?: continue
                val npc = spawnNpc(coords.translateX(1), type)
                for (op in InteractionOp.entries) {
                    if (!visible.hasOp(op) || visible.op[op.slot - 1] == ATTACK) {
                        continue
                    }
                    checked++
                    if (!deps.interactions.hasOpTrigger(checker, npc, op)) {
                        val text = visible.op[op.slot - 1]
                        missing += "$name (${visible.internalName}) op${op.slot} '$text'"
                    }
                }
                npcRepo.del(npc, Int.MAX_VALUE)
            }
            assertTrue(checked > 60) { "Only $checked ops checked." }
            assertTrue(missing.isEmpty()) {
                "Ops without a handler:\n${missing.joinToString("\n")}"
            }
        }

    /** The type the client draws for [type], or `null` if the npc is hidden for these vars. */
    private fun visibleType(
        interactions: NpcInteractions,
        type: UnpackedNpcType,
        vars: org.runerealm.game.vars.VarPlayerIntMap,
    ): UnpackedNpcType? {
        if (!type.isMultiNpc) {
            return type
        }
        return interactions.multiNpc(type, vars)
    }

    private fun spawnedNpcNames(): List<String> {
        val stream =
            javaClass.getResourceAsStream(SPAWNS_RESOURCE) ?: error("Missing $SPAWNS_RESOURCE")
        val text = stream.bufferedReader().use { it.readText() }
        return NPC_LINE.findAll(text).map { it.groupValues[1] }.distinct().toList()
    }

    class OpsTestDeps @Inject constructor(val interactions: NpcInteractions)

    private companion object {
        const val SPAWNS_RESOURCE = "/org/runerealm/content/areas/city/lumbridge/npcs.toml"

        val NPC_LINE = Regex("""(?m)^npc = '([^']+)'""")

        /**
         * "Attack" is handled for every npc by `PvNCombatScript`'s default op2 trigger, which
         * cannot start in an isolated test scope (its spell registry is only filled by the full
         * server).
         */
        const val ATTACK = "Attack"

        val SCRIPTS: Array<KClass<out PluginScript>> =
            LUMBRIDGE_NPC_SCRIPTS +
                arrayOf(
                    Banker::class,
                    Cow::class,
                    CowCalf::class,
                    DairyCow::class,
                    Ducks::class,
                    GenericPerson::class,
                    Sheep::class,
                    Fishing::class,
                    IronmanTutor::class,
                    Pickpocketing::class,
                )
    }
}
