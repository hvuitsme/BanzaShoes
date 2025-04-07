package com.hvuitsme.banzashoes.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import com.hvuitsme.banzashoes.data.repository.AuthRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentSignupBinding
import com.hvuitsme.banzashoes.service.GoogleAuthClient
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleAuthClient: GoogleAuthClient

    companion object {
        fun newInstance() = SignupFragment()
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
//        return inflater.inflate(R.layout.fragment_signup, container, false)
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupBtn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.sendOtp(email)
                val bundle = Bundle().apply {
                    putString("username", username)
                    putString("email", email)
                    putString("password", password)
                    putString("source", "signup")
                }
                findNavController().navigate(R.id.action_signupFragment_to_otpFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
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

        binding.signupToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}