package org.runerealm.content.skills.cooking.configs

import org.runerealm.api.type.refs.category.CategoryReferences
import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.queue.QueueReferences
import org.runerealm.api.type.refs.seq.SeqReferences

typealias cooking_objs = CookingObjs

object CookingObjs : ObjReferences() {
    val raw_shrimps = find("raw_shrimp")
    val raw_anchovies = find("raw_anchovies")
    val raw_sardine = find("raw_sardine")
    val raw_herring = find("raw_herring")
    val raw_trout = find("raw_trout")
    val raw_pike = find("raw_pike")
    val raw_salmon = find("raw_salmon")
    val raw_beef = find("raw_beef")
    val raw_rat_meat = find("raw_rat_meat")
    val raw_bear_meat = find("raw_bear_meat")
    val raw_chicken = find("raw_chicken")

    val shrimps = find("shrimp")
    val anchovies = find("anchovies")
    val sardine = find("sardine")
    val herring = find("herring")
    val trout = find("trout")
    val pike = find("pike")
    val salmon = find("salmon")
    val cooked_meat = find("cooked_meat")
    val cooked_chicken = find("cooked_chicken")

    val burnt_shrimp = find("burnt_shrimp")
    val burnt_fish_anchovies = find("burntfish1")
    val burnt_fish_trout = find("burntfish2")
    val burnt_fish_herring = find("burntfish3")
    val burnt_fish_sardine = find("burntfish5")
    val burnt_meat = find("burnt_meat")
    val burnt_chicken = find("burnt_chicken")
}

object CookingSeqs : SeqReferences() {
    val range = find("human_cooking")
    val fire = find("human_firecooking")
}

object CookingCategories : CategoryReferences() {
    val fire = find("cooking_fire")
    val range = find("cooking_range")
}

object CookingLocs : LocReferences() {
    /** The Cook-o-matic 100 in the Lumbridge Castle kitchen. */
    val lumbridge_range = find("cooksquestrange")
}

object CookingNpcs : NpcReferences() {
    val lumbridge_cook = find("cook")
}

object CookingQueues : QueueReferences() {
    val cook = find("cooking")
}
