package com.smartcity.greenpassport.navigation

import androidx.annotation.StringRes
import com.smartcity.greenpassport.R
import com.smartcity.greenpassport.core.navigation.Destination

@StringRes
fun destinationTitleRes(destination: Destination): Int = when (destination) {
    Destination.Tasks -> R.string.home_tile_tasks
    Destination.Profile -> R.string.home_tile_profile
    Destination.Calendar -> R.string.home_tile_calendar
    Destination.Map -> R.string.home_tile_map
    Destination.Community -> R.string.home_tile_community
    Destination.EcoTips -> R.string.home_tile_ecotips
    Destination.Games -> R.string.home_tile_games
    Destination.Shop -> R.string.home_tile_shop
    Destination.Feedback -> R.string.home_tile_feedback
    else -> error("No title for destination: $destination")
}
