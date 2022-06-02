package eu.tutorials.tourguideapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.tutorials.Constants.decodeBitmapImage
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.databinding.TourItemViewBinding
import eu.tutorials.tourguideapp.tour.AddTourFragment
import eu.tutorials.tourguideapp.tour.TourFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


/**
* We have the @param [tourItems] to represent the list that populates the adapter
**/
class ToursAdapter(
    private val tourItems: ArrayList<Tour>,
    private val listener: (Tour) -> Unit
) : RecyclerView.Adapter<ToursAdapter.ToursViewHolder>() {
    private val context: Context? = null
    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToursViewHolder {
        val binding = TourItemViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ToursViewHolder(binding)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ToursViewHolder, position: Int) {
        with(holder){
            with(tourItems[position]) {
                val context = holder.itemView.context

                val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

                // Setting underline style from
                // position 0 till length of
                // the spannable string
                mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

                // Displaying this spannable
                // string in TextView
                //binding.viewOnMap.text = mSpannableString

                binding.placeNameTextView.text = placeName
                binding.dateTextView.text = date.toString()
                binding.descriptionTextView.text = description
                binding.authorNameTextView.text = authorName

//                val uri = Uri.parse(placeImage)
//                //val compressedImageFile = Compressor.compress(context, uri.toFile())
//                //binding.placeImageView.setImageDrawable(placeImage.toString().tO as DrawableRes)
//                var imageUri = "data:image/jpeg;base64,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"
                //val decodedString: ByteArray = Base64.decode(placeImage, Base64.NO_WRAP)
                //val img = BitmapFactory.decodeByteArray(decodedString,  decodedString.size)
                //val img = decodeBitmapImage(placeImage.toString(), binding.placeImageView)
//                Log.d("TourAdapter Image =|=|=", "$img")
//                val image = getImageUrlWithAuthority(context, uri)
                Log.d("TourAdapter Image =|=|=", "===$placeImage")


                val image2 = "https://www.himgs.com/imagenes/hello/social/hello-fb-logo.png"
                val imageLink = "https://firebasestorage.googleapis.com/v0/b/tour-guide-app-993c2.appspot.com/o/tourImages%2FaI54C6Lt2SVsGNiHhvRpLwzKimp1?alt=media&token=7eefbead-ac6c-4455-87aa-3067c0678ebb"
                //val decodedString: ByteArray = Base64.decode(imageLink, Base64.DEFAULT)
                //val `in` = java.net.URL(imageLink).openStream()
                //val image: Bitmap = BitmapFactory.decodeStream(`in`)

                // Only for making changes in UI
//                CoroutineScope(Dispatchers.IO).launch {
//                    //binding.placeImageView.setImageBitmap(image2)
//                    binding.placeImageView.setImageURI(image2.toUri())
//                }




//                val decodedImg = decodeBitmapImage(imageLink)
//                binding.placeImageView.drawable
//                binding.placeImageView.setImageBitmap(decodedImg)
                Glide.with(holder.itemView.context)
                    .load(placeImage)
                    .into(binding.placeImageView)

                holder.itemView.setOnClickListener {
                    listener.invoke(tourItems[position])
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount() = tourItems.size

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class ToursViewHolder(val binding: TourItemViewBinding) :RecyclerView.ViewHolder(binding.root)


//    private fun getImageUrlWithAuthority(context: Context, uri: Uri): String? {
//        val tourFragmentContext = TourFragment()
//        var inputStream: InputStream? = null
//        if (uri.authority != null) {
//            Log.d("TourAdapter Image =|=|=", "authority not null")
//            try {
//                val cnt = tourFragmentContext.requireActivity()
//                inputStream = cnt.contentResolver.openInputStream(uri)
//                val bmp = BitmapFactory.decodeStream(inputStream)
//                Log.d("TourAdapter Image =|=|=", "Stream Bitmap $bmp")
//                return writeToTempImageAndGetPathUri(context, bmp).toString()
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//                Log.d("TourAdapter Image =|=|=", "Stream Bitmap error $e")
//            } finally {
//                try {
//                    inputStream?.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//        return null
//    }

//    private fun writeToTempImageAndGetPathUri(inContext: Context, inImage: Bitmap): Uri {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path =
//            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "tourImage", null)
//        Log.d("TourAdapter Image =|=|=", "Bitmap path $path")
//        return Uri.parse(path)
//    }
}
