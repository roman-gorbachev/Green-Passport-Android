package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.localizedText
import com.smartcity.greenpassport.core.datasource.remote.localizedTextList
import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.SurveyQuestion
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val SURVEY_QUERY_LIMIT = 1L

private const val FIELD_QUESTION = "question"
private const val FIELD_OPTIONS = "options"
private const val FIELD_QUESTIONS = "questions"
private const val FIELD_OPTION_LISTS = "optionLists"
private const val FIELD_IS_ACTIVE = "isActive"

class FirestoreFeedbackRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FeedbackRepository {

    override suspend fun getActiveSurvey(): SurveyQuestion? {
        val snapshot = FirestoreCollections.surveys(firestore)
            .whereEqualTo(FIELD_IS_ACTIVE, true)
            .limit(SURVEY_QUERY_LIMIT)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toSurveyQuestion()
    }

    override suspend fun hasAnsweredSurvey(userId: String, surveyId: String): Boolean {
        val answerId = "${userId}_$surveyId"
        return FirestoreCollections.surveyAnswers(firestore).document(answerId).get().await().exists()
    }
}

private fun DocumentSnapshot.toSurveyQuestion(): SurveyQuestion? {
    val question = localizedText(FIELD_QUESTION, FIELD_QUESTIONS) ?: return null
    val options = localizedTextList(FIELD_OPTIONS, FIELD_OPTION_LISTS)
        ?.takeIf { it.fallback.isNotEmpty() }
        ?: return null
    return SurveyQuestion(id = id, question = question, options = options)
}
