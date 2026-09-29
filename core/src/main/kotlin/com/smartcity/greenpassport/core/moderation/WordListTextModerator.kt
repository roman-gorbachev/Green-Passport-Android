package com.smartcity.greenpassport.core.moderation

import android.content.Context
import androidx.annotation.RawRes
import com.smartcity.greenpassport.core.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListTextModerator @Inject constructor(
    @ApplicationContext private val context: Context,
) : TextModerator {

    private val bannedRoots by lazy { readLines(R.raw.banned_roots) }
    private val allowedStems by lazy { readLines(R.raw.allowed_words) }

    override fun isAllowed(text: String): Boolean {
        val lowercase = text.lowercase().replace('ё', 'е')
        val candidates = words(toCyrillic(lowercase)) + words(toLatin(lowercase))
        return candidates.none(::isBanned)
    }

    private fun isBanned(word: String): Boolean =
        allowedStems.none(word::contains) && bannedRoots.any(word::contains)

    private fun words(text: String): List<String> {
        val tokens = text.split(WORD_SEPARATOR).filter { it.isNotEmpty() }
        val merged = mutableListOf<String>()
        val singleLetters = StringBuilder()
        tokens.forEach { token ->
            if (token.length == 1) {
                singleLetters.append(token)
            } else {
                if (singleLetters.isNotEmpty()) merged += singleLetters.toString()
                singleLetters.clear()
                merged += token
            }
        }
        if (singleLetters.isNotEmpty()) merged += singleLetters.toString()
        return merged.map { it.replace(REPEATED_LETTERS, "$1") }
    }

    private fun toCyrillic(text: String): String =
        text.map { char -> CYRILLIC_LOOKALIKES[char] ?: char }.joinToString(separator = "")

    private fun toLatin(text: String): String =
        text.map { char -> LATIN_LOOKALIKES[char] ?: char }.joinToString(separator = "")

    private fun readLines(@RawRes resId: Int): List<String> =
        context.resources.openRawResource(resId).bufferedReader().useLines { lines ->
            lines.map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toList()
        }

    companion object {
        private val WORD_SEPARATOR = Regex("[^a-zа-я]+")
        private val REPEATED_LETTERS = Regex("(.)\\1+")
        private val CYRILLIC_LOOKALIKES = mapOf(
            'a' to 'а', 'e' to 'е', 'o' to 'о', 'p' to 'р', 'c' to 'с', 'x' to 'х', 'y' to 'у',
            'k' to 'к', 'm' to 'м', 'h' to 'н', 'b' to 'в', 't' to 'т', 'u' to 'и', 'n' to 'п',
            '0' to 'о', '3' to 'з', '6' to 'б', '@' to 'а',
        )
        private val LATIN_LOOKALIKES = mapOf(
            'а' to 'a', 'е' to 'e', 'о' to 'o', 'р' to 'p', 'с' to 'c', 'х' to 'x', 'у' to 'y',
            'к' to 'k', 'м' to 'm', 'т' to 't', '0' to 'o', '1' to 'i', '3' to 'e', '4' to 'a',
            '@' to 'a', '$' to 's',
        )
    }
}
