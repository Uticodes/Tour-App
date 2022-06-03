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
import eu.tutorials.tourguideapp.utils.Resource
import eu.tutorials.tourguideapp.utils.SharedPrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class FirestoreImplementations() {
    // Declare FirebaseFirestore instance
    private var fbFirestore = FirebaseFirestore.getInstance()

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    private val sessionHandler = Constants

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



    // Create context
    var activityContext = Activity()
    var lContext = LoginActivity()

//    var pref: SharedPreferences = lContext.getSharedPreferences(Constants.PREF_KEY, MODE_PRIVATE)
//    var editor: SharedPreferences.Editor = pref.edit()

    // TODO Step : Implement Firebase signIn.
    fun signInUser(userEmail: String, userPassword: String, result: (Resource<Unit>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(activityContext) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Log.d(
                        "LoginActivity",
                        "User Info:::::: ${user?.email}:::${user?.displayName}:::${user?.uid}"
                    )

                    addUserDetails()
                    result.invoke(Resource.Success(message = "Tour deleted successfully"))
                    Log.d("LoginActivity", "signInUserWithEmail:success")
                } else {
                    result.invoke(Resource.Failure("An error occurred during login. ${task.exception}, Please try again later."))
                    Log.d("LoginActivity", "signInUserWithEmail:failure", task.exception)
                }
            }
    }


    //TODO : Register a user with Firebase.
    fun registerUser(
        userName: String,
        userEmail: String,
        userPassword: String,
        result: (Resource<Unit>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(activityContext) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Show UserName:::: $userName")
                    saveUserToFirebaseDatabase(userName, userEmail)
                    Log.d("LoginActivity", "createUserWithEmail:success")
                    result.invoke(Resource.Success(message = "Tour deleted successfully"))
                } else {
                    result.invoke(Resource.Failure("An error occurred during registration. ${task.exception}, Please try again later."))
                    Log.d("LoginActivity", "createUserWithEmail:failure", task.exception)
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
                val userToObject = result.toObject(User::class.java)
                updateSessionUser(userToObject)

                Log.d("LoginActivity", "User metadata:::::: ${result.metadata}")

//                val userInfo = User(
//                    user?.uid.toString(),
//                    user?.displayName.toString(),
//                    user?.email.toString(),
//                )
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
                }
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "Failed to set value to database: ${it.message}")
            }
    }

    // Get Session User Listens,
    private fun updateSessionUser(user: User?) {
        CoroutineScope(Dispatchers.Main).launch {
            sessionHandler.initSession(user!!)
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



    //TODO : Edit Tour info to firestore.
    fun editTour(
        tourId: String, docId: String, placeName: String, placeDescription: String,
        selectedBitmapImage: Uri, result: (Resource<Unit>) -> Unit
    ) {

        // Create tour with details to be updated
        val tour = mapOf(
            "id" to tourId,
            "placeName" to placeName,
            "date" to Timestamp(Date()),
            "authorsName" to user?.displayName,
            "description" to placeDescription,
            "placeImage" to selectedBitmapImage
        )

        // Add a new Tour document with a generated ID
        tourRef.document("/$docId")
            .set(tour)
            .addOnSuccessListener { documentReference ->
                result.invoke(Resource.Success(message = "Tour updated successfully"))
                Log.d("AddTourFragment", "Tour DocumentSnapshot updated with ID: $docId, snapshotId::|:: $documentReference")
            }
            .addOnFailureListener { e ->
                result.invoke(Resource.Failure("Error updating document $e"))
                Log.d("AddTourFragment", "Error updating document", e)
            }


        //Upload image to firebase and get download url
//        uploadImageToFirebaseStorage(selectedBitmapImage) { imageUrl ->
//
//            if (imageUrl.toString().isNotEmpty()) {
//                // Create a new tour details
//                val tour = hashMapOf(
//                    "placeName" to placeName,
//                    "description" to placeDescription,
//                    "placeImage" to imageUrl.toString()
//                )
//
//                Log.d(
//                    "AddTourFragment",
//                    "TourImage link getDownloadImageUrl inside if-else: ${imageUrl}"
//                )
//                // Add a new Tour document with a generated ID
//                tourRef.document("/$docId")
//
//                .set(tour)
//                    .addOnSuccessListener { documentReference ->
//                        result.invoke(Resource.Success(message = "Tour updated successfully"))
//                        Log.d("AddTourFragment", "Tour DocumentSnapshot updated with ID: $documentReference")
//                    }
//                    .addOnFailureListener { e ->
//                        result.invoke(Resource.Failure("Error updating document $e"))
//                        Log.d("AddTourFragment", "Error updating document", e)
//                    }
//            } else {
//                result.invoke(Resource.Failure("Image Url is null"))
//                Log.d("AddTourFragment", "Image Url is null")
//            }
//        }
    }

    //TODO : Delete a Tour info from firestore.
    fun deleteATour(docId: String, result: (Resource<Unit>) -> Unit) {
        Log.d("TourDetailsFragment", "DocumentSnapshot id::: $docId")

        tourRef.document("/$docId")
        .delete()
            .addOnSuccessListener {
                result.invoke(Resource.Success(message = "Tour deleted successfully"))
                Log.d("TourDetailsFragment", "Tour deleted successfully")
            }
            .addOnFailureListener { e ->
                result.invoke(Resource.Failure("Error deleting document $e"))
                Log.d("TourDetailsFragment", "Error deleting document", e)
            }
    }


    //TODO : uploadImageToFirebaseStorage.
    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri, getDownloadImageUrl: (Uri) -> Unit) {

        val uploadTask = storageRef.child("tourImages/${firebaseAuth.currentUser?.uid}")
        uploadTask.putFile(selectedImageUri).addOnSuccessListener { it ->
            uploadTask.downloadUrl.addOnSuccessListener { uri ->
                getDownloadImageUrl(uri)
                Log.d("AddTourFragment", "Image File Location:// $uri")
            }
            Log.d("AddTourFragment", "Successfully upload image: ${it.metadata?.path}")
        }.addOnFailureListener { e ->
            Log.d("AddTourFragment", "Failed to upload image $e")
        }
    }

    //TODO : Add Tour info to firestore.
    fun addTour(
        placeName: String, placeDescription: String,
        selectedImageUri: Uri, result: (Resource<Unit>) -> Unit
    ) {

        val uniqueId = UUID.randomUUID().toString()
        Log.d("AddTourFragment", "User display name: ${user?.displayName}")

        //Upload image to firebase and get download url
        uploadImageToFirebaseStorage(selectedImageUri) { imageUrl ->

            if (imageUrl.toString().isNotEmpty()) {
                // Create a new tour details
                val tour = hashMapOf(
                    "id" to uniqueId,
                    "placeName" to placeName,
                    "date" to Timestamp(Date()),
                    "authorsName" to user?.displayName,
                    "description" to placeDescription,
                    "placeImage" to imageUrl.toString()
                )
                Log.d(
                    "AddTourFragment",
                    "TourImage link getDownloadImageUrl inside if-else: ${imageUrl}"
                )
                // Add a new Tour document with a generated ID
                tourRef.document("/$uniqueId")

                tourRef.add(tour)
                    .addOnSuccessListener { documentReference ->
                        result.invoke(Resource.Success(message = "Tour uploaded successfully"))
                        Log.d("AddTourFragment", "Tour uploaded successfully:: ${documentReference}"  )
                    }
                    .addOnFailureListener { e ->
                        result.invoke(Resource.Failure("Error adding document $e"))
                        Log.d("AddTourFragment", "Error adding a Tour", e)
                    }
            } else {
                result.invoke(Resource.Failure("Image Url is null"))
                Log.d("AddTourFragment", "Image Url is null")
            }

        }

    }

}