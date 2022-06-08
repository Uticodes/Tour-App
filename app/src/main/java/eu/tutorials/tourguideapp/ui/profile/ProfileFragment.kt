package eu.tutorials.tourguideapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.tourguideapp.utils.Constants
import eu.tutorials.tourguideapp.utils.Constants.logOut
import eu.tutorials.tourguideapp.utils.Constants.logoutAlertDialog
import eu.tutorials.tourguideapp.utils.Constants.showToast
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.viewModel.ToursViewModel
import eu.tutorials.tourguideapp.adapter.UserTourFeedsAdapter
import eu.tutorials.tourguideapp.models.Tour
import eu.tutorials.tourguideapp.models.User
import eu.tutorials.tourguideapp.databinding.FragmentProfileBinding
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

    private lateinit var toolbar: Toolbar


    //Initialize viewModel
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[ToursViewModel::class.java]
    }

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
        logOut()
        showProgressBar()
    }

    private fun logOut() {
        binding.logoutLayout.setOnClickListener {
            logoutAlertDialog {
                viewModel.logOut().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Loading -> showProgressBar()
                        is Resource.Success -> {
                            hideProgressBar()
                            showToast(result.message)
                            findNavController().logOut()
                        }
                        is Resource.Failure -> {
                            hideProgressBar()
                            showToast(result.message)
                        }
                    }
                }
            }
        }
    }

    private fun fetchUserTourFeeds(toursList: ArrayList<Tour>) {
        // Adapter class is initialized and list is passed in the param.
        val tourAdapter = UserTourFeedsAdapter(toursList) { tour -> itemOnClick(tour) }
        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        tourAdapter.clearData()
        viewModel.getUserTourFeeds(toursList).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> showProgressBar()
                is Resource.Success -> {
                    hideProgressBar()
                    if (toursList.isNotEmpty()) {
                        // Hide progressbar
                        hideProgressBar()
                        binding.apply {
                            // adapter instance is set to the recyclerview to inflate the items.
                            toursFeedsRecyclerView.adapter = tourAdapter
                            // Set the LayoutManager that this RecyclerView will use.
                            toursFeedsRecyclerView.layoutManager =
                                LinearLayoutManager(requireActivity())
                            // Set the addItemDecoration that this RecyclerView will use.
                            toursFeedsRecyclerView.addItemDecoration(dividerItemDecoration)
                            toursFeedsRecyclerView.visibility = View.VISIBLE
                            emptyTextView.visibility = View.INVISIBLE
                        }

                    } else {
                        binding.apply {
                            toursFeedsRecyclerView.visibility = View.INVISIBLE
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

    /* Opens TourDetailsActivity when RecyclerView item is clicked. */
    private fun itemOnClick(tour: Tour) {
        val args = Bundle()
        args.putParcelable(Constants.TOUR_KEY, tour)
        findNavController().navigate(R.id.to_TourDetailsFragment, args)
    }

    override fun onStart() {
        super.onStart()
        fetchUserTourFeeds(toursList = toursList)
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