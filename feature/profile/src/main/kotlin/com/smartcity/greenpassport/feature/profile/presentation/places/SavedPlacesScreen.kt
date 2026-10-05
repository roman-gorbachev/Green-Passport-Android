package com.smartcity.greenpassport.feature.profile.presentation.places

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.common.resolveForDeviceLanguage
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.ErrorContent
import com.smartcity.greenpassport.core.designsystem.component.ListRowContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.component.SymbolTile
import com.smartcity.greenpassport.core.designsystem.component.listSectionItems
import com.smartcity.greenpassport.core.designsystem.layout.plus
import com.smartcity.greenpassport.core.designsystem.text.localized
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.feature.profile.R
import com.smartcity.greenpassport.core.R as CoreR

@Composable
fun SavedPlacesScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: SavedPlacesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier.padding(contentPadding))
        uiState.hasError -> ErrorContent(
            message = stringResource(CoreR.string.error_generic_message),
            retryLabel = stringResource(CoreR.string.retry_button),
            onRetry = viewModel::retry,
            modifier = modifier.padding(contentPadding),
        )
        uiState.places.isEmpty() -> EmptyContent(
            message = stringResource(R.string.saved_places_empty),
            modifier = modifier.padding(contentPadding),
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding + PaddingValues(
                horizontal = Dimens.ScreenHorizontalPadding,
                vertical = Dimens.SpacingMedium,
            ),
        ) {
            listSectionItems(uiState.places, key = { it.id }) { point ->
                ListRowContent(
                    title = point.name.localized(),
                    subtitle = stringResource(mapPointTypeLabelRes(point.type)),
                    leading = { SymbolTile(icon = mapPointTypeIcon(point.type)) },
                    trailing = {
                        Row {
                            IconButton(onClick = { openRoute(context, point) }) {
                                Icon(
                                    imageVector = Icons.Filled.Directions,
                                    contentDescription = stringResource(R.string.build_route)
                                )
                            }
                            IconButton(onClick = { viewModel.onToggleSaved(point) }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = stringResource(R.string.saved),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

private fun openRoute(context: Context, point: MapPoint) {
    val coordinates = "${point.latitude},${point.longitude}"
    val uri = Uri.parse("geo:$coordinates?q=$coordinates(${Uri.encode(point.name.resolveForDeviceLanguage())})")
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
}
