package org.rsmod.game

import kotlin.contracts.contract

public class GameUpdate {
    public var state: State? = null
        private set

    /**
     * Resets the current [state], clearing any active game update status.
     *
     * This only resets the internal state. It does **not** clear any reboot timer state previously
     * sent to player clients. Clearing client-side state should be coordinated separately,
     * typically via [clear].
     *
     * @see [clear]
     */
    public fun reset() {
        state = null
    }

    /**
     * Marks the [state] as [State.Clear], signaling that reboot timer state should be cleared from
     * player clients.
     *
     * This function must be called **before** [State.Updating] is reached. Afterward, [reset] can
     * be used to fully reset the update state.
     *
     * This function only updates the internal state; it does not directly clear client-side timers.
     * It is the responsibility of the caller to handle client notifications.
     */
    public fun clear() {
        check(state != State.Updating) { "Game has already signaled for shutdown." }
        state = State.Clear
    }

    /**
     * Sets the [state] to [State.Countdown] to begin a reboot countdown.
     *
     * This function should be called when the server needs to start a timed shutdown sequence. The
     * [cycles] parameter specifies the number of game cycles remaining before the update is
     * triggered.
     *
     * Setting this state does **not** automatically broadcast countdown updates to player clients;
     * external systems are responsible for notifying players and managing the countdown
     * progression.
     *
     * @param cycles the number of game cycles to count down from. Must be positive or zero.
     * @throws IllegalArgumentException if [cycles] is negative.
     * @throws IllegalStateException if the server has already entered the [State.Updating] state.
     */
    public fun setCountdown(cycles: Int) {
        require(cycles >= 0) { "Countdown cycles must be positive: $cycles" }
        check(state != State.Updating) { "Game has already signaled for shutdown." }
        state = State.Countdown(cycles)
    }

    /**
     * Sets the [state] to [State.Updating] to indicate that the server has finished the reboot
     * countdown and is now updating.
     *
     * This function should be called once [State.Countdown] reaches `0`.
     *
     * Setting this state does **not** automatically terminate the server or affect player clients;
     * external systems must handle these actions.
     */
    public fun setUpdating() {
        state = State.Updating
    }

    public fun isUpdating(): Boolean = state == State.Updating

    public sealed class State {
        public data class Countdown(val start: Int, var current: Int = start) : State()

        public data object Clear : State()

        public data object Updating : State()
    }

    public companion object {
        public fun State?.isCountdown(): Boolean {
            contract { returns(true) implies (this@isCountdown is State.Countdown) }
            return this is State.Countdown
        }

        public fun State.isClear(): Boolean = this == State.Clear

        public fun State?.isUpdating(): Boolean = this == State.Updating
    }
}
