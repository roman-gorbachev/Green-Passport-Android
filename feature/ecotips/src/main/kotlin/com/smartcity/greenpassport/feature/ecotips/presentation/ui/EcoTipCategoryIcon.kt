package com.smartcity.greenpassport.feature.ecotips.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.model.EcoTipCategory

fun ecoTipCategoryIcon(category: EcoTipCategory): ImageVector = when (category) {
    EcoTipCategory.ARTICLE -> Icons.AutoMirrored.Filled.Article
    EcoTipCategory.VIDEO -> Icons.Filled.SmartDisplay
    EcoTipCategory.KIDS -> Icons.Filled.ChildCare
}
