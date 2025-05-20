package org.rsmod.api.player.dialogue.align

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.factory.font.TestFontMetricsTypeListFactory
import org.rsmod.api.testing.factory.q8Full
import org.rsmod.game.type.font.FontMetricsTypeList

class TextAlignmentTest {
    @Test
    fun `generate 1-line page list`() {
        val fontList = fontTypeList()
        val alignment = TextAlignment(fontList)
        val text = """Advanced clues have tiers between easy and master.""".trimDialogue()
        val pageList = alignment.generatePageList(text, TEXT_WIDTH, fontList.q8Full())
        assertEquals(text, pageList[0].text)
        assertEquals(1, pageList[0].lineCount)
        assertEquals(1, pageList.size)
    }

    @Test
    fun `generate 2-line page list`() {
        val fontList = fontTypeList()
        val alignment = TextAlignment(fontList)
        val text =
            """
                A cipher is a string of letters that have been scrambled
                 using the shift cipher method.
            """
                .trimDialogue()
        val expectedText =
            """
                A cipher is a string of letters that have been scrambled<br>
                using the shift cipher method.
            """
                .trimDialogue()
        val pageList = alignment.generatePageList(text, TEXT_WIDTH, fontList.q8Full())
        assertEquals(expectedText, pageList[0].text)
        assertEquals(2, pageList[0].lineCount)
        assertEquals(1, pageList.size)
    }

    @Test
    fun `generate 3-line page list`() {
        val fontList = fontTypeList()
        val alignment = TextAlignment(fontList)
        val text =
            """
                You can gain advanced clues the same way you would
                 get a beginner clue. But to get higher tier scrolls you
                 will need to defeat more powerful foes.
            """
                .trimDialogue()
        val expectedText =
            """
                You can gain advanced clues the same way you would<br>
                get a beginner clue. But to get higher tier scrolls you<br>
                will need to defeat more powerful foes.
            """
                .trimDialogue()
        val pageList = alignment.generatePageList(text, TEXT_WIDTH, fontList.q8Full())
        assertEquals(expectedText, pageList[0].text)
        assertEquals(3, pageList[0].lineCount)
        assertEquals(1, pageList.size)
    }

    @Test
    fun `generate 4-line page list`() {
        val fontList = fontTypeList()
        val alignment = TextAlignment(fontList)
        val text =
            """
                The Lumbridge cook has been having problems, the
                 Duke is confused over some strange talisman and on
                 top of all that, poor lad Romeo in Varrock has girlfriend
                 problems.
            """
                .trimDialogue()
        val expectedText =
            """
                The Lumbridge cook has been having problems, the<br>
                Duke is confused over some strange talisman and on<br>
                top of all that, poor lad Romeo in Varrock has girlfriend<br>
                problems.
            """
                .trimDialogue()
        val pageList = alignment.generatePageList(text, TEXT_WIDTH, fontList.q8Full())
        assertEquals(expectedText, pageList[0].text)
        assertEquals(4, pageList[0].lineCount)
        assertEquals(1, pageList.size)
    }

    @Test
    fun `generate 6-line 2-page list`() {
        val fontList = fontTypeList()
        val alignment = TextAlignment(fontList)
        val text =
            """
                The Lumbridge cook has been having problems, the
                 Duke is confused over some strange talisman and on
                 top of all that, poor lad Romeo in Varrock has girlfriend
                 problems. It's funny really, the cook would forget his head if it
                 wasn't screwed on. This time he forgot to get
                 ingredients for the Duke's birthday cake.
            """
                .trimDialogue()
        val expectedTextPg1 =
            """
                The Lumbridge cook has been having problems, the<br>
                Duke is confused over some strange talisman and on<br>
                top of all that, poor lad Romeo in Varrock has girlfriend<br>
                problems. It's funny really, the cook would forget his
            """
                .trimDialogue()
        val expectedTextPg2 =
            """
                head if it wasn't screwed on. This time he forgot to get<br>
                ingredients for the Duke's birthday cake.
            """
                .trimDialogue()
        val pageList = alignment.generatePageList(text, TEXT_WIDTH, fontList.q8Full())
        assertEquals(expectedTextPg1, pageList[0].text)
        assertEquals(4, pageList[0].lineCount)
        assertEquals(2, pageList.size)
        assertEquals(expectedTextPg2, pageList[1].text)
        assertEquals(2, pageList[1].lineCount)
    }

    private fun fontTypeList(): FontMetricsTypeList =
        TestFontMetricsTypeListFactory().createDefault()

    // Removes implicit newline characters from multi-line string templates.
    private fun String.trimDialogue(): String = trimIndent().replace("\n", "")

    private companion object {
        private const val TEXT_WIDTH = 380
    }
}
