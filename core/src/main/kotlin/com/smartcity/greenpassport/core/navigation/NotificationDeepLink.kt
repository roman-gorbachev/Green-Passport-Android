package com.smartcity.greenpassport.core.navigation

enum class NotificationDeepLink(val destination: Destination) {
    TASK_REMINDER(Destination.Tasks),
    EVENT_REMINDER(Destination.Calendar),
    COMMUNITY_ALERT(Destination.Community),
    REWARD_GRANTED(Destination.Profile),
    COUPON_EXPIRING(Destination.Shop),
}
