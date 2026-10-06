package com.smartcity.greenpassport.feature.community.domain

sealed interface MessageChange {
    data class Edit(val text: String) : MessageChange

    data object Delete : MessageChange
}
