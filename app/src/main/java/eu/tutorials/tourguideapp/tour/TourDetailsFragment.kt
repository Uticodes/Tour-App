package eu.tutorials.tourguideapp.tour

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.Constants
import eu.tutorials.Constants.getDate
import eu.tutorials.Constants.showToast
import eu.tutorials.tourguideapp.FirestoreImplementations
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.databinding.FragmentTourDetailsBinding
import eu.tutorials.tourguideapp.utils.Resource

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TourDetailsFragment : Fragment() {
    private val TAG = TourDetailsFragment::class.java.simpleName
    private var tour: Tour? = null
    private var _binding: FragmentTourDetailsBinding? = null
    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()
    //Get current user
    val user = firebaseAuth.currentUser

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTourDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tour = arguments?.getParcelable(Constants.TOUR_KEY)

        binding.apply {

            Log.d(TAG, "Argument =>:|: DocumentId ${tour?.id} ${tour?.placeName} || ${tour?.description}")

            placeNameTextView.text = tour?.placeName
            dateTextView.text = getDate(tour?.date.toString())
            descriptionTextView.text = tour?.description
            authorsNameTextView.text = getString(R.string.author, tour?.authorsName)
            Glide.with(requireContext())
                .load(tour?.placeImage)
                .placeholder(R.drawable.babs_dock)
                .into(placeImageView)

            if (tour?.email != user?.email){
                editBtn.visibility = View.GONE
                deleteBtn.visibility = View.GONE
            }

            editBtn.setOnClickListener {
                val args = Bundle()
                args.putParcelable(Constants.TOUR_KEY, tour)
                findNavController().navigate(R.id.to_AddTourFragment, args)
            }
            deleteBtn.setOnClickListener {
                FirestoreImplementations().deleteATour(
                    tour?.id.toString(),
                    result = { result->
                        when(result){
                            is Resource.Loading -> showProgressBar()
                            is Resource.Success -> {
                                hideProgressBar()
                                showToast("Tour deleted successfully")
                                findNavController().popBackStack()
                            }
                            is Resource.Failure -> {
                                hideProgressBar()
                                showToast(result.message)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            tourDetailsProgressBar.visibility = View.INVISIBLE
            deleteBtn.isEnabled = true
            editBtn.isEnabled = true
        }
    }

    private fun showProgressBar() {
        binding.apply {
            tourDetailsProgressBar.visibility = View.VISIBLE
            deleteBtn.isEnabled = false
            editBtn.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}