package eu.tutorials.tourguideapp.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Tour(
    var id: String? = null,
    var placeName: String? = null,
    var date: Date? = null,
    var description: String? = null,
    var authorsName: String? = null,
    var placeImage: String? = null,
) : Parcelable

data class TourImage(
    var imageDownloadUrl: Uri? = null
)