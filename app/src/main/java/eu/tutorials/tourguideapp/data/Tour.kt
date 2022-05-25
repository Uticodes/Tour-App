package eu.tutorials.tourguideapp.data

import androidx.annotation.DrawableRes

data class Tour(
    val id: Long,
    val countryName: String,
    val placeName: String,
    val date: String,
    val description: String,
    @DrawableRes
    val placeImage: Int?,
)
