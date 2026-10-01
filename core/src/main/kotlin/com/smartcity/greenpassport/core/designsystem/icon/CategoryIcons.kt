package com.smartcity.greenpassport.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.core.model.TaskCategory

fun ecoTipCategoryIcon(category: EcoTipCategory): ImageVector = when (category) {
    EcoTipCategory.ARTICLE -> Icons.AutoMirrored.Filled.Article
    EcoTipCategory.VIDEO -> Icons.Filled.PlayCircle
    EcoTipCategory.KIDS -> Icons.Filled.ChildCare
}

fun taskCategoryIcon(category: TaskCategory): ImageVector = when (category) {
    TaskCategory.RECYCLING -> Icons.Filled.Recycling
    TaskCategory.CLEANUP -> Icons.Filled.Eco
    TaskCategory.TRANSPORT -> Icons.Filled.DirectionsBike
    TaskCategory.REUSABLE_ITEMS -> Icons.Filled.LocalMall
    TaskCategory.LECTURE -> Icons.Filled.MenuBook
}
