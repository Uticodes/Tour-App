package eu.tutorials.tourguideapp.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.tutorials.Constants
import eu.tutorials.tourguideapp.FirestoreImplementations
import eu.tutorials.tourguideapp.R
import eu.tutorials.tourguideapp.data.Tour
import eu.tutorials.tourguideapp.data.User
import eu.tutorials.tourguideapp.databinding.FragmentProfileBinding
import eu.tutorials.tourguideapp.databinding.FragmentToursBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val TAG = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null
    private var user: User? = null
    protected val appUser = Constants.sessionUser
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = user
        Log.d(TAG, "User Info =|=|===:: ${user?.name}")

        binding.apply {
            emailEt.setText(appUser?.email)
            nameEt.setText(appUser?.name)
        }

    }

    override fun onStart() {
        super.onStart()
        FirestoreImplementations().getUserInfo()
    }
}