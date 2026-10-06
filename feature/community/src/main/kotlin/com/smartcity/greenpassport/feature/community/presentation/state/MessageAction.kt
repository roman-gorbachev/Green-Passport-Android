package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.moderation.ReportReason

sealed interface MessageAction {
    data object Reply : MessageAction

    data object Copy : MessageAction

    data object Forward : MessageAction

    data object Edit : MessageAction

    data object Delete : MessageAction

    data class Report(val reason: ReportReason) : MessageAction
}
