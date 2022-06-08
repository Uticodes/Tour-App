package eu.tutorials.tourguideapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.tourguideapp.utils.Constants.showToast
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.viewModel.ToursViewModel
import eu.tutorials.tourguideapp.databinding.ActivityLoginBinding
import eu.tutorials.tourguideapp.ui.tour.ToursActivity
import eu.tutorials.tourguideapp.utils.Resource
import eu.tutorials.tourguideapp.utils.Validation.isValidEmail
import eu.tutorials.tourguideapp.utils.Validation.isValidName
import eu.tutorials.tourguideapp.utils.Validation.isValidPassword


class LoginActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private var binding: ActivityLoginBinding? = null

    // Declare FirebaseAuth instance
    private var firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userName: String

    //Initialize viewModel
    private val viewModel by lazy {
        ViewModelProvider(this)[ToursViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        toolbar = binding!!.loginToolbar
        setSupportActionBar(toolbar)
        setupInputs()
    }

    private fun setupInputs() {

        binding?.apply {

            //initialize views
            userEmail = binding?.emailEt?.text.toString().trim()
            userPassword = binding?.passwordEt?.text.toString().trim()


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
                binding!!.loginToolbar.title = getString(R.string.login)
            }
            //Hide Login views and show Signup views
            signUpInstruction.setOnClickListener {
                loginHideGroup.visibility = View.VISIBLE
                signUpHideGroup.visibility = View.GONE
                binding!!.loginToolbar.title = getString(R.string.signUp)
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
            fetchUser()
            startActivity(Intent(this@LoginActivity, ToursActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        userEmail = binding?.emailEt?.text.toString().trim()
        userPassword = binding?.passwordEt?.text.toString().trim()
        //Check if email and password is empty
        if (!validateLoginFields()) return
        viewModel.loginUser(userEmail, userPassword)
            .observe(this) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Success -> {
                        hideProgressBar()
                        showToast("Logged in successfully")
                        startActivity(Intent(this, ToursActivity()::class.java))
                    }
                    is Resource.Failure -> {
                        hideProgressBar()
                        showToast(it.message)
                    }
                }
            }

    }

    private fun registerUser() {
        userEmail = binding?.emailEt?.text.toString().trim()
        userPassword = binding?.passwordEt?.text.toString().trim()
        userName = binding?.nameEt?.text.toString().trim()

        if (!validateSignUpFields()) return

        viewModel.registerUser(userName, userEmail, userPassword).observe(this) {
            when (it) {

                is Resource.Loading -> showProgressBar()
                is Resource.Success -> {
                    hideProgressBar()
                    showToast("Account successfully create")
                    startActivity(Intent(this, ToursActivity()::class.java))
                }
                is Resource.Failure -> {
                    hideProgressBar()
                    showToast(it.message)
                }
            }
        }
    }

    private fun fetchUser() { // waiting callback
        viewModel.getUserInfo().observe(this) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    //hideProgressBar()
                }
                is Resource.Failure -> {
                    //hideProgressBar()
                    showToast(it.message)
                }
            }
        }
    }

    private fun validateSignUpFields(): Boolean {
        if (!isValidName(userName.trim())) {
            binding?.nameEt?.error = "Name is required"
            return false
        } else {
            binding?.nameEt?.error = null
        }
        if (!isValidEmail(userEmail.trim())) {
            binding?.emailEt?.error = "Email is Required"
            return false
        } else {
            binding?.emailEt?.error = null
        }
        if (!isValidPassword(userPassword.trim())) {
            binding?.passwordEt?.error = "Password of 4 characters & above Required"
            return false
        } else {
            binding?.passwordEt?.error = null
        }
        return true
    }

    private fun validateLoginFields(): Boolean {
        if (!isValidEmail(userEmail.trim())) {
            binding?.emailEt?.error = "Email is Required"
            return false
        } else {
            binding?.emailEt?.error = null
        }
        if (!isValidPassword(userPassword.trim())) {
            binding?.passwordEt?.error = "Password of 4 characters & above Required"
            return false
        } else {
            binding?.passwordEt?.error = null
        }
        return true
    }


}