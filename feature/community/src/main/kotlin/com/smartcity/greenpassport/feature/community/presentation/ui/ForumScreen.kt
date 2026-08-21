package com.smartcity.greenpassport.feature.community.presentation.ui

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.EmptyContent
import com.smartcity.greenpassport.core.designsystem.component.LoadingContent
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.ForumPost
import com.smartcity.greenpassport.feature.community.R
import com.smartcity.greenpassport.feature.community.presentation.viewmodels.ForumViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ForumScreen(
    modifier: Modifier = Modifier,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LoadingContent(modifier = Modifier.weight(1f))
            uiState.posts.isEmpty() -> EmptyContent(
                message = stringResource(R.string.forum_empty),
                modifier = Modifier.weight(1f),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(Dimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
            ) {
                items(uiState.posts) { post -> ForumPostCard(post) }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = uiState.draft,
                onValueChange = viewModel::onDraftChanged,
                label = { Text(stringResource(R.string.forum_draft_label)) },
                modifier = Modifier.weight(1f),
            )
            if (uiState.isPosting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = Dimens.SpacingSmall)
                        .size(Dimens.IconSizeMedium),
                )
            } else {
                Button(
                    onClick = viewModel::onPost,
                    modifier = Modifier.padding(start = Dimens.SpacingSmall),
                ) {
                    Text(stringResource(R.string.forum_post_button))
                }
            }
        }
    }
}

@Composable
private fun ForumPostCard(post: ForumPost) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
            Text(
                text = DateFormat.getDateTimeInstance().format(Date(post.createdAtEpochMillis)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = Dimens.SpacingExtraSmall),
            )
        }
    }
}
