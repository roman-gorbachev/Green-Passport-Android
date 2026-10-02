package com.smartcity.greenpassport.feature.games.presentation.hub.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.vector.ImageVector

private val gameIcons = mapOf(
    "DirectionsRun" to Icons.AutoMirrored.Filled.DirectionsRun,
    "Inventory" to Icons.Filled.Inventory,
    "Waves" to Icons.Filled.Waves,
    "Forest" to Icons.Filled.Forest,
    "GridView" to Icons.Filled.GridView,
    "Psychology" to Icons.Filled.Psychology,
    "Lightbulb" to Icons.Filled.Lightbulb,
    "EmojiNature" to Icons.Filled.EmojiNature,
    "PedalBike" to Icons.Filled.PedalBike,
    "Style" to Icons.Filled.Style,
)

fun gameIcon(name: String?): ImageVector = gameIcons[name] ?: Icons.Filled.SportsEsports
