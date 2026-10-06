package com.smartcity.greenpassport.feature.profile.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.ListSection
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.settings.AppIcon
import com.smartcity.greenpassport.feature.profile.R

private const val COLUMN_COUNT = 3

@Composable
fun AppIconSection(
    modifier: Modifier = Modifier,
    viewModel: AppIconViewModel = hiltViewModel(),
) {
    val selected by viewModel.icon.collectAsStateWithLifecycle()
    ListSection(modifier = modifier, header = stringResource(R.string.app_icon)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
        ) {
            AppIcon.entries.chunked(COLUMN_COUNT).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { icon ->
                        AppIconOption(
                            icon = icon,
                            isSelected = icon == selected,
                            onClick = { viewModel.onIconSelected(icon) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.app_icon_change_hint_msg),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AppIconOption(
    icon: AppIcon,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        modifier = modifier.clickable(role = Role.RadioButton, onClick = onClick),
    ) {
        Image(
            painter = painterResource(icon.previewRes),
            contentDescription = stringResource(icon.titleRes),
            modifier = Modifier
                .size(Dimens.AppIconPreviewSize)
                .border(BorderStroke(Dimens.AppIconSelectionWidth, accent), CircleShape)
                .padding(Dimens.AppIconSelectionWidth)
                .clip(CircleShape),
        )
        Text(
            text = stringResource(icon.titleRes),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private val AppIcon.titleRes: Int
    get() = when (this) {
        AppIcon.STANDARD -> R.string.app_icon_standard
        AppIcon.DARK -> R.string.app_icon_dark
        AppIcon.SUNSET -> R.string.app_icon_sunset
        AppIcon.NIGHT -> R.string.app_icon_night
        AppIcon.OCEAN -> R.string.app_icon_ocean
        AppIcon.LIME -> R.string.app_icon_lime
    }

private val AppIcon.previewRes: Int
    get() = when (this) {
        AppIcon.STANDARD -> R.drawable.app_icon_preview_standard
        AppIcon.DARK -> R.drawable.app_icon_preview_dark
        AppIcon.SUNSET -> R.drawable.app_icon_preview_sunset
        AppIcon.NIGHT -> R.drawable.app_icon_preview_night
        AppIcon.OCEAN -> R.drawable.app_icon_preview_ocean
        AppIcon.LIME -> R.drawable.app_icon_preview_lime
    }
