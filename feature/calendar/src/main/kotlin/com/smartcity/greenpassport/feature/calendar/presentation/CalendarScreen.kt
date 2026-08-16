package com.smartcity.greenpassport.feature.calendar.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.EcoEvent
import com.smartcity.greenpassport.feature.calendar.R
import java.text.DateFormat
import java.util.Date

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingContent(modifier = modifier)
        uiState.events.isEmpty() -> EmptyContent(
            message = stringResource(R.string.calendar_empty),
            modifier = modifier,
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimens.SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium),
        ) {
            items(uiState.events) { event ->
                EventCard(
                    event = event,
                    isRegistered = uiState.registeredEventIds.contains(event.id),
                    isRegistering = uiState.registeringEventId == event.id,
                    onRegister = { viewModel.onRegister(event) },
                )
            }
        }
    }
}

@Composable
private fun EventCard(
    event: EcoEvent,
    isRegistered: Boolean,
    isRegistering: Boolean,
    onRegister: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(
                text = DateFormat.getDateInstance().format(Date(event.startAtEpochMillis)),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
            Text(
                text = event.location,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Dimens.SpacingSmall),
            )

            Row(modifier = Modifier.padding(top = Dimens.SpacingMedium)) {
                when {
                    isRegistered -> Text(
                        text = stringResource(R.string.calendar_registered_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    isRegistering -> CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeMedium))

                    else -> Button(onClick = onRegister) {
                        Text(stringResource(R.string.calendar_register_button))
                    }
                }
            }
        }
    }
}
