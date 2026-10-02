package com.smartcity.greenpassport.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.smartcity.greenpassport.core.common.ArticleBlock
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.designsystem.theme.GreenPassportTheme

private const val BOLD_MARKER = "**"
private const val BULLET = "•"

@Composable
fun ArticleText(markdown: String, modifier: Modifier = Modifier) {
    val blocks = remember(markdown) { ArticleBlock.parse(markdown) }
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
        modifier = modifier.fillMaxWidth(),
    ) {
        blocks.forEach { block ->
            when (block) {
                is ArticleBlock.Heading -> Text(
                    text = inlineText(block.text),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = Dimens.SpacingSmall),
                )
                is ArticleBlock.Paragraph -> Text(
                    text = inlineText(block.text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                is ArticleBlock.Bullet -> Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)) {
                    Text(
                        text = BULLET,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = inlineText(block.text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

private fun inlineText(text: String): AnnotatedString = buildAnnotatedString {
    text.split(BOLD_MARKER).forEachIndexed { index, part ->
        if (index % 2 == 1) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(part) }
        } else {
            append(part)
        }
    }
}

@Preview
@Composable
private fun ArticleTextPreview() {
    GreenPassportTheme {
        ArticleText(markdown = "Первый абзац с **жирным** текстом.\n\n## Подзаголовок\n\n- Пункт один\n- Пункт два")
    }
}
