package gg.rsmod.cache.util

/**
 * A utility class containing [Character]-related constants.
 *
 * @author Tom
 */
object CharacterUtil {

    /**
     * An array of the valid characters that can be used in strings stored inside
     * the file system.
     */
    val VALID_CHARACTERS = charArrayOf(
        '\u20ac',
        '\u0000',
        '\u201a',
        '\u0192',
        '\u201e',
        '\u2026',
        '\u2020',
        '\u2021',
        '\u02c6',
        '\u2030',
        '\u0160',
        '\u2039',
        '\u0152',
        '\u0000',
        '\u017d',
        '\u0000',
        '\u0000',
        '\u2018',
        '\u2019',
        '\u201c',
        '\u201d',
        '\u2022',
        '\u2013',
        '\u2014',
        '\u02dc',
        '\u2122',
        '\u0161',
        '\u203a',
        '\u0153',
        '\u0000',
        '\u017e',
        '\u0178'
    )

    fun Char.toEncoded(): Byte {
        val value = toInt()
        if (value in 0 until 128 || value in 160 until 256) {
            return value.toByte()
        }
        val index = VALID_CHARACTERS.indexOf(this)
        if (index == -1) {
            return '?'.toByte()
        }
        return (-128 + index).toByte()
    }
}
