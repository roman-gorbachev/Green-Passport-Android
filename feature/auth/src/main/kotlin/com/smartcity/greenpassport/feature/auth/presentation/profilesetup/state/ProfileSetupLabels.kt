package com.smartcity.greenpassport.feature.auth.presentation.profilesetup.state

import androidx.annotation.StringRes
import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.feature.auth.R

@StringRes
fun interestLabelRes(category: TaskCategory): Int = when (category) {
    TaskCategory.RECYCLING -> R.string.recycling
    TaskCategory.CLEANUP -> R.string.cleanups
    TaskCategory.TRANSPORT -> R.string.eco_transport
    TaskCategory.REUSABLE_ITEMS -> R.string.reusable_items
    TaskCategory.LECTURE -> R.string.lectures
}

@StringRes
fun nameErrorMessageRes(error: NameError): Int = when (error) {
    NameError.LENGTH -> R.string.name_from_2_to_30_characters
    NameError.CHARACTERS -> R.string.name_letters_only_msg
    NameError.INAPPROPRIATE -> R.string.text_contains_banned_words
}
