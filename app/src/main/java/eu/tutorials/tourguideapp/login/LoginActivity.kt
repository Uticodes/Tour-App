package eu.tutorials.tourguideapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.Toolbar
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.tour.ToursActivity
import eu.tutorials.tourguideapp.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        toolbar = binding!!.loginToolbar
        setSupportActionBar(toolbar)
        setupInputs()
    }

    private fun setupInputs(){
        binding?.apply {

//            email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message, 0, 0, 0)
//            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password, 0, 0, 0)


            email.addTextChangedListener(loginTextWatcher)
            password.addTextChangedListener(loginTextWatcher)

            binding?.loginBtn?.setOnClickListener {
                onLoginButtonClick()
            }

            email.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {

                }

                override fun afterTextChanged(s: Editable)
                {
//                    if (s.length != 0)
//                    {
//                        var drawable = resources.getDrawable(R.drawable.message) //Your drawable image
//                        drawable = DrawableCompat.wrap(drawable!!)
//                        DrawableCompat.setTint(drawable, resources.getColor(R.color.colordarkblue)) // Set whatever color you want
//                        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                        email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                        email.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.message),
//                            null, resources.getDrawable(R.drawable.cancel), null)
//                    }
//                    else if (s.length == 0)
//                    {
//                        email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message,
//                            0, 0, 0)
//                        var drawable = resources.getDrawable(R.drawable.message) //Your drawable image
//                        drawable = DrawableCompat.wrap(drawable!!)
//                        DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
//                        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                        email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                        email.setCompoundDrawablesWithIntrinsicBounds(
//                            resources.getDrawable(R.drawable.message),
//                            null, null, null
//                        )
//                    }
                }
            })

            password.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {

                }

                override fun afterTextChanged(s: Editable)
                {
//                    if (s.length != 0)
//                    {
//                        var drawable = resources.getDrawable(R.drawable.password) //Your drawable image
//                        drawable = DrawableCompat.wrap(drawable!!)
//                        DrawableCompat.setTint(drawable, resources.getColor(R.color.colordarkblue)) // Set whatever color you want
//                        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                        password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                        password.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.password),
//                            null, resources.getDrawable(R.drawable.cancel), null)
//                    }
//                    else if (s.length == 0)
//                    {
//                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,
//                            0, 0, 0)
//                        var drawable = resources.getDrawable(R.drawable.password) //Your drawable image
//                        drawable = DrawableCompat.wrap(drawable!!)
//                        DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
//                        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                        password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                        password.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.password),
//                            null, null, null
//                        )
//                    }
                }
            })

//            email.setOnTouchListener(View.OnTouchListener { v, event ->
//
//                if (event.action == MotionEvent.ACTION_DOWN)
//                {
//                    if (email.getCompoundDrawables().get(2) != null)
//                    {
//                        if (event.x >= email.getRight() - email.getLeft() -
//                            email.getCompoundDrawables().get(2).getBounds().width())
//                        {
//                            if (email.getText().toString() != "")
//                            {
//                                email.setText("")
//                            }
//                        }
//                    }
//                }
//                false
//            })
//
//            password.setOnTouchListener(View.OnTouchListener { v, event ->
//
//                if (event.action == MotionEvent.ACTION_DOWN)
//                {
//                    if (password.getCompoundDrawables().get(2) != null)
//                    {
//                        if (event.x >= password.getRight() - password.getLeft() -
//                            password.getCompoundDrawables().get(2).getBounds().width()
//                        )
//                        {
//                            if (password.getText().toString() != "")
//                            {
//                                password.setText("")
//                            }
//                        }
//                    }
//                }
//                false
//            })

        }

    }


    private val loginTextWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
        {


        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
        {


        }

        override fun afterTextChanged(s: Editable)
        {
            val mUsername: String = binding?.email?.text.toString().trim()
            val mPassword: String = binding?.password?.text.toString().trim()
            val t = !mUsername.isEmpty() && !mPassword.isEmpty()
            if (t)
            {
                binding?.loginBtn?.setBackgroundResource(R.color.purple_700)
            }
            else
            {
                binding?.loginBtn?.setBackgroundResource(R.color.grey)
            }

        }
    }

    override fun onStart()
    {
        super.onStart()
        val mUsername: String = binding?.email?.text.toString().trim()
        val mPassword: String = binding?.password?.text.toString().trim()
        val t = !mUsername.isEmpty() && !mPassword.isEmpty()
        if (t)
        {
            binding?.loginBtn?.setBackgroundResource(R.color.purple_700)
        }
        else
        {
            binding?.loginBtn?.setBackgroundResource(R.color.grey)
        }
    }

    /* Opens TourDetailsActivity when RecyclerView item is clicked. */
    private fun onLoginButtonClick() {
        val intent = Intent(this, ToursActivity()::class.java)
        //intent.putExtra(TOUR_ID, tour.id)
        startActivity(intent)
    }

}

//
//package com.example.loginapp
//
//import android.graphics.PorterDuff
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.MotionEvent
//import android.view.View
//import androidx.appcompat.widget.Toolbar
//import androidx.core.graphics.drawable.DrawableCompat
//import kotlinx.android.synthetic.main.activity_main.*

//class MainActivity : AppCompatActivity() {
//
//
//    private lateinit var toolbar: Toolbar
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//        toolbar = findViewById(R.id.toolbar_login)
//        setSupportActionBar(toolbar)
//
//        email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message, 0, 0, 0)
//        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password, 0, 0, 0)
//
//
//        email.addTextChangedListener(loginTextWatcher)
//        password.addTextChangedListener(loginTextWatcher)
//
//
//        email.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
//            {
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
//            {
//
//            }
//
//            override fun afterTextChanged(s: Editable)
//            {
//                if (s.length != 0)
//                {
//                    var drawable = resources.getDrawable(R.drawable.message) //Your drawable image
//                    drawable = DrawableCompat.wrap(drawable!!)
//                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colordarkblue)) // Set whatever color you want
//                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                    email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                    email.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.message),
//                        null, resources.getDrawable(R.drawable.cancel), null)
//                }
//                else if (s.length == 0)
//                {
//                    email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message,
//                        0, 0, 0)
//                    var drawable = resources.getDrawable(R.drawable.message) //Your drawable image
//                    drawable = DrawableCompat.wrap(drawable!!)
//                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
//                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                    email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                    email.setCompoundDrawablesWithIntrinsicBounds(
//                        resources.getDrawable(R.drawable.message),
//                        null, null, null
//                    )
//                }
//            }
//        })
//
//        password.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
//            {
//
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
//            {
//
//            }
//
//            override fun afterTextChanged(s: Editable)
//            {
//                if (s.length != 0)
//                {
//                    var drawable = resources.getDrawable(R.drawable.password) //Your drawable image
//                    drawable = DrawableCompat.wrap(drawable!!)
//                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colordarkblue)) // Set whatever color you want
//                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                    password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                    password.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.password),
//                        null, resources.getDrawable(R.drawable.cancel), null)
//                }
//                else if (s.length == 0)
//                {
//                    password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,
//                        0, 0, 0)
//                    var drawable = resources.getDrawable(R.drawable.password) //Your drawable image
//                    drawable = DrawableCompat.wrap(drawable!!)
//                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
//                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
//                    password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
//                    password.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.password),
//                        null, null, null
//                    )
//                }
//            }
//        })
//
//        email.setOnTouchListener(View.OnTouchListener { v, event ->
//
//            if (event.action == MotionEvent.ACTION_DOWN)
//            {
//                if (email.getCompoundDrawables().get(2) != null)
//                {
//                    if (event.x >= email.getRight() - email.getLeft() -
//                        email.getCompoundDrawables().get(2).getBounds().width())
//                    {
//                        if (email.getText().toString() != "")
//                        {
//                            email.setText("")
//                        }
//                    }
//                }
//            }
//            false
//        })
//
//        password.setOnTouchListener(View.OnTouchListener { v, event ->
//
//            if (event.action == MotionEvent.ACTION_DOWN)
//            {
//                if (password.getCompoundDrawables().get(2) != null)
//                {
//                    if (event.x >= password.getRight() - password.getLeft() -
//                        password.getCompoundDrawables().get(2).getBounds().width()
//                    )
//                    {
//                        if (password.getText().toString() != "")
//                        {
//                            password.setText("")
//                        }
//                    }
//                }
//            }
//            false
//        })
//
//        remember_password.setOnClickListener(View.OnClickListener {
//
//            if (!(remember_password.isSelected)) {
//                remember_password.isChecked = true
//                remember_password.isSelected = true
//            } else {
//                remember_password.isChecked = false
//                remember_password.isSelected = false
//            }
//        })
//    }
//
//    private val loginTextWatcher: TextWatcher = object : TextWatcher {
//
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
//        {
//
//
//        }
//
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
//        {
//
//
//        }
//
//        override fun afterTextChanged(s: Editable)
//        {
//            val mUsername: String = email.getText().toString().trim()
//            val mPassword: String = password.getText().toString().trim()
//            val t = !mUsername.isEmpty() && !mPassword.isEmpty()
//            if (t)
//            {
//                login_button.setBackgroundResource(R.color.colordarkblue)
//            }
//            else
//            {
//                login_button.setBackgroundResource(R.color.colorwhiteblueshade)
//            }
//
//        }
//    }
//
//    override fun onStart()
//    {
//        super.onStart()
//        val mUsername: String = email.getText().toString().trim()
//        val mPassword: String = password.getText().toString().trim()
//        val t = !mUsername.isEmpty() && !mPassword.isEmpty()
//        if (t)
//        {
//            login_button.setBackgroundResource(R.color.colordarkblue)
//        }
//        else
//        {
//            login_button.setBackgroundResource(R.color.colorwhiteblueshade)
//        }
//    }
//}