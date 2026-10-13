package org.runerealm.content.interfaces.bank.util

fun IntRange.offset(by: Int): IntRange = (by + start)..(last + by)
