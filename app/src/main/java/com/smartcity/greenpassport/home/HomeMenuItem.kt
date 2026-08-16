package com.smartcity.greenpassport.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.R
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.core.navigation.homeMenuDestinations

data class HomeMenuItem(
    val destination: Destination,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
)

fun homeMenuLabelRes(destination: Destination): Int = when (destination) {
    Destination.Tasks -> R.string.home_tile_tasks
    Destination.Profile -> R.string.home_tile_profile
    Destination.Calendar -> R.string.home_tile_calendar
    Destination.Map -> R.string.home_tile_map
    Destination.Community -> R.string.home_tile_community
    Destination.EcoTips -> R.string.home_tile_ecotips
    Destination.Games -> R.string.home_tile_games
    Destination.Shop -> R.string.home_tile_shop
    Destination.Feedback -> R.string.home_tile_feedback
    else -> error("Unexpected home menu destination: $destination")
}

private fun homeMenuIcon(destination: Destination): ImageVector = when (destination) {
    Destination.Tasks -> Icons.Filled.Checklist
    Destination.Profile -> Icons.Filled.Person
    Destination.Calendar -> Icons.Filled.CalendarMonth
    Destination.Map -> Icons.Filled.Map
    Destination.Community -> Icons.Filled.Groups
    Destination.EcoTips -> Icons.Filled.Eco
    Destination.Games -> Icons.Filled.SportsEsports
    Destination.Shop -> Icons.Filled.Storefront
    Destination.Feedback -> Icons.Filled.Feedback
    else -> error("Unexpected home menu destination: $destination")
}

val homeMenuItems: List<HomeMenuItem> = homeMenuDestinations.map { destination ->
    HomeMenuItem(destination, homeMenuLabelRes(destination), homeMenuIcon(destination))
}
