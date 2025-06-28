package org.rsmod.api.db.util

import java.sql.ResultSet
import java.time.LocalDateTime

public fun ResultSet.getLocalDateTime(columnLabel: String): LocalDateTime? {
    val time = getString(columnLabel) ?: return null
    return LocalDateTime.parse(time, DatabaseDate.DATE_FORMATTER)
}

public fun ResultSet.getStringOrNull(columnLabel: String): String? {
    return getString(columnLabel).takeUnless { wasNull() }
}

public fun ResultSet.getIntOrNull(columnLabel: String): Int? {
    return getInt(columnLabel).takeUnless { wasNull() }
}
