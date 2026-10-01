package com.smartcity.greenpassport.feature.map.domain

import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.model.map.LocationRepository
import com.smartcity.greenpassport.core.model.map.MapFocus
import com.smartcity.greenpassport.core.model.profile.SupportedCities
import com.smartcity.greenpassport.core.model.profile.UserProfileRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class ResolveMapFocusUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(): MapFocus {
        locationRepository.currentLocation()?.let { location -> return MapFocus.UserLocation(location) }
        val city = profileCity()
        return MapFocus.City(SupportedCities.centers[city] ?: SupportedCities.defaultCenter)
    }

    private suspend fun profileCity(): String? = withTimeoutOrNull(PROFILE_TIMEOUT_MILLIS) {
        val userId = authRepository.session.first()?.userId ?: return@withTimeoutOrNull null
        userProfileRepository.observeProfile(userId).catch { emit(null) }.first()?.city
    }

    companion object {
        private const val PROFILE_TIMEOUT_MILLIS = 5_000L
    }
}
