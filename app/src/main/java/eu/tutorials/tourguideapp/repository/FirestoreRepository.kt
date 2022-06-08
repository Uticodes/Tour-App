package eu.tutorials.tourguideapp.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import eu.tutorials.tourguideapp.utils.Constants
import eu.tutorials.tourguideapp.utils.Constants.COLLECTION_TOURS
import eu.tutorials.tourguideapp.utils.Constants.COLLECTION_USERS
import eu.tutorials.tourguideapp.models.Tour
import eu.tutorials.tourguideapp.models.User
import eu.tutorials.tourguideapp.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


class FirestoreRepository {
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


    // TODO Step : Implement Login with Firebase.
    suspend fun loginUser(userEmail: String, userPassword: String): Resource<String> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).await()
            addUserDetails()
            Log.d("LoginActivity", "signInUserWithEmail:success")

            Resource.Success("Logged in successfully")
        } catch (error: Exception) {
            Log.d("LoginActivity", "signInUserWithEmail:failure:: ${error.message}")
            Resource.Failure(error.message.toString())
        }
    }

    //TODO : Register a user with Firebase.
    suspend fun registerUser(
        userName: String,
        userEmail: String,
        userPassword: String
    ): Resource<String> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).await()
            Log.d("LoginActivity", "Show UserName:::: $userName")
            //TODO : Save User to Firebase
            saveUserToFirebaseDatabase(userName, userEmail)
            Log.d("LoginActivity", "createUserWithEmail:success")
            Resource.Success("Account successfully created")
        } catch (error: Exception) {
            Log.d("LoginActivity", "createUserWithEmail:failure ${error.message}")
            Resource.Failure(error.message.toString())
        }
    }

    // TODO Step : Implement Add User details to Firestore.
    private suspend fun addUserDetails(): Resource<String> {
        return try {
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
                .set(userMap).await()
            Log.d("AddUserDetails", "User data saved successfully.")

            // TODO Step : Call the getUserInfo function to get the user.
            // START
            getUserInfo()
            // END
            Resource.Success("Account successfully created")
        } catch (error: Exception) {
            Log.d("AddUserDetails", "User data failed to save ${error.message}")
            Resource.Failure(error.message.toString())
        }
    }

    //TODO : Get user info from firestore.
    suspend fun getUserInfo(): Resource<User> {
        return try {
            val userSnapshot = userRef.document(user?.uid.toString()).get().await()
            val userToObject = userSnapshot.toObject(User::class.java)
            saveAUser(userToObject)
            Log.d("LoginActivity", "User metadata:::::: $userToObject")
            Resource.Success(userToObject, "User details gotten successfully")
        } catch (error: Exception) {
            Log.d("ProfileFragment", "Error getting user documents. ::${error.message}")
            Resource.Failure(error.message.toString())
        }
    }

    //TODO : Add User info to firestore.
    private suspend fun saveUserToFirebaseDatabase(
        userName: String,
        email: String
    ): Resource<String> {
        return try {
            val uid = firebaseAuth.uid ?: ""
            val ref = userRef.document("/$uid")
            Log.d("LoginActivity", "Show UserName:::: $userName")
            val user = User(uid, userName, email)

            ref.set(user).await()
            //Update userName on firebase
            updateUserDisplayName(userName)
            Log.d("LoginActivity", "Show UserName:::: $userName")
            Log.d("LoginActivity", "Finally we saved the user to Firebase Database")
            Resource.Success("User details save to Firestore successfully")
        } catch (error: Exception) {
            Log.d("LoginActivity", "Failed to save User to Firestore:: ${error.message}")
            Resource.Failure(error.message.toString())
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
    private suspend fun updateUserDisplayName(userName: String): Resource<String> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            val profileUpdates = UserProfileChangeRequest
                .Builder()
                .setDisplayName(userName).build()
            user?.updateProfile(profileUpdates)?.await()

            Log.d("LoginActivity", "Show UserName:::: $userName")
            Resource.Success("User DisplayName updated  successfully")
        } catch (error: Exception) {
            Log.d("LoginActivity", "Failed to update DisplayName: ${error.message}")
            Resource.Failure(error.message.toString())
        }
    }


    //TODO : Edit Tour info to firestore.
    suspend fun editTour(tour: Tour): Resource<String> {
        return try {
            Log.d("AddTourFragment", "Showing Tour items sent: $tour")
            tourRef.document(tour.id).update(tour.toMap()).await()
            Log.d(
                "AddTourFragment",
                "Tour DocumentSnapshot updated with ID: ${tour.id}"
            )
            Resource.Success(message = "Tour updated successfully")
        } catch (error: Exception) {
            Log.d("AddTourFragment", "Error updating document:: ${error.message}")
            Resource.Failure("Error updating document ${error.message}")
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
    suspend fun deleteATour(docId: String): Resource<String> {
        return try {
            Log.d("TourDetailsFragment", "DocumentSnapshot id::: $docId")

            tourRef.document("/$docId").delete().await()

            Log.d("TourDetailsFragment", "Tour deleted successfully")
            Resource.Success(message = "Tour deleted successfully")
        } catch (error: Exception) {
            Log.d("TourDetailsFragment", error.message.toString())
            Resource.Failure(error.message.toString())
        }
    }


    //TODO : uploadImageToFirebaseStorage.
    suspend fun uploadImageToFirebaseStorageAndAddTour(
        tour: Tour,
        selectedImageUri: Uri,
    ): Resource<String> {
        return try {
            val uploadTask = storageRef.child("tourImages/${firebaseAuth.currentUser?.uid}")
            uploadTask.putFile(selectedImageUri).await()

            uploadTask.downloadUrl.addOnSuccessListener { uri ->
                if (uri.toString().isNotEmpty()) {

                    Log.d(
                        "AddTourFragment",
                        "TourImage link getDownloadImageUrl: $uri"
                    )
                    addTour(tour, uri, result = {})

                } else {
                    Log.d("AddTourFragment", "Image Url is null")
                }
                Log.d("AddTourFragment", "Image File Location:// $uri")
            }
            Resource.Success(message = "")
        } catch (error: Exception) {
            Resource.Failure("Error adding document ${error.message}")
        }
    }

    //TODO : Add Tour info to firestore.
    private fun addTour(
        tour: Tour, selectedImageUri: Uri,
        result: (Resource<Unit>) -> Unit,
    ) {
        Log.d("AddTourFragment", "User display name: ${user?.displayName}")

        tourRef.document("/${tour.id}")
            .set(tour.copy(placeImage = selectedImageUri.toString()))
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "AddTourFragment",
                    "Tour added successfully::"
                )
                result.invoke(Resource.Success(message = "Tour added successfully"))
            }
            .addOnFailureListener { e ->
                Log.d("AddTourFragment", "Error adding a Tour", e)
                result.invoke(Resource.Failure(message = "Failed t added a Tour"))
            }
    }

    suspend fun getTours(toursList: ArrayList<Tour>): Resource<String> {
        return try {
            val response = fbFirestore.collection(COLLECTION_TOURS).get().await()
            // Here we get the list of users in the form of documents.
            for (document in response.documents) {
                val tour = document.toObject(Tour::class.java)
                if (tour != null) {
                    toursList.add(tour)
                }
                Log.d(
                    "TourFragment",
                    "All Tours Info:|:|:|:${document.id} => ${document.data}"
                )
            }
            Log.d("Get All Tours", response.documents.toString())
            Resource.Success(message = "Tours gotten successfully")
        } catch (error: Exception) {
            Log.w("TourFragment", "Error getting all tours. ${error.message}")
            Resource.Failure(error.message.toString())
        }
    }


    suspend fun getUserTourFeeds(toursList: ArrayList<Tour>): Resource<String> {
        return try {
            val response =
                fbFirestore.collection(COLLECTION_TOURS).whereEqualTo("email", user?.email)
                    .get().await()
            // Here we get the list of users in the form of documents.
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
            Log.d("User Tour feeds", response.documents.toString())
            Resource.Success(message = "All Tours gotten successfully")
        } catch (error: Exception) {
            Log.w("ProfileFragment", "Error getting User Tours. ${error.message}")
            Resource.Failure(error.message.toString())
        }
    }

    //TODO : Log out current user from firebase.
    fun logOut(): Resource<String> {
        return try {
            firebaseAuth.signOut()
            Log.d("ProfileFragment", "Logged out successfully")
            Resource.Success(message = "Logged out successfully")
        } catch (error: Exception) {
            Log.d("ProfileFragment", error.message.toString())
            Resource.Failure(error.message.toString())
        }
    }
}