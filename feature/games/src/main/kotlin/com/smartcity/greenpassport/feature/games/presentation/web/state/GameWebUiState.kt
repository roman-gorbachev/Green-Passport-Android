package com.smartcity.greenpassport.feature.games.presentation.web.state

import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.rewards.RewardResult

data class GameWebUiState(
    val game: Game? = null,
    val isGameMissing: Boolean = false,
    val lastReward: RewardResult? = null,
    val rewardFailure: RewardFailure? = null,
    val rewardCount: Int = 0,
)
