package org.rsmod.api.db.util

import java.sql.PreparedStatement
import java.time.LocalDateTime

public fun PreparedStatement.setSqliteTimestamp(index: Int, time: LocalDateTime) {
    val formatted = time.format(DatabaseDate.DATE_FORMATTER)
    setString(index, formatted)
}
