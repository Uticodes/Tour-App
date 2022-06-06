package eu.tutorials

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.User
import java.io.ByteArrayOutputStream
import java.lang.String.format
import java.text.DateFormat
import java.util.*

object Constants {
    private const val TAG = "Constants"
    //App package name
    private const val PACKAGE_NAME = "eu.tutorials.tourguideapp"

    private lateinit var observer: Observer<User>

    var sessionUser: User? = null
        private set

    //Collections
    const val COLLECTION_USERS = "Users"
    const val COLLECTION_TOURS = "Tours"
    const val COLLECTION_USERS_ADD_TOURS = "Users/Tours"

    //Intent
    const val TOUR_KEY = "${PACKAGE_NAME}.TOUR_KEY"
    const val PREF_KEY = "${PACKAGE_NAME}.PREF_KEY"
    const val IMAGE_URL_KEY = "image_download_url"
    const val DOCUMENT_ID_KEY = "document_id"

    //Request codes
    const val EXTERNAL_STORAGE_REQUEST_CODE = 100
    const val URI_REQUEST_CODE = 101


    fun Fragment.toBitmap(imageUri: String){
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.parse(imageUri))
    }

    fun Fragment.spannableString(context: Context, textView: TextView){
        val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

        // Setting underline style from position 0 till length of the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable string in TextView
        textView.text = mSpannableString
    }

    fun Activity.spannableString(context: Context, textView: TextView){
        val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

        // Setting underline style from position 0 till length of the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable
        // string in TextView
        textView.text = mSpannableString
    }


    fun Fragment.showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun initSession(user: User) {
        sessionUser = user

        Log.d(TAG, "init Session user $sessionUser")
    }

//    fun getDate(timestamp: Long) :String {
//        val calendar = Calendar.getInstance(Locale.ENGLISH)
//        calendar.timeInMillis = timestamp * 1000L
//        val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
//        return date
//    }
}