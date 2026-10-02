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
    data object EditProfile : Destination

    @Serializable
    data object Achievements : Destination

    @Serializable
    data object History : Destination

    @Serializable
    data object Notifications : Destination

    @Serializable
    data object Favorites : Destination

    @Serializable
    data object Calendar : Destination

    @Serializable
    data class EventDetail(val eventId: String) : Destination

    @Serializable
    data object Map : Destination

    @Serializable
    data object Community : Destination

    @Serializable
    data object Forum : Destination

    @Serializable
    data object CommunityGroups : Destination

    @Serializable
    data class CommunityGroup(val groupId: String) : Destination

    @Serializable
    data object EcoTips : Destination

    @Serializable
    data class EcoTipDetail(val tipId: String) : Destination

    @Serializable
    data object Games : Destination

    @Serializable
    data class GameWeb(val gameId: String) : Destination

    @Serializable
    data object Shop : Destination

    @Serializable
    data object Coupons : Destination

    @Serializable
    data class CouponDetail(val couponId: String) : Destination

    @Serializable
    data object Feedback : Destination

    @Serializable
    data object Moderation : Destination
}
