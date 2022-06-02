package eu.tutorials.tourguideapp

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import eu.tutorials.Constants
import eu.tutorials.Constants.COLLECTION_TOURS
import eu.tutorials.Constants.COLLECTION_USERS
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.data.TourImage
import eu.tutorials.tourguideapp.data.User
import eu.tutorials.tourguideapp.login.LoginActivity
import eu.tutorials.tourguideapp.tour.AddTourFragment
import eu.tutorials.tourguideapp.tour.TourDetailsFragment
import eu.tutorials.tourguideapp.tour.ToursActivity
import eu.tutorials.tourguideapp.utils.SharedPrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class FirestoreImplementations() {
    // Declare FirebaseFirestore instance
    private var fbFirestore = FirebaseFirestore.getInstance()

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()


    // Declare Firebase storage
    private val storage = Firebase.storage

    // Create a storage reference from our app
    private val storageRef = storage.reference

    // Declare User collection reference
    private val userRef = fbFirestore.collection(COLLECTION_USERS)

    // Declare Tour collection reference
    private val tourRef = fbFirestore.collection(COLLECTION_TOURS)

    // Get Firebase current user
    var user = firebaseAuth.currentUser

    // Varieble to get Firebase storage image downloaded URL.
    private var imageUrl: Uri? = null
    private var getDownloadImageUrl: Uri? = null


    // Create context
    var activityContext = Activity()
    var lContext = LoginActivity()

//    var pref: SharedPreferences = lContext.getSharedPreferences(Constants.PREF_KEY, MODE_PRIVATE)
//    var editor: SharedPreferences.Editor = pref.edit()

    // TODO Step : Implement Firebase signIn.
    fun signInUser(userEmail: String, userPassword: String, intent: () -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(activityContext) { task ->
                if (task.isSuccessful) {
                    var user = firebaseAuth.currentUser
                    Log.d(
                        "LoginActivity",
                        "User Info user:::::: ${user?.email}:::${user?.displayName}:::${user?.uid}"
                    )

                    task.addOnSuccessListener {
                        Log.d(
                            "LoginActivity",
                            "User Info:::::: ${it.user?.email}:::${it.user?.displayName}:::${it.user?.uid}"
                        )

                    }
                    addUserDetails()
                    Log.d("LoginActivity", "signInUserWithEmail:success")
                    intent()
                } else {
                    Toast.makeText(
                        activityContext,
                        "An error has occurred during login. ${task.exception}, Please try again later.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("LoginActivity", "signInUserWithEmail:failure", task.exception)
                }
            }
    }


    //TODO : Register a user with Firebase.
    fun registerUser(
        context: LoginActivity,
        userName: String,
        userEmail: String,
        userPassword: String,
        intent: () -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Show UserName:::: $userName")
                    saveUserToFirebaseDatabase(userName, userEmail)
                    Log.d("LoginActivity", "createUserWithEmail:success")
                    intent()
                } else {
                    Log.d("LoginActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "An error has occurred during signup. ${task.exception}, Please try again later.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // TODO Step : Implement Add User details to Firestore.
    private fun addUserDetails() {

        val userMap = hashMapOf(
            "id" to user?.uid,
            "name" to user?.displayName,
            "placeName" to user?.email,
        )

        //fbFirestore.collection(Constants.COLLECTION_USERS)
        // Document ID for users fields.
        // Here the document it is auto created. You can also define whatever you want by passing the value as param.
        userRef.document(user?.email.toString())
            // Here the userInfo are Fields for the database with the values.
            .set(userMap)
            .addOnSuccessListener {

                Toast.makeText(
                    activityContext,
                    "User data saved successfully.",
                    Toast.LENGTH_SHORT
                ).show()

                // TODO Step : Call the getUserInfo function to get the user.
                // START
                getUserInfo()
                // END
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    activityContext,
                    "User data failed to save $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    //TODO : Get user info from firestore.
    fun getUserInfo() {
        userRef.document(user?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                //result.data
                Log.d("LoginActivity", "User metadata:::::: ${result.metadata}")
                Log.d("LoginActivity", "User metadata data:::::: ${result.data}")
                Log.d("LoginActivity", "User metadata data:::::: ${result.data}")
                Log.d(
                    "LoginActivity",
                    "User Info:::::: ${user?.email}, :::${user?.displayName}, :::${user?.uid}"
                )
                //result.data.toString()
                val userInfo = User(
                    user?.uid.toString(),
                    user?.displayName.toString(),
                    user?.email.toString(),
                )
                // Here we get the users info from the user document.
                Log.d("ProfileFragment", "User Info =|=|===:: $userInfo")

            }
            .addOnFailureListener { exception ->
                Log.d("ProfileFragment", "Error getting documents.", exception)
            }
    }

    //TODO : Add User info to firestore.
    private fun saveUserToFirebaseDatabase(userName: String, email: String) {
        val uid = firebaseAuth.uid ?: ""
        val ref = userRef.document("/$uid")
        Log.d("LoginActivity", "Show UserName:::: $userName")
        val user = User(uid, userName, email)

        ref.set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    updateUserDisplayName(userName)
                    Log.d("LoginActivity", "Show UserName:::: $userName")
                    Log.d("LoginActivity", "Finally we saved the user to Firebase Database")
                } else {
                    Log.d("LoginActivity", "Failed to save User", task.exception)
                    Toast.makeText(
                        activityContext,
                        "Failed to save User. ${task.exception}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "Failed to set value to database: ${it.message}")
            }
    }

    //TODO : Update User DisplayName on firestore.
    private fun updateUserDisplayName(userName: String) {

        // [START update_profile]
        val user = FirebaseAuth.getInstance().currentUser
        // [START update_profile]
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(userName).build()
        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->

                task.addOnSuccessListener {
                    Log.d("LoginActivity", "Show UserName:::: $userName")
                    Log.d(
                        "LoginActivity",
                        "User DisplayName updated :$task"
                    )
                }

            }
            ?.addOnFailureListener {
                Log.d("LoginActivity", "Failed to update DisplayName: ${it.message}")
            }
        // [END update_profile]
    }

    fun uploadImageToFirebaseStorage(selectedImageUri: Uri, context: AddTourFragment) {
        val fileName = UUID.randomUUID().toString()

        val uploadTask = storageRef.child("tourImages/${firebaseAuth.currentUser?.uid}")
        uploadTask.putFile(selectedImageUri).addOnSuccessListener { it ->
            uploadTask.downloadUrl.addOnSuccessListener { uri ->
                TourImage(
                    imageDownloadUrl = uri
                )

                SharedPrefUtils(context.requireContext()).setImageDownloadUrl(uri.toString())
                imageUrl = uri
                getDownloadImageUrl = uri
                Log.d("AddTourFragment", "Image File Location:// $uri")
                //SharedPreferences.

//                val downloadImgUrl = TourImage()
//                downloadImgUrl.imageDownloadUrl = uri
                Log.d("AddTourFragment", "TourImage imageUrl:||:||: $getDownloadImageUrl")

            }
            //imageUrl = downloadUrl
            Log.d("AddTourFragment", "Successfully upload image: ${it.metadata?.path}")
        }.addOnFailureListener { e ->
            Log.d("AddTourFragment", "Failed to upload image $e")
        }
    }

    //TODO : Add Tour info to firestore.
    fun addTour(
        placeName: String, placeDescription: String, context: AddTourFragment
    ) {
        var  imageDownloadUrl = ""
        CoroutineScope(Dispatchers.IO).launch {
           imageDownloadUrl = SharedPrefUtils(context.requireContext()).getImageDownloadUrl()
            Log.d("AddTourFragment", "CoroutineScope TourImage link imgFromSharedPref: ${imageDownloadUrl} :|:|: ${SharedPrefUtils(context.requireContext()).getImageDownloadUrl()}")
        }.invokeOnCompletion {
            Log.d("AddTourFragment", "CoroutineScope Complete TourImage link: ${imageDownloadUrl}")
        }
        //val imgFromSharedPref =  pref.getString(Constants.IMAGE_URL_KEY, null)

        val uniqueId = UUID.randomUUID().toString()
        //val user = User()
        val imgUrl = TourImage()
        Log.d("AddTourFragment", "User display name: ${user?.displayName}")
        Log.d("AddTourFragment", "TourImage link imgFromSharedPref: ${imageDownloadUrl}")
        // Create a new tour details
        val tour = hashMapOf(
            "id" to uniqueId,
            "placeName" to placeName,
            "date" to Timestamp(Date()),
            "authorsName" to user?.displayName,
            "description" to placeDescription,
            "placeImage" to imageDownloadUrl
        )

        if (imageUrl != null){
            // Add a new Tour document with a generated ID
            tourRef.document("/$uniqueId")

            tourRef.add(tour)
                .addOnSuccessListener { documentReference ->
                    documentReference.addSnapshotListener { value, error ->
                        value?.data
                        Log.d("AddTourFragment", "Snapshot error: ${error.toString()}")
                        Toast.makeText(
                            context.requireContext(),
                            error.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Toast.makeText(
                        context.requireContext(),
                        "Tour added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "AddTourFragment",
                        "Tour DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                    context.startActivity(
                        Intent(
                            context.requireActivity(),
                            ToursActivity()::class.java
                        )
                    )
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context.requireContext(),
                        "Error adding document $e",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("AddTourFragment", "Error adding document", e)
                }
        } else {
            Log.d("AddTourFragment", "Image Url is null")
        }

    }

    //TODO : Edit Tour info to firestore.
    fun editTour(
        tourId: String, placeName: String, startDate: String, endDate: String,
        placeDescription: String, selectedBitmapImage: String, context: AddTourFragment
    ) {

        // Create tour with details to be updated
        val tour = hashMapOf(
            "id" to tourId,
            "placeName" to placeName,
            "date" to endDate,
            "description" to placeDescription,
            "placeImage" to selectedBitmapImage
        )

        // Add a new Tour document with a generated ID
        tourRef.document(user?.email.toString())
            .set(tour, SetOptions.merge())
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    context.requireContext(),
                    "Tour updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("AddTourFragment", "Tour DocumentSnapshot updated with ID: $documentReference")
                context.startActivity(
                    Intent(
                        context.requireActivity(),
                        ToursActivity()::class.java
                    )
                )
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context.requireContext(),
                    "Error updating document $e",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("AddTourFragment", "Error updating document", e)
            }
    }

    //TODO : Delete a Tour info from firestore.
    fun deleteATour(tour: Tour?, nav: () -> Unit, context: TourDetailsFragment) {
        val doc = tourRef.document(tour?.id.toString())
//        var tourRef = fbFirestore.collection(Constants.COLLECTION_ADD_TOURS)
//            .whereEqualTo("id", equals(tour.id))
        doc.delete()
            .addOnSuccessListener {
                Toast.makeText(
                    context.requireContext(),
                    "DocumentSnapshot successfully deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                nav()
                Log.d(" ", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context.requireContext(),
                    "Error deleting document ==: $e",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TourDetailsFragment", "Error deleting document", e)
            }
    }

}