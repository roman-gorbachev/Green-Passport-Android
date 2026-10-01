package com.smartcity.greenpassport.feature.shop.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcity.greenpassport.core.designsystem.component.GpPrimaryButton
import com.smartcity.greenpassport.core.designsystem.component.GpSheetScaffold
import com.smartcity.greenpassport.core.designsystem.theme.Dimens
import com.smartcity.greenpassport.core.model.CouponStatus
import com.smartcity.greenpassport.feature.shop.R
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponDetailUiState
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponItem
import com.smartcity.greenpassport.feature.shop.presentation.state.couponFailureMessageRes
import com.smartcity.greenpassport.feature.shop.presentation.state.couponStatusText
import com.smartcity.greenpassport.feature.shop.presentation.viewmodels.CouponDetailViewModel
import com.smartcity.greenpassport.core.R as CoreR

private const val INACTIVE_ALPHA = 0.4f
private const val CODE_LETTER_SPACING_SP = 4

@Composable
fun CouponDetailSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CouponDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isConfirmationVisible by remember { mutableStateOf(false) }

    if (isConfirmationVisible) {
        AlertDialog(
            onDismissRequest = { isConfirmationVisible = false },
            title = { Text(stringResource(R.string.mark_as_used)) },
            text = { Text(stringResource(R.string.mark_coupon_used_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        isConfirmationVisible = false
                        viewModel.onMarkUsed()
                    },
                ) {
                    Text(stringResource(R.string.mark_as_used))
                }
            },
            dismissButton = {
                TextButton(onClick = { isConfirmationVisible = false }) {
                    Text(stringResource(CoreR.string.cancel))
                }
            },
        )
    }

    GpSheetScaffold(onDismiss = onDismiss, modifier = modifier) {
        val item = uiState.item
        if (item == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpacingExtraLarge),
            ) {
                CircularProgressIndicator()
            }
        } else {
            CouponDetailContent(
                item = item,
                uiState = uiState,
                onMarkUsed = { isConfirmationVisible = true },
            )
        }
    }
}

@Composable
private fun CouponDetailContent(
    item: CouponItem,
    uiState: CouponDetailUiState,
    onMarkUsed: () -> Unit,
) {
    val status = item.coupon.status(uiState.nowEpochMillis)
    val isActive = status == CouponStatus.ACTIVE
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall),
        ) {
            item.reward?.partnerName?.let { partner ->
                Text(
                    text = partner,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(text = item.title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text(
                text = couponStatusText(item, uiState.nowEpochMillis),
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive && !item.isExpiringSoon(uiState.nowEpochMillis)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
        CouponCodeBlock(
            payload = uiState.qrPayload,
            code = item.coupon.code,
            modifier = Modifier.alpha(if (isActive) 1f else INACTIVE_ALPHA),
        )
        uiState.failure?.let { failure ->
            Text(
                text = stringResource(couponFailureMessageRes(failure)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
        if (isActive) {
            GpPrimaryButton(
                text = stringResource(R.string.mark_as_used),
                onClick = onMarkUsed,
                isLoading = uiState.isMarking,
            )
        }
    }
}

@Composable
private fun CouponCodeBlock(
    payload: String?,
    code: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingCompact),
        modifier = modifier,
    ) {
        payload?.let { payload ->
            QrCodeImage(
                payload = payload,
                contentDescription = stringResource(R.string.coupon_code),
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(Dimens.CornerRadiusMedium))
                    .padding(Dimens.SpacingCompact)
                    .size(Dimens.CouponQrSize),
            )
        }
        code?.let { code ->
            Text(
                text = stringResource(R.string.coupon_code),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SelectionContainer {
                Text(
                    text = code,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = CODE_LETTER_SPACING_SP.sp,
                    ),
                )
            }
        }
        Text(
            text = stringResource(R.string.partner_scans_qr_msg),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
