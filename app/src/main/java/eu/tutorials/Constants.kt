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
import androidx.fragment.app.Fragment
import eu.tutorials.tourguideapp.R
import java.io.ByteArrayOutputStream

object Constants {
    //App package name
    private const val PACKAGE_NAME = "eu.tutorials.tourguideapp"

    //Collections
    const val COLLECTION_USERS = "Users"
    const val COLLECTION_TOURS = "Tours"
    const val COLLECTION_USERS_ADD_TOURS = "Users/Tours"

    //Intent
    const val TOUR_KEY = "${PACKAGE_NAME}.TOUR_KEY"
    const val PREF_KEY = "${PACKAGE_NAME}.PREF_KEY"
    const val IMAGE_URL_KEY = "image_download_url"

    //Request codes
    const val EXTERNAL_STORAGE_REQUEST_CODE = 100
    const val URI_REQUEST_CODE = 101


    fun Fragment.toBitmap(imageUri: String){
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.parse(imageUri))
    }

    fun Fragment.spannableString(context: Context, textView: TextView){
        val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

        // Setting underline style from
        // position 0 till length of
        // the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable
        // string in TextView
        textView.text = mSpannableString
    }

    fun Activity.spannableString(context: Context, textView: TextView){
        val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

        // Setting underline style from
        // position 0 till length of
        // the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable
        // string in TextView
        textView.text = mSpannableString
    }

    fun decodeBitmapImage(base64String: String /*, imageview: ImageView*/): Bitmap? {
        //decode base64 string to image
        //decode base64 string to image
        val baos = ByteArrayOutputStream()
        var imageBytes = baos.toByteArray()
        imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        //imageview.setImageBitmap(decodedImage)
        Log.d("Adapter", "Showing decodeImage === $decodedImage")
//        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
//        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
//        imageView.setImageBitmap(decodedImage)
        return decodedImage
    }

    private fun encodeImage(bm: Bitmap): String? {
//        Uri imageUri = intent.getData();
//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
//        Imageview my_img_view = (Imageview ) findViewById (R.id.my_img_view);
//        my_img_view.setImageBitmap(bitmap





        val baos = ByteArrayOutputStream()
        //val bitmap = BitmapFactory.decodeResource(resources, image.toInt())
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()

        Log.d("encodeImage", "Showing encodeImage === $imageBytes")
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
}