package eu.tutorials.tourguideapp.tour

import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.Constants.COLLECTION_TOURS
import eu.tutorials.Constants.DOCUMENT_ID_KEY
import eu.tutorials.Constants.TOUR_KEY
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.adapter.ToursAdapter
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.databinding.FragmentToursBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TourFragment : Fragment() {
    private val TAG = "TourFragment"
    private var _binding: FragmentToursBinding? = null
    var db = FirebaseFirestore.getInstance()
    private val tourRef = db.collection(COLLECTION_TOURS)
    val toursList: ArrayList<Tour> = ArrayList()
    private var documentId: String? = ""

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
            findNavController().navigate(R.id.to_AddTourFragment)
        }
        binding.profileBtn.setOnClickListener {
            findNavController().navigate(R.id.to_profileFragment)
        }
        showProgressBar()
        setupListOfDataIntoRecyclerView(toursList = toursList)
    }

    /**
     * Function is used to show the list of inserted data.
     */
    private fun setupListOfDataIntoRecyclerView(toursList: ArrayList<Tour>) {

        // Adapter class is initialized and list is passed in the param.
        val tourAdapter = ToursAdapter(toursList) { tour -> itemOnClick(tour) }
        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        tourAdapter.clearData()
        db.collection(COLLECTION_TOURS)
            .get()
            .addOnSuccessListener { result ->
                // Hide progressbar
                hideProgressBar()
                // Here we get the list of users in the form of documents.
                Log.e("Tours List", result.documents.toString())

                //if (toursList.isNotEmpty()) {
                for (document in result.documents) {
                    val tour = document.toObject(Tour::class.java)
                    if (tour != null) {

                        toursList.add(tour)
                    }

                    // TODO Step : Populate the toursList in the UI using RecyclerView.
                    // START
                    binding.apply {
                        // adapter instance is set to the recyclerview to inflate the items.
                        toursRecyclerView.adapter = tourAdapter
                        // Set the LayoutManager that this RecyclerView will use.
                        toursRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
                        //toursRecyclerView.setHasFixedSize(true)
                        // Set the addItemDecoration that this RecyclerView will use.
                        toursRecyclerView.addItemDecoration(dividerItemDecoration)
                        toursRecyclerView.visibility = View.VISIBLE
                        emptyTextView.visibility = View.GONE
                    }
                    documentId = document.id
                    Log.d(TAG, "TourInfo:|:|:|:${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                // Hide progressbar
                hideProgressBar()

                Log.w(TAG, "Error getting documents.", exception)
            }

        if (toursList.isNotEmpty()) {
            // Hide progressbar
            hideProgressBar()
            binding.apply {
                toursRecyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
            }

        } else {
            binding.apply {
                toursRecyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
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
        args.putString(DOCUMENT_ID_KEY, documentId)
        findNavController().navigate(R.id.to_TourDetailsFragment, args)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}