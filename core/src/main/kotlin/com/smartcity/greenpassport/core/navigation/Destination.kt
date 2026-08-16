package com.smartcity.greenpassport.core.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Auth : Destination

    @Serializable
    data object Home : Destination

    @Serializable
    data object Tasks : Destination

    @Serializable
    data class TaskDetail(val taskId: String) : Destination

    @Serializable
    data object Profile : Destination

    @Serializable
    data object Calendar : Destination

    @Serializable
    data object Map : Destination

    @Serializable
    data object Community : Destination

    @Serializable
    data object Forum : Destination

    @Serializable
    data object CommunityGroups : Destination

    @Serializable
    data object EcoTips : Destination

    @Serializable
    data object Games : Destination

    @Serializable
    data object EcoPuzzleGame : Destination

    @Serializable
    data object WasteSortingGame : Destination

    @Serializable
    data object EcoMazeGame : Destination

    @Serializable
    data object EcoQuizGame : Destination

    @Serializable
    data object Shop : Destination

    @Serializable
    data object Feedback : Destination
}

val homeMenuDestinations: List<Destination> = listOf(
    Destination.Tasks,
    Destination.Profile,
    Destination.Calendar,
    Destination.Map,
    Destination.Community,
    Destination.EcoTips,
    Destination.Games,
    Destination.Shop,
    Destination.Feedback,
)
