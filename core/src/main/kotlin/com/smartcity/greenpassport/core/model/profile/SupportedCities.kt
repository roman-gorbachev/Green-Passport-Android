package com.smartcity.greenpassport.core.model.profile

import com.smartcity.greenpassport.core.model.map.GeoPoint

object SupportedCities {
    const val DEFAULT_CITY = "Минск"

    val defaultCenter = GeoPoint(latitude = 53.9006, longitude = 27.5590)

    val centers = mapOf(
        DEFAULT_CITY to defaultCenter,
        "Брест" to GeoPoint(latitude = 52.0976, longitude = 23.7341),
        "Витебск" to GeoPoint(latitude = 55.1904, longitude = 30.2049),
        "Гомель" to GeoPoint(latitude = 52.4345, longitude = 30.9754),
        "Гродно" to GeoPoint(latitude = 53.6884, longitude = 23.8258),
        "Могилёв" to GeoPoint(latitude = 53.9007, longitude = 30.3314),
        "Бобруйск" to GeoPoint(latitude = 53.1384, longitude = 29.2214),
        "Барановичи" to GeoPoint(latitude = 53.1327, longitude = 26.0139),
        "Борисов" to GeoPoint(latitude = 54.2279, longitude = 28.5050),
        "Пинск" to GeoPoint(latitude = 52.1229, longitude = 26.0951),
        "Орша" to GeoPoint(latitude = 54.5081, longitude = 30.4172),
        "Мозырь" to GeoPoint(latitude = 52.0495, longitude = 29.2456),
    )

    val all = centers.keys.toList()
}
