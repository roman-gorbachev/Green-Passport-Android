package com.smartcity.greenpassport.feature.games.presentation.hub.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

private val gameIcons = mapOf(
    "Abc" to Icons.Filled.Abc,
    "DeleteSweep" to Icons.Filled.DeleteSweep,
    "Explore" to Icons.Filled.Explore,
    "Extension" to Icons.Filled.Extension,
    "FactCheck" to Icons.Filled.FactCheck,
    "MoveDown" to Icons.Filled.MoveDown,
    "Quiz" to Icons.Filled.Quiz,
    "WaterDrop" to Icons.Filled.WaterDrop,
)

fun gameIcon(name: String?): ImageVector = gameIcons[name] ?: Icons.Filled.SportsEsports
