package eu.tutorials.tourguideapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.os.Build
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.models.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Constants {
    private const val TAG = "Constants"

    //App package name
    private const val PACKAGE_NAME = "eu.tutorials.tourguideapp"

    private lateinit var observer: Observer<User>

    var savedUser: User? = null
        private set

    //Collections
    const val COLLECTION_USERS = "Users"
    const val COLLECTION_TOURS = "Tours"

    //Intent
    const val TOUR_KEY = "$PACKAGE_NAME.TOUR_KEY"
    const val DOCUMENT_ID_KEY = "document_id"

    //Request codes
    const val EXTERNAL_STORAGE_REQUEST_CODE = 100
    const val URI_REQUEST_CODE = 101

    fun Fragment.spannableString(context: Context, textView: TextView) {
        val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

        // Setting underline style from position 0 till length of the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable string in TextView
        textView.text = mSpannableString
    }

    fun Activity.spannableString(context: Context, textView: TextView) {
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
        savedUser = user
        Log.d(TAG, "Init save Session user $savedUser")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(timestamp: String): String {

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")
        val formatted = current.format(formatter)

        return formatted.format(timestamp)
    }

    fun NavController.logOut() {
        savedUser = null
        popBackStack(R.id.nav_graph, true)
        navigate(R.id.nav_graph)
    }

    fun Fragment.logoutAlertDialog(logOut: () -> Unit) {

        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage("Sure you want to log out?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(R.string.log_out, DialogInterface.OnClickListener { dialog, id ->
                logOut()
            })
            // negative button text and action
            .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        //2. now setup to change color of the button
        //2. now setup to change color of the button
        alert.setOnShowListener(OnShowListener {
            alert.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(requireContext().resources.getColor(R.color.red))
        })
        // set title for alert dialog box
        alert.setTitle(R.string.log_out)
        // show alert dialog
        alert.show()
    }

}