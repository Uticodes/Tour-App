package eu.tutorials.tourguideapp.tour

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import eu.tutorials.Constants
import eu.tutorials.Constants.spannableString
import eu.tutorials.tourguideapp.FirestoreImplementations
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.databinding.FragmentTourDetailsBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TourDetailsFragment : Fragment() {
    private val TAG = TourDetailsFragment::class.java.simpleName
    private var tour: Tour? = null
    private var _binding: FragmentTourDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTourDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tour = arguments?.getParcelable(Constants.TOUR_KEY)


        binding.apply {
//            val mSpannableString = SpannableString(getString(R.string.view_on_google_maps))
//            mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
//            binding.viewOnMap.text = mSpannableString

            Log.d(TAG, "Argument => ${tour?.placeName} || ${tour?.description}")

            binding.placeNameTextView.text = tour?.placeName
            binding.dateTextView.text = tour?.date.toString()
            binding.descriptionTextView.text = tour?.description
            //decodeBitmapImage(tour?.placeImage ?: "", binding.placeImageView)

            spannableString(requireContext(), binding.viewOnMap)
//            countryTextView.text = getString(R.string.country_name1)
//            placeNameTextView.text = getString(R.string.place_name1)
//            dateTextView.text = getString(R.string.tour_date)
//            descriptionTextView.text = getString(R.string.tour_description)
            Glide.with(requireContext())
                .load(tour?.placeImage)
                .placeholder(R.drawable.babs_dock)
                .into(binding.placeImageView)

            editBtn.setOnClickListener {
                val args = Bundle()
                args.putParcelable(Constants.TOUR_KEY, tour)
                //Toast.makeText(requireContext(), "Edit button Clicked", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.to_AddTourFragment, args)
            }
            deleteBtn.setOnClickListener {
                FirestoreImplementations().deleteATour(tour, nav = {
                    findNavController().navigate(R.id.to_ToursFragment)
                }, this@TourDetailsFragment)
//                Toast.makeText(requireContext(), "Delete button Clicked", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}