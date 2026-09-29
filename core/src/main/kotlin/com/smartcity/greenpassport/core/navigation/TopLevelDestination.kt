package com.smartcity.greenpassport.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.R

enum class TopLevelDestination(
    val destination: Destination,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    HOME(Destination.Home, Icons.Filled.Home, R.string.home),
    SHOP(Destination.Shop, Icons.Filled.ShoppingCart, R.string.shop),
    MAP(Destination.Map, Icons.Filled.Map, R.string.map),
    FAVORITES(Destination.Favorites, Icons.Filled.Favorite, R.string.favorites),
}
