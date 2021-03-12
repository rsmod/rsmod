package org.rsmod.game.queue

import org.rsmod.game.coroutine.GameCoroutineTask

inline class GameQueue(val task: GameCoroutineTask)
inline class GameQueueBlock(val block: suspend () -> Unit)

sealed class QueueType {
    object Weak : QueueType()
    object Normal : QueueType()
    object Strong : QueueType()
}
