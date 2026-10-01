package com.smartcity.greenpassport.feature.feedback.domain

import com.smartcity.greenpassport.core.model.rewards.RewardResult
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import javax.inject.Inject

class SubmitSurveyAnswerUseCase @Inject constructor(
    private val rewardsRepository: RewardsRepository,
) {
    suspend operator fun invoke(surveyId: String, optionIndex: Int): RewardResult =
        rewardsRepository.submitSurveyAnswer(surveyId, optionIndex)
}
