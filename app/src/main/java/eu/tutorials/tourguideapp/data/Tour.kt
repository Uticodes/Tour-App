package eu.tutorials.tourguideapp.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.google.firebase.Timestamp
import com.google.type.DateTime
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

@Parcelize
data class UserTour(
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