package org.runerealm.content.interfaces.combat.tab.configs

import org.runerealm.api.type.refs.queue.QueueReferences

typealias combat_queues = CombatTabQueues

object CombatTabQueues : QueueReferences() {
    val sa_instant_spec = find("sa_instant_spec")
    val attackstyle_change = find("attackstyle_change")
}
