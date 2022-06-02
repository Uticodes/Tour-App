package eu.tutorials.tourguideapp.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import eu.tutorials.Constants
import eu.tutorials.tourguideapp.FirestoreImplementations
import eu.tutorials.tourguideapp.databinding.ActivityLoginBinding
import eu.tutorials.tourguideapp.tour.ToursActivity
import eu.tutorials.tourguideapp.utils.SharedPrefUtils


class LoginActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private var binding: ActivityLoginBinding? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = LoginActivity::class.java.simpleName
    // Declare FirebaseFirestore instance
    private var fbFirestore = FirebaseFirestore.getInstance()

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    // Declare Firebase storage
    private val storage = Firebase.storage

    // Create a storage reference from our app
    private val storageRef = storage.reference

    // Declare User collection reference
    private val userRef = fbFirestore.collection(Constants.COLLECTION_USERS)

    // Declare Tour collection reference
    private val tourRef = fbFirestore.collection(Constants.COLLECTION_TOURS)

    // Get Firebase current user
    var user = firebaseAuth.currentUser

//    val t = userEmail.isNotEmpty() && userPassword.isNotEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()

        setContentView(binding?.root)
        toolbar = binding!!.loginToolbar
        setSupportActionBar(toolbar)
        SharedPrefUtils(this)
        setupInputs()
    }

    private fun setupInputs() {

        binding?.apply {

            //initialize views
            var userEmail = binding?.emailEt?.text.toString().trim()
            var userPassword = binding?.passwordEt?.text.toString().trim()


            //Perform login
            loginBtn.setOnClickListener {
                loginUser()
            }
            //Perform signUp
            signUpBtn.setOnClickListener {
                signUpUser()
            }

            emailEt.apply {
                setOnEditorActionListener {_, actionId, keyEvent ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent == null ||
                        keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        userEmail = text.toString()
                    }
                    false
                }

                setOnFocusChangeListener {view, gainedFoucs ->
                    userEmail = text.toString()
                }
            }

            passwordEt.apply {
                setOnEditorActionListener {_, actionId, keyEvent ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent == null ||
                        keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        userPassword = text.toString()
                    }
                    false
                }

                setOnFocusChangeListener {view, gainedFoucs ->
                    userPassword = text.toString()
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()
        mAuth?.currentUser?.let {
            FirestoreImplementations().getUserInfo()
            Intent(this@LoginActivity, ToursActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun loginUser() {
        val userEmail = binding?.emailEt?.text.toString().trim()
        val userPassword = binding?.passwordEt?.text.toString().trim()
        //Check if email and password is empty
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(
                this,
                "Please make sure to fill in your email and password",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            FirestoreImplementations().signInUser(
                userEmail, userPassword,
                intent = {
                    startActivity(Intent(this, ToursActivity()::class.java))
                }
            )
        }

    }

    private fun signUpUser() {
        val userEmail = binding?.emailEt?.text.toString().trim()
        val userPassword = binding?.passwordEt?.text.toString().trim()
        val userName = binding?.nameEt?.text.toString().trim()

        if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty()) {
            Toast.makeText(
                this,
                "Please make sure to fill in your email and password",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            FirestoreImplementations().registerUser(
                this,
                userName,
                userEmail,
                userPassword,
                intent = {
                    startActivity(Intent(this, ToursActivity()::class.java))
                }
            )
        }
    }

}