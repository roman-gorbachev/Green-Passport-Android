package com.smartcity.greenpassport.core.common

import kotlin.math.ceil
import kotlin.math.max

private const val HEADING_PREFIX = "## "
private const val BULLET_PREFIX = "- "
private const val BOLD_MARKER = "**"
private const val WORDS_PER_MINUTE = 180
private const val MINIMUM_READ_MINUTES = 1
private val whitespace = Regex("\\s+")

sealed interface ArticleBlock {
    val text: String

    data class Heading(override val text: String) : ArticleBlock

    data class Paragraph(override val text: String) : ArticleBlock

    data class Bullet(override val text: String) : ArticleBlock

    companion object {
        fun parse(markdown: String): List<ArticleBlock> =
            markdown.lines()
                .map(String::trim)
                .filter(String::isNotEmpty)
                .map { line ->
                    when {
                        line.startsWith(HEADING_PREFIX) -> Heading(line.removePrefix(HEADING_PREFIX))
                        line.startsWith(BULLET_PREFIX) -> Bullet(line.removePrefix(BULLET_PREFIX))
                        else -> Paragraph(line)
                    }
                }
    }
}

val ArticleBlock.plainText: String
    get() = text.replace(BOLD_MARKER, "")

fun articleReadMinutes(markdown: String): Int {
    val words = markdown.split(whitespace).count(String::isNotBlank)
    return max(MINIMUM_READ_MINUTES, ceil(words / WORDS_PER_MINUTE.toDouble()).toInt())
}

fun articlePreview(markdown: String): String =
    ArticleBlock.parse(markdown).firstOrNull { it is ArticleBlock.Paragraph }?.plainText.orEmpty()
