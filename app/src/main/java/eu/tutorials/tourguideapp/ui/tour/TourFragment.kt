package eu.tutorials.tourguideapp.ui.tour

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.tourguideapp.utils.Constants.TOUR_KEY
import eu.tutorials.tourguideapp.utils.Constants.showToast
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.viewModel.ToursViewModel
import eu.tutorials.tourguideapp.adapter.ToursAdapter
import eu.tutorials.tourguideapp.models.Tour
import eu.tutorials.tourguideapp.databinding.FragmentToursBinding
import eu.tutorials.tourguideapp.ui.login.LoginActivity
import eu.tutorials.tourguideapp.utils.Resource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TourFragment : Fragment() {
    private val TAG = "TourFragment"
    private var _binding: FragmentToursBinding? = null

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    // Declare Firestore instance
    var db = FirebaseFirestore.getInstance()
    val toursList: ArrayList<Tour> = ArrayList()

    //Initialize viewModel
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[ToursViewModel::class.java]
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentToursBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addATourBtn.setOnClickListener {
            checkIfUserIsLoggedIn()
        }
        binding.profileBtn.setOnClickListener {
            checkIfUserExist()
        }
        showProgressBar()
    }

    override fun onStart() {
        super.onStart()
        fetchAllTours(toursList = toursList)
    }

    private fun checkIfUserIsLoggedIn() {
        if (firebaseAuth.currentUser != null) {
            findNavController().navigate(R.id.to_AddTourFragment)
        } else {
            showToast("You need to Login or Register to add a tour")
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    private fun checkIfUserExist() {
        if (firebaseAuth.currentUser != null) {
            findNavController().navigate(R.id.to_profileFragment)
        } else {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            showToast("You need to Login or Register to see your profile")
        }
    }

    /**
     * Function is used to show the list of inserted data.
     */
    private fun fetchAllTours(toursList: ArrayList<Tour>) {
        // Adapter class is initialized and list is passed in the param.
        val tourAdapter = ToursAdapter(toursList) { tour -> itemOnClick(tour) }
        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        tourAdapter.clearData()
        lifecycleScope.launch {
            delay(500)
            viewModel.getTours(toursList).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Success -> {
                        hideProgressBar()
                        if (toursList.isNotEmpty()) {
                            // Hide progressbar
                            hideProgressBar()
                            binding.apply {
                                // adapter instance is set to the recyclerview to inflate the items.
                                toursRecyclerView.adapter = tourAdapter
                                // Set the LayoutManager that this RecyclerView will use.
                                toursRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
                                // Set the addItemDecoration that this RecyclerView will use.
                                toursRecyclerView.addItemDecoration(dividerItemDecoration)
                                toursRecyclerView.visibility = View.VISIBLE
                                emptyTextView.visibility = View.INVISIBLE
                            }

                        } else {
                            binding.apply {
                                toursRecyclerView.visibility = View.INVISIBLE
                                emptyTextView.visibility = View.VISIBLE
                            }
                        }

                    }
                    is Resource.Failure -> {
                        hideProgressBar()
                        binding.emptyTextView.visibility = View.VISIBLE
                        showToast(result.message)
                    }
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            tourProgressBar.visibility = View.INVISIBLE
            toursRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar() {
        binding.apply {
            tourProgressBar.visibility = View.VISIBLE
            toursRecyclerView.visibility = View.INVISIBLE
        }
    }

    /* Opens TourDetailsActivity when RecyclerView item is clicked. */
    private fun itemOnClick(tour: Tour) {
        val args = Bundle()
        args.putParcelable(TOUR_KEY, tour)
        findNavController().navigate(R.id.to_TourDetailsFragment, args)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}