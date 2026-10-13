package org.runerealm.content.skills.fishing.configs

import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.seq.SeqReferences

typealias fishing_objs = FishingObjs

typealias fishing_seqs = FishingSeqs

object FishingObjs : ObjReferences() {
    val small_fishing_net = find("net")
    val fishing_rod = find("fishing_rod")
    val fly_fishing_rod = find("fly_fishing_rod")
    val fishing_bait = find("fishing_bait")
    val feather = find("feather")

    val raw_shrimps = find("raw_shrimp")
    val raw_anchovies = find("raw_anchovies")
    val raw_sardine = find("raw_sardine")
    val raw_herring = find("raw_herring")
    val raw_trout = find("raw_trout")
    val raw_pike = find("raw_pike")
    val raw_salmon = find("raw_salmon")
}

object FishingSeqs : SeqReferences() {
    val small_net = find("human_smallnet")
    val rod_casting = find("human_fishing_casting")
}
