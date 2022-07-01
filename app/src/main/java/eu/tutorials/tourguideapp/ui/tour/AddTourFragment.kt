package eu.tutorials.tourguideapp.ui.tour

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.databinding.FragmentAddTourBinding
import eu.tutorials.tourguideapp.models.Tour
import eu.tutorials.tourguideapp.utils.Constants
import eu.tutorials.tourguideapp.utils.Constants.showToast
import eu.tutorials.tourguideapp.utils.Resource
import eu.tutorials.tourguideapp.viewModel.ToursViewModel
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates


class AddTourFragment : Fragment() {
    private val TAG = "AddTourFragment"

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var startActivityLaunch: ActivityResultLauncher<Intent>

    private var _binding: FragmentAddTourBinding? = null
    private var tour: Tour? = null
    private var tourId: String = ""
    private var placeName: String = ""
    private var placeImageView: ImageView? = null
    private var placeDescription: String = ""
    private var documentId: String? = ""
    val appUser = Constants.savedUser
    private var isUploadNewImageForEdit by Delegates.notNull<Boolean>()

    private val binding get() = _binding!!

    // A global variable for URI of a selected image from phone storage.
    private var selectedImageFileUri: Uri? = null

    //Initialize viewModel
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[ToursViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    showToast("Storage permission granted")
                } else {
                    //Displaying another toast if permission is not granted
                    showToast("Storage permission denied. You can also allow it from settings.")
                }
            }

        //Checks for READ_EXTERNAL_STORAGE permission
        startActivityLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                //Get the data returned by intent
                val resultCode = it.resultCode
                val data = it.data
                
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    try {

                        // TODO Step : Initialize the global variable for URI and set the image to the ImageView.
                        // START
                        // The uri of selected image from phone storage.
                        selectedImageFileUri = data.data

                        //Show imageView if Image Uri is null
                        binding.previewImageView.visibility = View.VISIBLE
                        //isUploadNewImageForEdit = true

                        binding.previewImageView.setImageURI(selectedImageFileUri)

                        Log.d(TAG, "Selected Image Uri file ||| === ${selectedImageFileUri}")
                        Log.d(TAG, "Showing image Uri $selectedImageFileUri")
                        // END
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showToast("Image selection Failed! ${e.message}")
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddTourBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tour = arguments?.getParcelable(Constants.TOUR_KEY)
        documentId = arguments?.getString(Constants.DOCUMENT_ID_KEY).toString()
        Log.d(
            "AddTourFragment",
            "AppUser details $appUser:::: ${firebaseAuth.currentUser?.uid}:::: ${firebaseAuth.currentUser?.displayName}:::: ${firebaseAuth.currentUser?.email}"
        )

        isUploadNewImageForEdit = false

        // TODO Step : Get intent argument.
        // START
        Log.d(TAG, "Argument===: $tour")
        if (tour != null) {
            with(binding) {
                placeName.setText(tour?.placeName)
                placeDescription.setText(tour?.description)
                tourId = tour?.id.toString()
                uploadBtn.text = getString(R.string.upload_edit)
                previewImageView.visibility = View.VISIBLE
                chooseImageBtn.isEnabled = false
                Glide.with(requireContext())
                    .load(tour?.placeImage)
                    .placeholder(R.drawable.babs_dock)
                    .into(previewImageView)
            }
        }
        // END

        placeName = binding.placeName.text.toString().trim()
        placeImageView = binding.previewImageView
        placeDescription = binding.placeDescription.text.toString().trim()

        binding.apply {
            // TODO Step : Upload tour info to the cloud firestore.
            // START
            uploadBtn.setOnClickListener {
                //Check that all fields are not empty before button action is taken
                if (placeName.text.isNotEmpty() && placeDescription.text.isNotEmpty()
                    && placeImageView?.drawable != null){
                    if (tour != null) {
                        editTour(tour)
                    } else {
                        showProgressBar()
                        addTour()
                    }
                }
            }
            // END


            // TODO Step : Select an image from device.
            // START
            chooseImageBtn.setOnClickListener {
                checkPermissions()
            }
            // END
        }
    }

    private fun checkPermissions() {
        //TODO Open gallery and get image

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            // TODO Step : Now after the permission is granted write the code to select the image.
            // START
            // An intent for launching the image selection of phone storage.
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            // Launches the selected image intent
            startActivityLaunch.launch(galleryIntent)
            // END
        }
    }

    private fun addTour() {
        placeName = binding.placeName.text.toString().trim()
        placeImageView = binding.previewImageView
        placeDescription = binding.placeDescription.text.toString().trim()
        val user = firebaseAuth.currentUser
        val uniqueId = UUID.randomUUID().toString()

        val tour = Tour(
            id = uniqueId,
            placeName = placeName,
            date = Date(),
            description = placeDescription,
            authorsName = user?.displayName ?: "",
            email = user?.email ?: "",
        )

        // Add a new Tour document with a generated ID
        if (placeName.isNotEmpty() && placeDescription.isNotEmpty()
            && placeImageView?.drawable != null
        ) {
            viewModel.addTour(
                tour, selectedImageFileUri ?: "".toUri()
            ).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Success -> {
                        hideProgressBar()
                        showToast("Tour uploaded successfully")
                        findNavController().popBackStack()
                    }
                    is Resource.Failure -> {
                        showToast(result.message)
                    }

                }
            }
        } else {
            showToast("Kindly check that all information are provided and try again")
        }
    }

    private fun editTour(tour: Tour?) {

        if (tour != null) {
            val placeName = binding.placeName.text.toString().trim()
            val placeDescription = binding.placeDescription.text.toString().trim()
            // Update existing Tour document
            if (placeName.isNotEmpty() && placeDescription.isNotEmpty()) {
                viewModel.editTour(
                    tour.copy(
                        placeName = placeName,
                        description = placeDescription
                    ),
                ).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Loading -> showProgressBar()
                        is Resource.Success -> {
                            hideProgressBar()
                            showToast("Tour edited successfully")
                            findNavController().navigate(R.id.nav_graph)
                        }
                        is Resource.Failure -> {
                            hideProgressBar()
                            showToast(result.message)
                        }
                    }
                }

            } else {
                showToast("Kindly check that all information are provided and try again")
            }
        } else {
            Log.d("AddTourFragment", "Tour intent argument is null")
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            addTourProgressBar.visibility = View.INVISIBLE
            uploadBtn.isEnabled = true
            chooseImageBtn.isEnabled = true
        }
    }

    private fun showProgressBar() {
        binding.apply {
            addTourProgressBar.visibility = View.VISIBLE
            uploadBtn.isEnabled = false
            chooseImageBtn.isEnabled = false
        }
    }
}