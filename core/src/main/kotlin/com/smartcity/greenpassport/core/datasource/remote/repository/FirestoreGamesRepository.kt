package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.model.games.Game
import com.smartcity.greenpassport.core.model.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIELD_TITLES = "titles"
private const val FIELD_PATH = "path"
private const val FIELD_MATERIAL_ICON = "materialIcon"
private const val FIELD_ICON_PATH = "iconPath"
private const val FIELD_ICON_EMOJI = "iconEmoji"
private const val FIELD_ICON_COLORS = "iconColors"
private const val FIELD_MAX_POINTS = "maxPoints"
private const val FIELD_ORDER = "order"
private const val FIELD_IS_ACTIVE = "isActive"
private const val DEFAULT_MAX_POINTS = 30

class FirestoreGamesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : GamesRepository {

    override fun observeGames(): Flow<List<Game>> =
        FirestoreCollections.games(firestore)
            .whereEqualTo(FIELD_IS_ACTIVE, true)
            .cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toGame() }.sortedBy { it.order } }
            .distinctUntilChanged()
}

private fun DocumentSnapshot.toGame(): Game? {
    val path = getString(FIELD_PATH) ?: return null
    val titles = (get(FIELD_TITLES) as? Map<*, *>)
        ?.mapNotNull { (key, value) ->
            val language = key as? String
            val title = value as? String
            if (language != null && title != null) language to title else null
        }
        ?.toMap()
        .orEmpty()
    return Game(
        id = id,
        titles = titles,
        path = path,
        materialIcon = getString(FIELD_MATERIAL_ICON),
        iconPath = getString(FIELD_ICON_PATH),
        iconEmoji = getString(FIELD_ICON_EMOJI),
        iconColors = (get(FIELD_ICON_COLORS) as? List<*>)?.filterIsInstance<String>().orEmpty(),
        maxPoints = getLong(FIELD_MAX_POINTS)?.toInt() ?: DEFAULT_MAX_POINTS,
        order = getLong(FIELD_ORDER)?.toInt() ?: Int.MAX_VALUE,
    )
}
