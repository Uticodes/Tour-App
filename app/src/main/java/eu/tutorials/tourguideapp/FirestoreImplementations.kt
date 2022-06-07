package eu.tutorials.tourguideapp

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import eu.tutorials.Constants
import eu.tutorials.Constants.COLLECTION_TOURS
import eu.tutorials.Constants.COLLECTION_USERS
import eu.tutorials.tourguideapp.adapter.ToursAdapter
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.data.User
import eu.tutorials.tourguideapp.data.UserTour
import eu.tutorials.tourguideapp.login.LoginActivity
import eu.tutorials.tourguideapp.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList


class FirestoreImplementations {
    // Declare FirebaseFirestore instance
    private var fbFirestore = FirebaseFirestore.getInstance()

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    private val savedUserHandler = Constants
    private val savedUser = savedUserHandler.savedUser

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
//    private var activityContext = Activity()
//    private var lContext = LoginActivity()

//    var pref: SharedPreferences = lContext.getSharedPreferences(Constants.PREF_KEY, MODE_PRIVATE)
//    var editor: SharedPreferences.Editor = pref.edit()

    // TODO Step : Implement Firebase signIn.
    fun signInUser(context: Activity, userEmail: String, userPassword: String, result: (Resource<Unit>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(context) { task ->
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

    suspend fun registerrUser(
        userName: String,
        userEmail: String,
        userPassword: String,
        result: (Resource<Unit>) -> Unit,
        context: Activity,): Resource<String> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).await()
            Resource.Success("Account successfully created")
        } catch (e: Exception) {
            Resource.Failure(e.message!!)
        }
    }

    //TODO : Register a user with Firebase.
    fun registerUser(
        userName: String,
        userEmail: String,
        userPassword: String,
        result: (Resource<Unit>) -> Unit,
        context: Activity,
    ) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(context) { task ->
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

                Log.d("AddUserDetails", "User data saved successfully.")
                // TODO Step : Call the getUserInfo function to get the user.
                // START
                getUserInfo()
                // END
            }
            .addOnFailureListener { e ->
                Log.d("AddUserDetails", "User data failed to save $e")
            }
    }

    //TODO : Get user info from firestore.
    fun getUserInfo() {
        userRef.document(user?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val userToObject = result.toObject(User::class.java)
                saveAUser(userToObject)

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
    private fun saveAUser(user: User?) {
        CoroutineScope(Dispatchers.Main).launch {
            if (user != null) {
                savedUserHandler.initSession(user)
            }
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
        tour: Tour,
//       isUploadNewImage: Boolean,
//       selectedImageUri: Uri,
        result: (Resource<Unit>) -> Unit
    ) {

        Log.d("AddTourFragment", "Showing Tour items sent: $tour")

        //if (isUploadNewImage){
        // Add a new Tour document with a generated ID
        tourRef.document(tour.id)
            .update(tour.toMap())
            .addOnSuccessListener { documentReference ->
                result.invoke(Resource.Success(message = "Tour updated successfully"))
                Log.d(
                    "AddTourFragment",
                    "Tour DocumentSnapshot updated with ID: ${tour.id}, snapshotId::|:: $documentReference"
                )
            }
            .addOnFailureListener { e ->
                result.invoke(Resource.Failure("Error updating document $e"))
                Log.d("AddTourFragment", "Error updating document", e)
            }
//        } else {
//            Log.d("AddTourFragment", "Added new image on EditTour: ${tour.placeImage}")

        // Add a new Tour document with a generated ID
//            tourRef.document(tour.id)
//                .update(tour.toMap())
//                .addOnSuccessListener { documentReference ->
//                    result.invoke(Resource.Success(message = "Tour updated successfully"))
//                    Log.d("AddTourFragment", "Tour DocumentSnapshot updated with ID: ${tour.id}, snapshotId::|:: $documentReference")
//                }
//                .addOnFailureListener { e ->
//                    result.invoke(Resource.Failure("Error updating document $e"))
//                    Log.d("AddTourFragment", "Error updating document", e)
//                }
//            Log.d(
//                "AddTourFragment",
//                "TourImage link getSelectedImageUrl inside FbImp: $selectedImageUri"
//            )
//            uploadImageToFirebaseStorage(selectedImageUri) { imageUrl ->
//
//                if (imageUrl.toString().isNotEmpty()) {
//
//                    Log.d(
//                        "AddTourFragment",
//                        "TourImage link getDownloadImageUrl inside if-else: ${imageUrl}"
//                    )
//                    // Add a new Tour document with a generated ID
//                    tourRef.document(tour.id)
//                        .update(tour.copy(placeImage  = imageUrl.toString()).toMap())
//                        .addOnSuccessListener { documentReference ->
//                            result.invoke(Resource.Success(message = "Tour updated successfully"))
//                            Log.d("AddTourFragment", "Tour DocumentSnapshot updated with ID: ${tour.id}, snapshotId::|:: $documentReference")
//                        }
//                        .addOnFailureListener { e ->
//                            result.invoke(Resource.Failure("Error updating document $e"))
//                            Log.d("AddTourFragment", "Error updating document", e)
//                        }
//                } else {
//                    result.invoke(Resource.Failure("Image Url is null"))
//                    Log.d("AddTourFragment", "Image Url is null")
//                }
//
//            }
//
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
    private fun uploadImageToFirebaseStorage(
        selectedImageUri: Uri,
        getDownloadImageUrl: (Uri) -> Unit
    ) {

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
        tour: Tour, selectedImageUri: Uri, result: (Resource<Unit>) -> Unit
    ) {

        Log.d("AddTourFragment", "User display name: ${user?.displayName}")

        uploadImageToFirebaseStorage(selectedImageUri) { imageUrl ->

            if (imageUrl.toString().isNotEmpty()) {

                Log.d(
                    "AddTourFragment",
                    "TourImage link getDownloadImageUrl inside if-else: ${imageUrl}"
                )
                // Add a new Tour document with a generated ID
                tourRef.document("/${tour.id}")
                    .set(tour.copy(placeImage = imageUrl.toString()))
                    .addOnSuccessListener { documentReference ->
                        result.invoke(Resource.Success(message = "Tour uploaded successfully"))
                        Log.d(
                            "AddTourFragment",
                            "Tour uploaded successfully:: ${documentReference}"
                        )
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


    fun getUserTourFeeds(toursList: ArrayList<Tour>, result: (Resource<Unit>) -> Unit) {
        fbFirestore.collection(COLLECTION_TOURS).whereEqualTo("email", user?.email)
            .get()
            .addOnSuccessListener { response ->
                // Hide progressbar
                result.invoke(Resource.Success(message = "Tour uploaded successfully"))
                // Here we get the list of users in the form of documents.
                Log.d("User Tour feeds", response.documents.toString())

                for (document in response.documents) {
                    val tour = document.toObject(Tour::class.java)
                    if (tour != null) {
                        toursList.add(tour)
                    }
                    Log.d(
                        "ProfileFragment",
                        "User Tour Feeds Info:|:|:|:${document.id} => ${document.data}"
                    )
                }
            }
            .addOnFailureListener { exception ->
                result.invoke(Resource.Failure("Error adding document $exception"))

                Log.w("ProfileFragment", "Error getting documents.", exception)
            }

    }

}