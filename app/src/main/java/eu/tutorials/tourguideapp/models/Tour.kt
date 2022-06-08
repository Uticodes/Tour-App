package eu.tutorials.tourguideapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Tour(
    var id: String = "",
    var placeName: String = "",
    var date: Date = Date(),
    var description: String = "",
    var authorsName: String = "",
    var email: String = "",
    var placeImage: String = "",
) : Parcelable {

    fun toMap() =
        mapOf(
            "id" to id,
            "placeName" to placeName,
            "date" to date,
            "description" to description,
            "authorsName" to authorsName,
            "email" to email,
            "placeImage" to placeImage
        )
}