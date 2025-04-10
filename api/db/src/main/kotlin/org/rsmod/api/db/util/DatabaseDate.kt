package org.rsmod.api.db.util

import java.time.format.DateTimeFormatter

internal object DatabaseDate {
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
}
