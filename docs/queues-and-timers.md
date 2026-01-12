# Queues and Timers

This document describes queue and timer behavior in RS Mod.

## Overview

Queues and timers schedule script work.

- A queue runs once when its delay reaches zero.
- A timer runs on a fixed interval until cleared.

Scheduling requires `cycles > 0`.

**Protected access gates player work:**
- Normal player queues and timers run only while `player.isAccessProtected == false`.
- Soft player queues and timers still process while `player.isAccessProtected == true` and publish as plain events.

## Player Queues

Player queues schedule one-shot work.

Implementation note: player queues are stored in two lists:
- `player.queueList`: soft, normal, strong, long-accelerate, long-discard.
- `player.weakQueueList`: weak.

Weak queues are also cleared by `player.ifClose`.

### Queue Types

#### Weak Queue
```kotlin
weakQueue(queue: QueueType, cycles: Int, args: Any? = null)
```

**Characteristics:**
- Stored in `player.weakQueueList`.
- Cleared by `player.ifClose`.
    - Called automatically at the start of player queue processing if `player.queueList.strongQueues > 0`.
    - Also called when the client requests closing modals.
- Published only when `player.isAccessProtected == false`.

#### Soft Queue
```kotlin
softQueue(queue: QueueType, cycles: Int, args: Any? = null)
```

**Characteristics:**
- Published even while `player.isAccessProtected == true`.
- Published as `PlayerQueueEvents.Soft`.
- Before publishing, the processor invokes `player.ifClose`.

#### Normal Queue
```kotlin
queue(queue: QueueType, cycles: Int, args: Any? = null)
```

**Characteristics:**
- Published only when `player.isAccessProtected == false`.
- Published as `PlayerQueueEvents.Protected`.
- Runs inside `ProtectedAccess`.

#### Strong Queue
```kotlin
strongQueue(queue: QueueType, cycles: Int, args: Any? = null)
```

**Characteristics:**
- If at least one strong queue exists at the start of queue processing, `player.ifClose` is invoked before publishing any queues.
- Before publishing a strong queue, the processor invokes `player.ifClose`.
- Clears weak queues via `player.ifClose`.

Concrete examples:
- Queued hits use strong queues by default via `Player.queueHit` with `strongQueue = true`.
- Recoil damage can opt out via `strongQueue = false`.

#### Long Queue
```kotlin
longQueueAccelerate(queue: QueueType, cycles: Int, args: Any? = null)
longQueueDiscard(queue: QueueType, cycles: Int, args: Any? = null)
```

**Characteristics:**
- Long accelerate queues have their remaining delay forced to zero while `player.pendingLogout == true`.
- Pending logout does not proceed if any queued entry exists that is not a long discard queue.

### Processing Order

Player queues are processed by `PlayerQueueProcessor`.

- If `player.queueList.strongQueues > 0`, the processor calls `player.ifClose` before publishing any queues.
- Each tick, queued entries have `remainingCycles` decremented once per tick.
- When `player.pendingLogout == true` and the queue category is long accelerate, the processor forces `remainingCycles = 0`.
- When `remainingCycles` reaches zero, the processor publishes the queue only if it can be launched.
  - Soft queues always launch.
  - Normal, strong, long accelerate, long discard launch only while `player.isAccessProtected == false`.
- Soft and strong categories invoke `player.ifClose` right before publishing.
- Weak queues are processed from `player.weakQueueList` and are published only while `player.isAccessProtected == false`.

### API

```kotlin
weakQueue(queue: QueueType, cycles: Int, args: Any? = null)
clearWeakQueue(queue: QueueType)

softQueue(queue: QueueType, cycles: Int, args: Any? = null)

queue(queue: QueueType, cycles: Int, args: Any? = null)
strongQueue(queue: QueueType, cycles: Int, args: Any? = null)

longQueueAccelerate(queue: QueueType, cycles: Int, args: Any? = null)
longQueueDiscard(queue: QueueType, cycles: Int, args: Any? = null)
```

## Player Timers

Timers execute scripts at fixed intervals and automatically reschedule themselves after execution.

### Normal Timers
```kotlin
timer(timerType: TimerType, cycles: Int)
```

**Characteristics:**
- Normal timers are skipped while `player.isAccessProtected == true`.
- Normal timer handlers run in `ProtectedAccess`.
- After the event runs, the timer is rescheduled with the same interval if it was not cleared by the handler.

### Soft Timers
```kotlin
softTimer(timerType: TimerType, cycles: Int)
```

**Characteristics:**
- Soft timers are processed even while `player.isAccessProtected == true`.
- Soft timer handlers run as `PlayerTimerEvent.Soft`.
- After the event runs, the timer is rescheduled with the same interval if it was not cleared by the handler.

### API

```kotlin
timer(timerType: TimerType, cycles: Int)
clearTimer(timerType: TimerType)

softTimer(timerType: TimerType, cycles: Int)
clearSoftTimer(timerType: TimerType)
```

### Key Differences: Timer vs Soft Timer

| Feature | Normal Timer | Soft Timer |
|---------|-------------|------------|
| **Processed while `player.isAccessProtected == true`** | No | Yes |
| **Handler context** | `ProtectedAccess` | `PlayerTimerEvent.Soft` |
| **Reschedule rule** | Reschedules if not cleared during handler | Reschedules if not cleared during handler |

## NPC Queues and Timers

NPCs use a separate queue and timer system.

- NPC timers store multiple entries in `npc.timerMap`.
- Timer processing can stop early for a tick if `npc.isBusy` becomes true.
- Timer handlers run in `StandardNpcAccess`.

NPC queues use a single queue type with `StandardNpcAccess` handlers.

NPC AI uses a built-in timer.

## Controller Queues and Timers

Controllers are area-based entities with their own queue and timer system.

- Controller queue and timer handlers run in `StandardConAccess`.
- Controller AI uses a built-in timer event.

## Examples

### Weak queue loop
```kotlin
weakQueue(queues.fletch_logs, cycles = 3, args = logType)
```

### Normal timer
```kotlin
timer(timers.poison, cycles = 30)
```

### Soft timer
```kotlin
softTimer(timers.freeze_immunity, cycles = 50)
```

## Best Practices

- Use weak queues for work that should be cancelled by `player.ifClose`.
- Use normal queues and timers when you need `ProtectedAccess`.
- Use strong queues when you also need the strong pre-close behavior.
- Use soft queues and timers only for work that must run while access protected.
- Clear timers when the effect ends since timers reschedule unless cleared.

## References

- OSRS queues: <https://osrs-docs.com/docs/mechanics/queues/>
- OSRS timers: <https://osrs-docs.com/docs/mechanics/timers/>
- Source: `api/script/src/main/kotlin/org/rsmod/api/script/PlayerScriptEventExtensions.kt`
- Source: `api/player/src/main/kotlin/org/rsmod/api/player/protect/ProtectedAccess.kt`
