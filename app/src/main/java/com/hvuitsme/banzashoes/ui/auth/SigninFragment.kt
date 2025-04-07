package com.hvuitsme.banzashoes.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvuitsme.admin.AdminMainActivity
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import com.hvuitsme.banzashoes.data.repository.AuthRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentSigninBinding
import com.hvuitsme.banzashoes.service.GoogleAuthClient
import kotlinx.coroutines.launch

class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleAuthClient: GoogleAuthClient

    companion object {
        fun newInstance() = SigninFragment()
    }

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AuthRepoImpl(AuthDataSource())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        googleAuthClient = GoogleAuthClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_login, container, false)
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginToolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.signinBtn.setOnClickListener {
            val identifier = binding.etUsernameEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (identifier.equals("admin", true) && password == "68686868"){
//                startActivity(Intent(requireContext(), AdminMainActivity::class.java))
                val intent = Intent(requireContext(), AdminMainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                return@setOnClickListener
            }
            if (identifier.isNotEmpty() && password.isNotEmpty()) {
                viewModel.sendOtp(identifier)
                val bundle = Bundle().apply {
                    putString("email", identifier)
                    putString("password", password)
                    putString("source", "signin")
                }
                findNavController().navigate(R.id.action_signinFragment_to_otpFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Please fill in all information", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleBtn.setOnClickListener {
            lifecycleScope.launch {
                val tokenCredential = googleAuthClient.getGoogleIdTokenCredential()
                if (tokenCredential != null) {
                    val googleEmail = tokenCredential.id
                    val prefs = requireContext().getSharedPreferences("APP_PREFS", android.content.Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("GOOGLE_ID_TOKEN", tokenCredential.idToken)
                        .putBoolean("OTP_VERIFIED", false)
                        .apply()
                    viewModel.sendOtp(googleEmail)
                    val bundle = Bundle().apply {
                        putString("email", googleEmail)
                        putString("password", "")
                        putString("source", "signin")
                        putString("auth_type", "google")
                    }
                    findNavController().navigate(R.id.action_signinFragment_to_otpFragment, bundle)
                } else {
                    Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signupBtn.setOnClickListener {
            val navOption = navOptions {
                anim {
                    enter = R.anim.slide_in_from_right
                    exit = R.anim.slide_out_to_left
                    popEnter = R.anim.pop_slide_in_from_left
                    popExit = R.anim.pop_slide_out_from_right
                }
            }
            findNavController().navigate(R.id.action_signinFragment_to_signupFragment, null, navOption)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}