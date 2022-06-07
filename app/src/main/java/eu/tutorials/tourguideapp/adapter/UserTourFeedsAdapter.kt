package eu.tutorials.tourguideapp.adapter

import android.os.Build
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.tutorials.Constants
import eu.tutorials.Constants.getDate
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.data.UserTour
import eu.tutorials.tourguideapp.databinding.TourItemViewBinding


/**
* We have the @param [tourItems] to represent the list that populates the adapter
**/
class UserTourFeedsAdapter(
    private val tourItems: ArrayList<Tour>,
    private val listener: (Tour) -> Unit
) : RecyclerView.Adapter<UserTourFeedsAdapter.UserToursFeedsViewHolder>() {
    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserToursFeedsViewHolder {
        val binding = TourItemViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserToursFeedsViewHolder(binding)
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UserToursFeedsViewHolder, position: Int) {
        with(holder){
            with(tourItems[position]) {
                val context = holder.itemView.context

                val mSpannableString = SpannableString(context.getString(R.string.view_on_google_maps))

                // Setting underline style from position 0 till length of
                // the spannable string
                mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

                binding.placeNameTextView.text = placeName
                binding.dateTextView.text = getDate(date.toString())
                binding.descriptionTextView.text = description
                binding.authorNameTextView.text = context.getString(R.string.author, authorsName)

                Log.d("TourAdapter Image =|=|=", "===$authorsName")

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
     * Clear items in the list
     */
    fun clearData() {
        tourItems.clear()
        notifyDataSetChanged()
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class UserToursFeedsViewHolder(val binding: TourItemViewBinding) :RecyclerView.ViewHolder(binding.root)

}
