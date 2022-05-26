package eu.tutorials.tourguideapp.login

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.databinding.ActivityLoginBinding
import eu.tutorials.tourguideapp.tour.ToursActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private var binding: ActivityLoginBinding? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = LoginActivity::class.java.simpleName

//    val t = userEmail.isNotEmpty() && userPassword.isNotEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()

        setContentView(binding?.root)
        toolbar = binding!!.loginToolbar
        setSupportActionBar(toolbar)
        setupInputs()
    }

    private fun setupInputs() {

        binding?.apply {

            //initialize views
            var userEmail = binding?.emailEt?.text.toString().trim()
            var userPassword = binding?.passwordEt?.text.toString().trim()

            emailEt.addTextChangedListener(loginTextWatcher)
            passwordEt.addTextChangedListener(loginTextWatcher)

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


    private val loginTextWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


        }

        override fun afterTextChanged(s: Editable) {
//            if (t) {
//                binding?.apply {
//                    loginBtn.isEnabled = true
//                    signUpBtn.isEnabled = true
//                }
//            } else {
//                binding?.apply {
//                    loginBtn.isEnabled = false
//                    signUpBtn.isEnabled = false
//                }
//            }

        }
    }

    override fun onStart() {
        super.onStart()
        //updateUI(currentUser);
        mAuth?.currentUser?.let {
            Intent(this@LoginActivity, ToursActivity::class.java).apply {
                startActivity(this)
            }
        }

//        if (t) {
//            binding?.apply {
//                loginBtn.isEnabled = true
//                signUpBtn.isEnabled = true
//            }
//
//        } else {
//            binding?.apply {
//                loginBtn.isEnabled = false
//                signUpBtn.isEnabled = false
//            }
//        }
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
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //updateFirebaseUserDisplayName()
                        Log.d(TAG, "signInUserWithEmail:success")
                        val intent = Intent(this, ToursActivity()::class.java)
                        //intent.putExtra(TOUR_ID, tour.id)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "An error has occurred during login. ${task.exception}, Please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.w(TAG, "signInUserWithEmail:failure", task.exception)
                    }
                }
        }


    }

    private fun signUpUser() {
        val userEmail = binding?.emailEt?.text.toString().trim()
        val userPassword = binding?.passwordEt?.text.toString().trim()

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(
                this,
                "Please make sure to fill in your email and password",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //updateFirebaseUserDisplayName()
                        Log.d(TAG, "createUserWithEmail:success")
                        val intent = Intent(this, ToursActivity()::class.java)
                        //intent.putExtra(TOUR_ID, tour.id)
                        startActivity(intent)
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this,
                            "An error has occurred during signup. ${task.exception}, Please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

}