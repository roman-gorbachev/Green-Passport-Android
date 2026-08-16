package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.model.FeedbackEntry
import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.SurveyQuestion
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

private const val SURVEY_QUERY_LIMIT = 1L

private const val FIELD_USER_ID = "userId"
private const val FIELD_TYPE = "type"
private const val FIELD_MESSAGE = "message"
private const val FIELD_RATING = "rating"
private const val FIELD_CREATED_AT = "createdAtEpochMillis"

private const val FIELD_QUESTION = "question"
private const val FIELD_OPTIONS = "options"
private const val FIELD_IS_ACTIVE = "isActive"

private const val FIELD_SURVEY_ID = "surveyId"
private const val FIELD_OPTION_INDEX = "optionIndex"

class FirestoreFeedbackRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FeedbackRepository {

    override suspend fun submitFeedback(entry: FeedbackEntry) {
        val data = mapOf(
            FIELD_USER_ID to entry.userId,
            FIELD_TYPE to entry.type.name,
            FIELD_MESSAGE to entry.message,
            FIELD_RATING to entry.rating,
            FIELD_CREATED_AT to System.currentTimeMillis(),
        )
        FirestoreCollections.feedback(firestore).add(data).await()
    }

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

    override suspend fun submitSurveyAnswer(userId: String, surveyId: String, optionIndex: Int) {
        val answerId = "${userId}_$surveyId"
        FirestoreCollections.surveyAnswers(firestore).document(answerId)
            .set(
                mapOf(
                    FIELD_USER_ID to userId,
                    FIELD_SURVEY_ID to surveyId,
                    FIELD_OPTION_INDEX to optionIndex,
                ),
            )
            .await()
    }
}

private fun DocumentSnapshot.toSurveyQuestion(): SurveyQuestion? {
    val question = getString(FIELD_QUESTION) ?: return null
    @Suppress("UNCHECKED_CAST")
    val options = get(FIELD_OPTIONS) as? List<String> ?: return null
    return SurveyQuestion(id = id, question = question, options = options)
}
