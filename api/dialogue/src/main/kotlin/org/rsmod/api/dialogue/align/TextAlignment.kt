package org.rsmod.api.dialogue.align

import jakarta.inject.Inject
import kotlin.math.ceil
import org.rsmod.api.config.refs.fontmetrics
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.font.UnpackedFontMetricsType

public class TextAlignment @Inject constructor(fonts: FontMetricsTypeList) {
    private val dialogueFont by lazy { fonts[fontmetrics.q8_full] }

    public fun generatePageList(
        text: String,
        font: UnpackedFontMetricsType = dialogueFont,
    ): List<Page> {
        val lineBuffers = Array(MAX_TOTAL_LINE_COUNT) { "" }
        val lineCount = font.splitText(text, MAX_LINE_PIXEL_WIDTH, lineBuffers)
        val pageCount = ceil(lineCount.toDouble() / LINES_PER_PAGE).toInt()
        return when (pageCount) {
            1 -> {
                val page1 = lineBuffers.joinToPageString(0 until lineCount)
                listOf(Page(page1, lineCount))
            }
            2 -> {
                val page1 = lineBuffers.joinToPageString(0 until LINES_PER_PAGE)

                val page2LineCount = lineCount - LINES_PER_PAGE
                val page2 =
                    lineBuffers.joinToPageString(
                        LINES_PER_PAGE until LINES_PER_PAGE + page2LineCount
                    )
                return listOf(Page(page1, LINES_PER_PAGE), Page(page2, page2LineCount))
            }
            // Don't see a point in supporting more than two pages from one single text string.
            // There will be extremely niche situations where one text will be split into two
            // pages, such as when the player display name is long enough to cause the dialogue
            // to need a second page.
            else -> throw NotImplementedError("Page list of size `$pageCount` is not implemented.")
        }
    }

    private fun UnpackedFontMetricsType.splitText(
        text: String,
        widthPerLine: Int,
        lineBuffer: Array<String>,
    ): Int {
        val lineBuilder = StringBuilder(100)
        var lineWidth = 0
        var lineStart = 0
        var lastWordIndex = -1
        var wordWidth = 0
        var extraSpacing: Byte = 0
        var tagStart: Int = -1
        var prevChar: Char = 0.toChar()
        var lineCount = 0
        for (index in text.indices) {
            var char = text[index]

            if (char == '<') {
                tagStart = index
                continue
            }

            if (char == '>' && tagStart != -1) {
                val tag = text.substring(tagStart + 1, index)
                tagStart = -1
                lineBuilder.append('<').append(tag).append('>')
                when (tag) {
                    "br" -> {
                        lineBuffer[lineCount] = lineBuilder.substring(lineStart)
                        lineCount++
                        lineStart = lineBuilder.length
                        lineWidth = 0
                        lastWordIndex = -1
                        prevChar = 0.toChar()
                    }
                    "lt" -> {
                        lineWidth += getAdjustedGlyphAdvance('<')
                        if (kerning.isNotEmpty() && prevChar != 0.toChar()) {
                            lineWidth += kerning[(prevChar.code shl 8) + 60]
                        }
                        prevChar = '<'
                    }
                    "gt" -> {
                        lineWidth += getAdjustedGlyphAdvance('>')
                        if (kerning.isNotEmpty() && prevChar != 0.toChar()) {
                            lineWidth += kerning[(prevChar.code shl 8) + 62]
                        }
                        prevChar = '>'
                    }
                    else ->
                        if (tag.startsWith("img=")) {
                            // TODO: load width from mod_icons
                            //  val imgId = tag.substring(4).toInt()
                            //  glyphs[imgId].getWidth()
                            lineWidth += 13
                            prevChar = 0.toChar()
                        }
                }
                char = 0.toChar()
            }

            if (tagStart == -1) {
                if (char != 0.toChar()) {
                    lineBuilder.append(char)
                    lineWidth += getAdjustedGlyphAdvance(char)
                    if (kerning.isNotEmpty() && prevChar != 0.toChar()) {
                        lineWidth += kerning[(prevChar.code shl 8) or char.code]
                    }
                    prevChar = char
                }

                if (char == ' ') {
                    lastWordIndex = lineBuilder.length
                    wordWidth = lineWidth
                    extraSpacing = 1
                }

                if (lineWidth > widthPerLine && lastWordIndex >= 0) {
                    lineBuffer[lineCount] =
                        lineBuilder.substring(lineStart, lastWordIndex - extraSpacing)
                    lineCount++
                    lineStart = lastWordIndex
                    lastWordIndex = -1
                    lineWidth -= wordWidth
                    prevChar = 0.toChar()
                }

                if (char == '-') {
                    lastWordIndex = lineBuilder.length
                    wordWidth = lineWidth
                    extraSpacing = 0
                }
            }
        }
        val line = lineBuilder.toString()
        if (line.length > lineStart) {
            lineBuffer[lineCount++] = line.substring(lineStart)
        }
        return lineCount
    }

    private fun UnpackedFontMetricsType.getAdjustedGlyphAdvance(char: Char): Int {
        val adjustedChar = if (char == 160.toChar()) ' ' else char
        return glyphAdvances[adjustedChar.code and 0xFF]
    }

    private fun Array<String>.joinToPageString(indexRangeExclusive: IntRange): String =
        indexRangeExclusive.joinToString(separator = "") { index ->
            this[index] + if (index < indexRangeExclusive.last) LINE_SEPARATOR else ""
        }

    public fun lineHeight(lineCount: Int): Int =
        when (lineCount) {
            2 -> TWO_LINE_LINE_HEIGHT
            3 -> THREE_LINE_LINE_HEIGHT
            else -> DEFAULT_LINE_HEIGHT
        }

    public data class Page(val text: String, val lineCount: Int)

    private companion object {
        private const val LINES_PER_PAGE: Int = 4
        private const val MAX_PAGE_COUNT: Int = 2
        private const val MAX_TOTAL_LINE_COUNT: Int = LINES_PER_PAGE * MAX_PAGE_COUNT

        private const val MAX_LINE_PIXEL_WIDTH: Int = 380

        private const val DEFAULT_LINE_HEIGHT: Int = 16
        private const val TWO_LINE_LINE_HEIGHT: Int = 28
        private const val THREE_LINE_LINE_HEIGHT: Int = 20

        /**
         * Used as a separator for each line in a dialogue page. This _can_ be left as a whitespace
         * as this is handled on the client's end as well. However, we send it for emulation.
         */
        private const val LINE_SEPARATOR: String = "<br>"
    }
}
