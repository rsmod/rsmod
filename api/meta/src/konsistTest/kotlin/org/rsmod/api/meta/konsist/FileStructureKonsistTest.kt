package org.rsmod.api.meta.konsist

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.ext.list.indexOfFirstInstance
import com.lemonappdev.konsist.api.ext.list.indexOfLastInstance
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class FileStructureKonsistTest {
    @Test
    fun `no empty files allowed`() {
        KonsistScope.files.assertFalse { it.text.isEmpty() }
    }

    @Test
    fun `no wildcard imports allowed`() {
        KonsistScope.imports.assertFalse { it.isWildcard }
    }

    @Test
    fun `properties are declared before functions`() {
        KonsistScope.classes().assertTrue {
            val lastKoPropertyDeclarationIndex =
                it.declarations(includeNested = false, includeLocal = false)
                    .indexOfLastInstance<KoPropertyDeclaration>()

            val firstKoFunctionDeclarationIndex =
                it.declarations(includeNested = false, includeLocal = false)
                    .indexOfFirstInstance<KoFunctionDeclaration>()

            if (lastKoPropertyDeclarationIndex != -1 && firstKoFunctionDeclarationIndex != -1) {
                lastKoPropertyDeclarationIndex < firstKoFunctionDeclarationIndex
            } else {
                true
            }
        }
    }

    @Test
    fun `companion object is last declaration in the class`() {
        KonsistScope.classes().assertTrue {
            val companionObject =
                it.objects(includeNested = false).lastOrNull { obj ->
                    obj.hasModifier(KoModifier.COMPANION)
                }
            if (companionObject != null) {
                it.declarations(includeNested = false, includeLocal = false).last() ==
                    companionObject
            } else {
                true
            }
        }
    }
}
