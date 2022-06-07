package eu.tutorials.tourguideapp.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.Constants
import eu.tutorials.Constants.showToast
import eu.tutorials.tourguideapp.FirestoreImplementations
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.adapter.ToursAdapter
import eu.tutorials.tourguideapp.adapter.UserTourFeedsAdapter
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.data.User
import eu.tutorials.tourguideapp.data.UserTour
import eu.tutorials.tourguideapp.databinding.FragmentProfileBinding
import eu.tutorials.tourguideapp.databinding.FragmentToursBinding
import eu.tutorials.tourguideapp.tour.ToursActivity
import eu.tutorials.tourguideapp.utils.Resource

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val TAG = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null
    private var user: User? = null
    val toursList: ArrayList<Tour> = ArrayList()
    val appUser = Constants.savedUser
    private var firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = user
        Log.d(TAG, "User Info =|=|===:: ${user?.name}")
        Log.d(TAG, "AppUser Info =|=|===:: $appUser")

        binding.emailEt.setText(currentUser?.email)
        binding.nameEt.setText(currentUser?.displayName)

        showProgressBar()
    }

    private fun fetchUserTourFeeds(toursList: ArrayList<Tour>){
        // Adapter class is initialized and list is passed in the param.
        val tourAdapter = UserTourFeedsAdapter(toursList) { tour -> itemOnClick(tour) }
        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)

        FirestoreImplementations().getUserTourFeeds(
            toursList,
            result = { result ->
                when (result) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Success -> {
                        hideProgressBar()
                        binding.apply {
                            // adapter instance is set to the recyclerview to inflate the items.
                            toursFeedsRecyclerView.adapter = tourAdapter
                            // Set the LayoutManager that this RecyclerView will use.
                            toursFeedsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
                            //toursRecyclerView.setHasFixedSize(true)
                            // Set the addItemDecoration that this RecyclerView will use.
                            toursFeedsRecyclerView.addItemDecoration(dividerItemDecoration)
                            toursFeedsRecyclerView.visibility = View.VISIBLE
                            emptyTextView.visibility = View.GONE
                        }
                    }
                    is Resource.Failure -> {
                        hideProgressBar()
                        showToast(result.message)
                    }
                }
            }
        )

        if (toursList.isNotEmpty()) {
            // Hide progressbar
            hideProgressBar()
            binding.apply {
                toursFeedsRecyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
            }

        } else {
            binding.apply {
                toursFeedsRecyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            }
        }
    }

    /* Opens TourDetailsActivity when RecyclerView item is clicked. */
    private fun itemOnClick(tour: Tour) {
        val args = Bundle()
        args.putParcelable(Constants.TOUR_KEY, tour)
        //args.putString(DOCUMENT_ID_KEY, documentId)
        findNavController().navigate(R.id.to_TourDetailsFragment, args)
    }

    override fun onStart() {
        super.onStart()
        FirestoreImplementations().getUserInfo()
        fetchUserTourFeeds(toursList = toursList)
        //setupListOfDataIntoRecyclerView(toursList = toursList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideProgressBar() {
        binding.apply {
            userFeedsProgressBar.visibility = View.INVISIBLE
            toursFeedsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar() {
        binding.apply {
            userFeedsProgressBar.visibility = View.VISIBLE
            toursFeedsRecyclerView.visibility = View.INVISIBLE
        }
    }
}