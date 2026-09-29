package com.smartcity.greenpassport.feature.auth.domain

import com.smartcity.greenpassport.core.moderation.TextModerator
import javax.inject.Inject

class IsTextAllowedUseCase @Inject constructor(
    private val textModerator: TextModerator,
) {
    operator fun invoke(text: String): Boolean = textModerator.isAllowed(text)
}
