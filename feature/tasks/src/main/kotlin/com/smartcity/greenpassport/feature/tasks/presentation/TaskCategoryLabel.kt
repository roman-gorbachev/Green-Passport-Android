package com.smartcity.greenpassport.feature.tasks.presentation

import com.smartcity.greenpassport.core.model.TaskCategory
import com.smartcity.greenpassport.feature.tasks.R

fun taskCategoryLabelRes(category: TaskCategory): Int = when (category) {
    TaskCategory.RECYCLING -> R.string.tasks_category_recycling
    TaskCategory.CLEANUP -> R.string.tasks_category_cleanup
    TaskCategory.TRANSPORT -> R.string.tasks_category_transport
    TaskCategory.REUSABLE_ITEMS -> R.string.tasks_category_reusable_items
    TaskCategory.LECTURE -> R.string.tasks_category_lecture
}
