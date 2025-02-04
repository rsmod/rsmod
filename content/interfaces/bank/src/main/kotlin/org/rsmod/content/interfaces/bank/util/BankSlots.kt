package org.rsmod.content.interfaces.bank.util

import org.rsmod.game.inv.Inventory

object BankSlots {
    fun shiftLeadingGapsToTail(target: Inventory, indices: IntRange): GapShiftResult {
        val firstNonNullIndex = indices.firstOrNull { target[it] != null }

        // If all slots in the range are `null`, no shifting is needed - return early.
        if (firstNonNullIndex == null) {
            val size = (indices.last - indices.first) + 1
            return GapShiftResult(leadingGaps = size, compactSize = 0, trailingGaps = size)
        }

        val firstNullIndex = indices.firstOrNull { target[it] == null }

        // If there are no gaps (`null` slots) in the range, return early to prevent unnecessary
        // updates.
        if (firstNullIndex == null) {
            return GapShiftResult(leadingGaps = 0, compactSize = 0, trailingGaps = 0)
        }

        // If the first `null` found is at the last slot, return early - nothing needs shifting.
        if (firstNullIndex == indices.last) {
            val exclusiveSize = indices.last - indices.first
            return GapShiftResult(leadingGaps = 0, compactSize = exclusiveSize, trailingGaps = 1)
        }

        // Since `firstNonNullIndex` is not null, we can safely use `last` instead of `lastOrNull`.
        val lastNonNullIndex = indices.last { target[it] != null }

        var writeIndex = indices.first
        for (readIndex in firstNonNullIndex..lastNonNullIndex) {
            if (target[writeIndex] != target[readIndex]) {
                target[writeIndex] = target[readIndex]
            }
            if (writeIndex != readIndex && target[readIndex] != null) {
                target[readIndex] = null
            }
            writeIndex++
        }

        for (index in writeIndex..indices.last) {
            if (target[index] != null) {
                target[index] = null
            }
        }

        val compactSize = writeIndex
        val leadingNullCount = firstNonNullIndex - indices.first
        val trailingNullCount = indices.last - writeIndex + 1
        return GapShiftResult(leadingNullCount, compactSize, trailingNullCount)
    }

    /**
     * @param leadingGaps The number of `null` slots at the beginning of the range **before shifting
     *   begins**.
     * @param compactSize The number of valid objs that take up the beginning of the range,
     *   excluding the trailing nulls that should be trimmed off.
     * @param trailingGaps The number of `null` slots at the end of the range **after shifting is
     *   complete**.
     */
    data class GapShiftResult(val leadingGaps: Int, val compactSize: Int, val trailingGaps: Int)
}
