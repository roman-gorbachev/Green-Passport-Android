package com.smartcity.greenpassport.core.model

data class Experience(
    val userId: String,
    val lifetimeXp: Int,
)

data class Level(
    val number: Int,
    val currentXp: Int,
    val xpForNextLevel: Int,
)

object LevelProgression {
    private const val XP_PER_LEVEL = 1000

    fun levelFor(experience: Experience): Level {
        val number = experience.lifetimeXp / XP_PER_LEVEL + 1
        val currentXp = experience.lifetimeXp % XP_PER_LEVEL
        return Level(
            number = number,
            currentXp = currentXp,
            xpForNextLevel = XP_PER_LEVEL,
        )
    }
}
