package com.smartcity.greenpassport.core.designsystem.component

data class SearchBarContent(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val placeholder: String,
)
