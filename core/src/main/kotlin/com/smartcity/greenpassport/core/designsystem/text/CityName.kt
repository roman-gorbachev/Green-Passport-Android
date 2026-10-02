package com.smartcity.greenpassport.core.designsystem.text

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.smartcity.greenpassport.core.R

private val cityNames = mapOf(
    "Минск" to R.string.city_minsk,
    "Брест" to R.string.city_brest,
    "Витебск" to R.string.city_vitebsk,
    "Гомель" to R.string.city_gomel,
    "Гродно" to R.string.city_grodno,
    "Могилёв" to R.string.city_mogilev,
    "Бобруйск" to R.string.city_bobruisk,
    "Барановичи" to R.string.city_baranovichi,
    "Борисов" to R.string.city_borisov,
    "Пинск" to R.string.city_pinsk,
    "Орша" to R.string.city_orsha,
    "Мозырь" to R.string.city_mozyr,
)

@StringRes
fun cityNameRes(city: String): Int? = cityNames[city]

@Composable
fun cityName(city: String): String = cityNameRes(city)?.let { stringResource(it) } ?: city
