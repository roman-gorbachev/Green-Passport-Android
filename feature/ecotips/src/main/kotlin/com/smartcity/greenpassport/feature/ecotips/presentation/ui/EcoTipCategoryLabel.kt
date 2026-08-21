package com.smartcity.greenpassport.feature.ecotips.presentation.ui

import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.R

fun ecoTipCategoryLabelRes(category: EcoTipCategory): Int = when (category) {
    EcoTipCategory.ARTICLE -> R.string.ecotips_category_article
    EcoTipCategory.VIDEO -> R.string.ecotips_category_video
    EcoTipCategory.KIDS -> R.string.ecotips_category_kids
}
