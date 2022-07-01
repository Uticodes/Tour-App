package eu.tutorials.tourguideapp.ui.tour

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.databinding.ActivityToursBinding

class ToursActivity : AppCompatActivity() {
    private val TAG = "ToursActivity"

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityToursBinding
    lateinit var startActivityLaunch: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityToursBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_tour_details)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //TODO Show a dialog for READ_EXTERNAL_STORAGE permission
        startActivityLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                ////Get the data returned by intent
                val resultCode = it.resultCode
                val data = it.data
            }

        checkPermissions()

    }

    //TODO Checks for READ_EXTERNAL_STORAGE permission
    private fun checkPermissions() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Storage permission granted")
            } else {
                Log.d(TAG, "Storage permission denied")
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            /*Requests permissions to be granted to this application. These permissions
            must be requested in your manifest, they should not be granted to your app,
            and they should have protection level*/
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_tour_details)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}