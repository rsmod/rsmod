package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.refs.queue.QueueReferences

internal typealias bank_queues = BankQueues

object BankQueues : QueueReferences() {
    val bank_compress = find("bank_compress")
}
