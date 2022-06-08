package eu.tutorials.tourguideapp.ui.tour

import android.Manifest
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.tourguideapp.utils.Constants
import eu.tutorials.tourguideapp.utils.Constants.EXTERNAL_STORAGE_REQUEST_CODE
import eu.tutorials.tourguideapp.utils.Constants.URI_REQUEST_CODE
import eu.tutorials.tourguideapp.utils.Constants.showToast
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.viewModel.ToursViewModel
import eu.tutorials.tourguideapp.models.Tour
import eu.tutorials.tourguideapp.databinding.FragmentAddTourBinding
import eu.tutorials.tourguideapp.utils.Resource
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class AddTourFragment : Fragment() {
    private val TAG = "AddTourFragment"

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    private var _binding: FragmentAddTourBinding? = null
    private var tour: Tour? = null
    private var tourId: String = ""
    private var placeName: String = ""
    private var date: String = ""
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
                if (tour != null) {
                    editTour(tour)
                } else {
                    showProgressBar()
                    addTour()
                }
            }
            // END


            // TODO Step : Select an image from device.
            // START
            chooseImageBtn.setOnClickListener { checkPermissions() }
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
            // Launches the selected image using the constant code.
            startActivityForResult(galleryIntent, URI_REQUEST_CODE)
            // END
        } else {
            /*Requests permissions to be granted to this application. These permissions
             must be requested in your manifest, they should not be granted to your app,
             and they should have protection level*/
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_REQUEST_CODE
            )
        }
    }

    // TODO Step : Override onRequestPermissionsResult the function to check the storage permission result based on the request code.
    // START
    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO Step : After the permission is granted, implement image selection.
                // START
                // An intent for launching the image selection of phone storage.
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                // Launches the image gallery of phone storage using the constant code.
                startActivityForResult(galleryIntent, URI_REQUEST_CODE)
                // END
            } else {
                //Displaying another toast if permission is not granted
                showToast("Storage permission denied. You can also allow it from settings.")
            }
        }
    }
    // END

    // TODO Step : Get the result of the selected image based on the request code.
    // START
    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == URI_REQUEST_CODE) {
                if (data != null) {
                    try {

                        // TODO Step : Initialize the global variable for URI and set the image to the ImageView.
                        // START
                        // The uri of selected image from phone storage.
                        selectedImageFileUri = data.data!!

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
                }
            } else {
                // A log is printed when user close or cancel the image selection.
                Log.e("InvalidRequestCode", "Invalid request code")
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    // END


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


//                if (selectedImageFileUri.toString().isNotEmpty()){
//                    FirestoreImplementations().editTour(
//                        docId,
//                        placeName,
//                        placeDescription,
//                        selectedImageFileUri ?: "".toUri(),
//                        result = { result->
//                            when(result) {
//                                is Resource.Loading -> showProgressBar()
//                                is Resource.Success -> {
//                                    hideProgressBar()
//                                    showToast("Tour edited successfully")
//                                    findNavController().popBackStack()
//                                }
//                                is Resource.Failure -> {
//                                    showToast(result.message)
//                                }
//                            }
//                        }
//                    )
//                }else {
//                    tour.placeImage?.toUri()?.let {
//                        FirestoreImplementations().editTour(
//                            docId,
//                            placeName,
//                            placeDescription,
//                            it,
//                            result = { result->
//                                when(result) {
//                                    is Resource.Loading -> showProgressBar()
//                                    is Resource.Success -> {
//                                        hideProgressBar()
//                                        showToast("Tour edited successfully")
//                                        findNavController().popBackStack()
//                                    }
//                                    is Resource.Failure -> {
//                                        showToast(result.message)
//                                    }
//                                }
//                            }
//                        )
//                    }
//                }


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