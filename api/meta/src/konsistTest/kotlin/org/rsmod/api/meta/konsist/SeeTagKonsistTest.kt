package org.rsmod.api.meta.konsist

import com.lemonappdev.konsist.api.declaration.KoKDocDeclaration
import com.lemonappdev.konsist.api.ext.list.tagprovider.withSeeTags
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class SeeTagKonsistTest {
    @Test
    fun `test see tag references are formatted correctly`() {
        val docs = KonsistScope.allKDocs().validateSeeTagParsing()
        docs.withSeeTags().assertTrue(additionalMessage = SEE_TAG_FORMAT_VALIDATION_ERROR_MESSAGE) {
            it.seeTags.all { tag -> tag.value.isValidSeeTagFormat() }
        }
    }

    /**
     * Validates that `see` tags in KDoc declarations do not throw unhandled exceptions during
     * parsing.
     *
     * This function addresses a bug in Konsist where `see` tags that are not followed by the
     * expected format (such as whitespace and a valid string reference) may cause errors during
     * parsing. These errors can lead to unhandled exceptions, resulting in test failures. By using
     * this function, such exceptions are caught, and a reasonable error message is output instead
     * of an unexpected internal exception.
     *
     * Additionally, this function handles cases where code examples in KDocs contain arbitrary tags
     * (e.g., `@Inject`), which can cause `see` tags to be parsed improperly, leading to similar
     * parsing errors.
     *
     * @return The original list of `KoKDocDeclaration` objects, ensuring it remains unmodified
     *   while confirming that no exceptions were thrown during the validation process.
     */
    private fun List<KoKDocDeclaration>.validateSeeTagParsing(): List<KoKDocDeclaration> {
        for (declaration in this) {
            assertDoesNotThrow(
                message = { "$SEE_TAG_PARSING_ERROR_MESSAGE\n\nFile: ${declaration.location}\n\n" },
                executable = { declaration.seeTags },
            )
        }
        return this
    }

    private companion object {
        private val SEE_TAG_FORMAT_VALIDATION_ERROR_MESSAGE: String =
            """
                |   @see references must be written within square brackets.
                |   Example: `@see [org.foo.FooClass]`
            """
                .trimMargin()

        private val SEE_TAG_PARSING_ERROR_MESSAGE: String =
            """
                Illegal @see tag declaration - tag must be followed by a valid reference!
                Example: `@see [org.bar.BarClass]`
            """
                .trimIndent()
    }
}

private fun String.isValidSeeTagFormat(): Boolean =
    isBlank() || isValidSeeTagReference() || isValidSeeTagHyperlink()

private fun String.isValidSeeTagReference(): Boolean = first() == '[' && last() == ']'

private fun String.isValidSeeTagHyperlink(): Boolean = startsWith("<img")
