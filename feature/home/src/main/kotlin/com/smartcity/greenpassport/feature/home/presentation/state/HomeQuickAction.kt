package com.smartcity.greenpassport.feature.home.presentation.state

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartcity.greenpassport.core.navigation.Destination
import com.smartcity.greenpassport.feature.home.R

enum class HomeQuickAction(
    val destination: Destination,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    COMMUNITY(Destination.Community, Icons.Filled.Groups, R.string.community),
    GAMES(Destination.Games, Icons.Filled.SportsEsports, R.string.games),
    ECO_TIPS(Destination.EcoTips, Icons.Filled.Eco, R.string.tips),
    CALENDAR(Destination.Calendar, Icons.Filled.CalendarMonth, R.string.calendar),
    FEEDBACK(Destination.Feedback, Icons.Filled.RateReview, R.string.feedback),
}
