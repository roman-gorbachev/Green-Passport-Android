package com.smartcity.greenpassport.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.R
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.core.navigation.homeMenuDestinations

sealed interface HomeMenuIcon {
    data class Artwork(@DrawableRes val drawableRes: Int) : HomeMenuIcon
    data class Symbol(val imageVector: ImageVector) : HomeMenuIcon
}

data class HomeMenuItem(
    val destination: Destination,
    @StringRes val labelRes: Int,
    val icon: HomeMenuIcon,
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

private fun homeMenuIcon(destination: Destination): HomeMenuIcon = when (destination) {
    Destination.Tasks -> HomeMenuIcon.Artwork(R.drawable.ic_home_tasks)
    Destination.Calendar -> HomeMenuIcon.Artwork(R.drawable.ic_home_calendar)
    Destination.Map -> HomeMenuIcon.Artwork(R.drawable.ic_home_map)
    Destination.Community -> HomeMenuIcon.Artwork(R.drawable.ic_home_community)
    Destination.EcoTips -> HomeMenuIcon.Artwork(R.drawable.ic_home_ecotips)
    Destination.Games -> HomeMenuIcon.Artwork(R.drawable.ic_home_games)
    Destination.Shop -> HomeMenuIcon.Artwork(R.drawable.ic_home_shop)
    Destination.Feedback -> HomeMenuIcon.Artwork(R.drawable.ic_home_feedback)
    Destination.Profile -> HomeMenuIcon.Symbol(Icons.Filled.Person)
    else -> error("Unexpected home menu destination: $destination")
}

val homeMenuItems: List<HomeMenuItem> = homeMenuDestinations.map { destination ->
    HomeMenuItem(destination, homeMenuLabelRes(destination), homeMenuIcon(destination))
}
