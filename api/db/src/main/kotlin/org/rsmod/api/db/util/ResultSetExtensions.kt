package org.rsmod.api.db.util

import java.sql.ResultSet
import java.time.LocalDateTime

public fun ResultSet.getLocalDateTime(columnLabel: String): LocalDateTime? {
    val time = getString(columnLabel) ?: return null
    return LocalDateTime.parse(time, DatabaseDate.DATE_FORMATTER)
}
