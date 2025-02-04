package org.rsmod.content.interfaces.bank.util

fun IntRange.offset(by: Int): IntRange = (by + start)..(last + by)
