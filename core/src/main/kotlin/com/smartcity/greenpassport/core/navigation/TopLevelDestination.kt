package com.smartcity.greenpassport.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.R

enum class TopLevelDestination(
    val destination: Destination,
    val icon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val labelRes: Int,
) {
    HOME(Destination.Home, Icons.Filled.Home, Icons.Outlined.Home, R.string.home),
    SHOP(Destination.Shop, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, R.string.shop),
    MAP(Destination.Map, Icons.Filled.Map, Icons.Outlined.Map, R.string.map),
    FAVORITES(Destination.Favorites, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, R.string.favorites),
}
