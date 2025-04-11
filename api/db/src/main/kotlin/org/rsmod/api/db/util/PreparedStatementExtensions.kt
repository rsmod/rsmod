package org.rsmod.api.db.util

import java.sql.PreparedStatement
import java.sql.Types
import java.time.LocalDateTime

public fun PreparedStatement.setSqliteTimestamp(index: Int, time: LocalDateTime) {
    val formatted = time.format(DatabaseDate.DATE_FORMATTER)
    setString(index, formatted)
}

public fun PreparedStatement.setNullableInt(index: Int, value: Int?) {
    if (value != null) {
        setInt(index, value)
    } else {
        setNull(index, Types.INTEGER)
    }
}
