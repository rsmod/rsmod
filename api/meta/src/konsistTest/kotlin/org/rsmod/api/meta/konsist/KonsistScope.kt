package org.rsmod.api.meta.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoKDocDeclaration
import com.lemonappdev.konsist.api.ext.list.functions
import com.lemonappdev.konsist.api.ext.list.kDocs
import com.lemonappdev.konsist.api.ext.list.properties
import com.lemonappdev.konsist.api.ext.list.withKDoc

object KonsistScope : KoScope by Konsist.scopeFromProject()

// NOTE: Filling this out as needed.
fun KonsistScope.allKDocs(): List<KoKDocDeclaration> {
    val classes = classes()
    val interfaces = interfaces()
    return buildList {
        this += interfaces.withKDoc().kDocs
        this += interfaces.functions().withKDoc().kDocs
        this += interfaces.properties().withKDoc().kDocs
        this += classes.withKDoc().kDocs
        this += classes.functions().withKDoc().kDocs
        this += classes.properties().withKDoc().kDocs
        this += functions().withKDoc().kDocs
    }
}
