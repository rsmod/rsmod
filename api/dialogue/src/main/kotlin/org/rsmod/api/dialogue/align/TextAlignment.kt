package org.rsmod.api.dialogue.align

import jakarta.inject.Inject
import org.rsmod.api.config.refs.fontmetrics
import org.rsmod.game.type.font.FontMetricsTypeList
import org.rsmod.game.type.font.UnpackedFontMetricsType

public class TextAlignment @Inject constructor(fonts: FontMetricsTypeList) {
    private val dialogueFont = fonts[fontmetrics.q8_full]

    public fun lineHeight(lineCount: Int): Int =
        when (lineCount) {
            2 -> 28
            3 -> 20
            else -> 16
        }

    public fun computeLineCount(text: String, font: UnpackedFontMetricsType = dialogueFont): Int =
        font.computeLineCount(text, widthPerLine = 380)

    private fun UnpackedFontMetricsType.computeLineCount(text: String, widthPerLine: Int): Int {
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
                        lineCount++
                        lineStart = lineBuilder.length
                        lineWidth = 0
                        lastWordIndex = -1
                        prevChar = 0.toChar()
                    }
                    "lt" -> {
                        lineWidth += getCharWidth('<')
                        if (kerning.isNotEmpty() && prevChar != 0.toChar()) {
                            lineWidth += kerning[(prevChar.code shl 8) + 60]
                        }
                        prevChar = '<'
                    }
                    "gt" -> {
                        lineWidth += getCharWidth('>')
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
                    lineWidth += getCharWidth(char)
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
            lineCount++
        }
        return lineCount
    }

    private fun UnpackedFontMetricsType.getCharWidth(char: Char): Int {
        val adjustedChar = if (char == 160.toChar()) ' ' else char
        return charWidths[adjustedChar.code and 0xFF]
    }
}
