package eu.tutorials.tourguideapp.utils

import android.content.Context
import android.content.SharedPreferences
import eu.tutorials.Constants

class SharedPrefUtils(context: Context) {

    private val imageUrlPref: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = imageUrlPref.edit()

    fun getImageDownloadUrl() : String{
        imageUrlPref.getString(Constants.IMAGE_URL_KEY, null) // getting String
        return imageUrlPref.toString()
    }
    fun setImageDownloadUrl(value: String){
        editor.putString(Constants.IMAGE_URL_KEY, value)
        editor.commit()

    }

    fun deleteImageDownloadUrl(){
        editor.clear();
        editor.commit();
    }
}