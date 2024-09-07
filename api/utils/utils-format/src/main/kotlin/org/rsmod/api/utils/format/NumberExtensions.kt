package org.rsmod.api.utils.format

import java.text.DecimalFormat

private val decimalFormatter = DecimalFormat()

public val Number.formatAmount: String
    get() = decimalFormatter.format(this)
