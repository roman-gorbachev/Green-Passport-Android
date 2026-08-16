package com.smartcity.greenpassport.feature.ecotips.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.PillListItem
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoTip
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.R

@Composable
fun EcoTipsListScreen(
    onTipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EcoTipsListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }

    EcoTipsListContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onTipSelected = onTipSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EcoTipsListContent(
    uiState: EcoTipsListUiState,
    onCategorySelected: (EcoTipCategory?) -> Unit,
    onTipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val dailyTip = uiState.dailyTip
        if (dailyTip != null) {
            DailyTipCard(tip = dailyTip, onClick = { onTipSelected(dailyTip.id) })
        }

        LazyRow(
            contentPadding = PaddingValues(Dimens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text(stringResource(R.string.ecotips_filter_all)) },
                )
            }
            items(EcoTipCategory.entries) { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(stringResource(ecoTipCategoryLabelRes(category))) },
                )
            }
        }

        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.fillMaxSize())
            uiState.visibleTips.isEmpty() -> EmptyContent(
                message = stringResource(R.string.ecotips_empty),
                modifier = Modifier.fillMaxSize(),
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(Dimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            ) {
                items(uiState.visibleTips) { tip ->
                    PillListItem(
                        title = tip.title,
                        leadingIcon = if (uiState.readTipIds.contains(tip.id)) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Filled.RadioButtonUnchecked
                        },
                        onClick = { onTipSelected(tip.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyTipCard(
    tip: EcoTip,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SpacingMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(
                text = stringResource(R.string.ecotips_daily_tip_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
        }
    }
}
