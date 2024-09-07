package org.rsmod.api.meta.konsist

import com.lemonappdev.konsist.api.ext.list.enumConstants
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class EnumCasingKonsistTest {
    @Test
    fun `enum entries require PascalCase`() {
        KonsistScope.classes().enumConstants.assertTrue { it.name.isPascalCase() }
    }
}

private fun String.isPascalCase(): Boolean {
    if (isBlank()) {
        return false
    } else if (!this[0].isUpperCase()) {
        return false
    } else if (length > 1 && this == uppercase()) {
        return false
    }
    for (i in 1 until this.length) {
        if (this[i] == '_') {
            return false
        }
    }
    return true
}
