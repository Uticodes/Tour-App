package eu.tutorials.tourguideapp.tour

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
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import eu.tutorials.tourguideapp.databinding.FragmentAddTourBinding
import java.io.IOException


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddTourFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTourFragment : Fragment() {
    private val TAG = AddTourFragment::class.java.simpleName

    private val EXTERNALSTORAGEREQUESTCODE = 100
    private val URIREQUESTCODE = 101

    private var _binding: FragmentAddTourBinding? = null
    private val binding get() = _binding!!
    var db = FirebaseFirestore.getInstance()
    // A global variable for URI of a selected image from phone storage.
    private var selectedImageFileUri: Uri? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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

        binding.apply {
            uploadBtn.setOnClickListener {
                startActivity(Intent(requireActivity(), ToursActivity()::class.java))
                //findNavController().navigate(R.id.to_ToursFragment)
            }

            // TODO Step : Upload tour info to the cloud firestore.
            // START
            uploadBtn.setOnClickListener {
                addItems()
            }
            // END

            chooseImageBtn.setOnClickListener {
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
                    startActivityForResult(galleryIntent, URIREQUESTCODE)
                    // END
                } else {
                    /*Requests permissions to be granted to this application. These permissions
                     must be requested in your manifest, they should not be granted to your app,
                     and they should have protection level*/
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        EXTERNALSTORAGEREQUESTCODE
                    )
                }
            }
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
        if (requestCode == EXTERNALSTORAGEREQUESTCODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO Step : After the permission is granted, implement image selection.
                // START
                // An intent for launching the image selection of phone storage.
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                // Launches the image selection of phone storage using the constant code.
                startActivityForResult(galleryIntent, URIREQUESTCODE)
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    requireContext(),
                    "Oops, storage permission denied. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
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
            if (requestCode == URIREQUESTCODE) {
                if (data != null) {
                    try {

                        // TODO Step : Initialize the global variable for URI and set the image to the ImageView.
                        // START
                        // The uri of selected image from phone storage.
                        selectedImageFileUri = data.data!!
                        binding.placeImageView.visibility = View.VISIBLE
                        binding.placeImageView.setImageURI(selectedImageFileUri)
                        Log.d(TAG, "Showing image Uri $selectedImageFileUri")
                        // END
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Image selection Failed!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    // END

    private fun setUpAddTours(){

    }

    private fun addItems(){
        val countryName = binding.countryName.text.toString().trim()
        val placeName = binding.placeName.text.toString().trim()
        val startDate = binding.startDate.text.toString().trim()
        val endDate = binding.endDate.text.toString().trim()
        val placeImageView = binding.placeImageView
        val placeDescription = binding.placeDescription.text.toString().trim()

        // Create a new user with a first and last name
        val tour = hashMapOf(
            "countryName" to countryName,
            "placeName" to placeName,
            "startDate" to startDate,
            "endDate" to endDate,
            "description" to placeDescription,
            "placeImage" to selectedImageFileUri
        )

        // Add a new Tour document with a generated ID
        if (countryName.isNotEmpty() && placeName.isNotEmpty()
            && startDate.isNotEmpty() && endDate.isNotEmpty()
            && placeDescription.isNotEmpty() && selectedImageFileUri != null){
            db.collection("addATour")
                .add(tour)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        requireContext(),
                        "Tour added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Tour DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error adding document $e" ,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w(TAG, "Error adding document", e)
                }
        } else {
            Toast.makeText(
                requireContext(),
                "Kindly check that all information are provided and try again" ,
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddTourFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddTourFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}