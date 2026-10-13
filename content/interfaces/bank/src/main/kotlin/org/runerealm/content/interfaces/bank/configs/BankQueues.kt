package org.runerealm.content.interfaces.bank.configs

import org.runerealm.api.type.refs.queue.QueueReferences

internal typealias bank_queues = BankQueues

object BankQueues : QueueReferences() {
    val bank_compress = find("bank_compress")
}
