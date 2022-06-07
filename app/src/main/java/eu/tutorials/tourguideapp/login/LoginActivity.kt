package eu.tutorials.tourguideapp.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import eu.tutorials.Constants
import eu.tutorials.Constants.showToast
import eu.tutorials.tourguideapp.FirestoreImplementations
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.databinding.ActivityLoginBinding
import eu.tutorials.tourguideapp.tour.ToursActivity
import eu.tutorials.tourguideapp.utils.Resource
import eu.tutorials.tourguideapp.utils.SharedPrefUtils


class LoginActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private var binding: ActivityLoginBinding? = null
    private val TAG = LoginActivity::class.java.simpleName

    // Declare FirebaseFirestore instance
    private var fbFirestore = FirebaseFirestore.getInstance()

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    // Declare Firebase storage
    private val storage = Firebase.storage

    // Get Firebase current user
    var user = firebaseAuth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

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
                registerUser()
            }

            //Hide Signup views and show login views
            loginInstruction.setOnClickListener {
                loginHideGroup.visibility = View.GONE
                signUpHideGroup.visibility = View.VISIBLE
            }
            //Hide Login views and show Signup views
            signUpInstruction.setOnClickListener {
                loginHideGroup.visibility = View.VISIBLE
                signUpHideGroup.visibility = View.GONE
                binding!!.loginToolbar.title = getString(R.string.author)
            }

            emailEt.apply {
                setOnEditorActionListener { _, actionId, keyEvent ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent == null ||
                        keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        userEmail = text.toString()
                    }
                    false
                }

                setOnFocusChangeListener { view, gainedFoucs ->
                    userEmail = text.toString()
                }
            }

            passwordEt.apply {
                setOnEditorActionListener { _, actionId, keyEvent ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent == null ||
                        keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        userPassword = text.toString()
                    }
                    false
                }

                setOnFocusChangeListener { view, gainedFoucs ->
                    userPassword = text.toString()
                }
            }

        }

    }

    private fun hideProgressBar() {
        binding?.apply {
            loginProgressBar.visibility = View.INVISIBLE
            loginBtn.isEnabled = true
            signUpBtn.isEnabled = true
        }
    }

    private fun showProgressBar() {
        binding?.apply {
            loginProgressBar.visibility = View.VISIBLE
            loginBtn.isEnabled = false
            signUpBtn.isEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.let {
            FirestoreImplementations().getUserInfo()
            startActivity(Intent(this@LoginActivity, ToursActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val userEmail = binding?.emailEt?.text.toString().trim()
        val userPassword = binding?.passwordEt?.text.toString().trim()
        //Check if email and password is empty
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            showToast("Please make sure to fill in your email and password")
        } else {

            FirestoreImplementations().signInUser(
                this,
                userEmail, userPassword,
                result = { result ->
                    when (result) {
                        is Resource.Loading -> showProgressBar()
                        is Resource.Success -> {
                            hideProgressBar()
                            showToast("Logged in successfully")
                            startActivity(Intent(this, ToursActivity()::class.java))
                        }
                        is Resource.Failure -> {
                            hideProgressBar()
                            showToast(result.message)
                        }
                    }
                }
            )
        }

    }

    private fun registerUser() {
        val userEmail = binding?.emailEt?.text.toString().trim()
        val userPassword = binding?.passwordEt?.text.toString().trim()
        val userName = binding?.nameEt?.text.toString().trim()

        if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty()) {
            showToast("Please make sure to fill in your email and password")
        } else {

            FirestoreImplementations().registerUser(
                userName,
                userEmail,
                userPassword,
                result = { result ->
                    when (result) {
                        is Resource.Loading -> showProgressBar()
                        is Resource.Success -> {
                            hideProgressBar()
                            showToast("Registered successfully")
                            startActivity(Intent(this, ToursActivity()::class.java))
                        }
                        is Resource.Failure -> {
                            hideProgressBar()
                            showToast(result.message)
                        }
                    }
                },
                this
            )
        }
    }


}