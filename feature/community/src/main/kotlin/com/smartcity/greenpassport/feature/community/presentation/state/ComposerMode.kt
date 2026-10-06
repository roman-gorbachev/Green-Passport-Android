package com.smartcity.greenpassport.feature.community.presentation.state

import com.smartcity.greenpassport.core.model.community.MessageQuote

sealed interface ComposerMode {
    data object New : ComposerMode

    data class Reply(val quote: MessageQuote) : ComposerMode

    data class Edit(val messageId: String) : ComposerMode
}
