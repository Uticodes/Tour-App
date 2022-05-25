package eu.tutorials.tourguideapp.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.databinding.TourItemViewBinding

/**
* We have the @param [tourItems] to represent the list that populates the adapter
**/
class ToursAdapter(
    private val tourItems: List<Tour>,
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
                binding.viewOnMap.text = mSpannableString

                binding.countryTextView.text = countryName
                binding.placeNameTextView.text = placeName
                binding.dateTextView.text = date
                binding.descriptionTextView.text = description

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
    inner class ToursViewHolder(val binding: TourItemViewBinding)
        :RecyclerView.ViewHolder(binding.root)

}
