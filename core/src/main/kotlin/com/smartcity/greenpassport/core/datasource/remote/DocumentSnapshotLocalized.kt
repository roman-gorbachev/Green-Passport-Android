package com.smartcity.greenpassport.core.datasource.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.smartcity.greenpassport.core.model.LocalizedText
import com.smartcity.greenpassport.core.model.LocalizedTextList

fun DocumentSnapshot.localizedText(field: String, translationsField: String): LocalizedText? {
    val fallback = getString(field) ?: return null
    val translations = (get(translationsField) as? Map<*, *>)
        ?.mapNotNull { (key, value) ->
            val language = key as? String
            val text = value as? String
            if (language != null && text != null) language to text else null
        }
        ?.toMap()
        .orEmpty()
    return LocalizedText(fallback, translations)
}

fun DocumentSnapshot.localizedTextList(field: String, translationsField: String): LocalizedTextList? {
    val fallback = (get(field) as? List<*>)?.filterIsInstance<String>() ?: return null
    val translations = (get(translationsField) as? Map<*, *>)
        ?.mapNotNull { (key, value) ->
            val language = key as? String
            val texts = (value as? List<*>)?.filterIsInstance<String>()
            if (language != null && texts != null) language to texts else null
        }
        ?.toMap()
        .orEmpty()
    return LocalizedTextList(fallback, translations)
}
