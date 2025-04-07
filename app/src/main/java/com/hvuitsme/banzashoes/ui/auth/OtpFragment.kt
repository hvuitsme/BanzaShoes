package com.hvuitsme.banzashoes.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import com.hvuitsme.banzashoes.data.repository.AuthRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentOtpBinding
import com.hvuitsme.banzashoes.service.EmailSenderService

class OtpFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private var userEmail: String = ""
    private var source: String = "signup"

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AuthRepoImpl(AuthDataSource())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        userEmail = arguments?.getString("email") ?: ""
        source = arguments?.getString("source") ?: "signup"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpInputs(
            binding.otpBox1,
            binding.otpBox2,
            binding.otpBox3,
            binding.otpBox4,
            binding.otpBox5,
            binding.otpBox6
        )

        binding.signupBtn.setOnClickListener {
            val otp = binding.otpBox1.text.toString() +
                    binding.otpBox2.text.toString() +
                    binding.otpBox3.text.toString() +
                    binding.otpBox4.text.toString() +
                    binding.otpBox5.text.toString() +
                    binding.otpBox6.text.toString()

            if (source == "signup") {
                val username = arguments?.getString("username") ?: ""
                val email = arguments?.getString("email") ?: ""
                val password = arguments?.getString("password") ?: ""
                val newUser = User(username = username, email = email, password = password)
                viewModel.verifyOtpAndSignUp(newUser, otp)
            } else if (source == "signin") {
                val authType = arguments?.getString("auth_type") ?: ""
                if (authType == "google") {
                    if (EmailSenderService.verifyOtp(otp)) {
                        val prefs = requireContext().getSharedPreferences("APP_PREFS", android.content.Context.MODE_PRIVATE)
                        val googleIdToken = prefs.getString("GOOGLE_ID_TOKEN", null)
                        if (googleIdToken != null) {
                            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(authCredential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        prefs.edit().putBoolean("OTP_VERIFIED", true)
                                            .remove("GOOGLE_ID_TOKEN")
                                            .apply()
                                        Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show()
                                        findNavController().previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("SIGN_IN_RESULT", true)
                                        findNavController().popBackStack(R.id.homeFragment, false)
                                    } else {
                                        Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), "Missing Google token", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val email = arguments?.getString("email") ?: ""
                    val password = arguments?.getString("password") ?: ""
                    viewModel.verifyOtpAndSignIn(email, password, otp)
                }
            }
        }

        viewModel.signUpResult.observe(viewLifecycleOwner) { resultPair ->
            val (result, errorMsg) = resultPair
            if (result) {
                Toast.makeText(requireContext(), "Sign up successful", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.signinFragment, false)
            } else {
                Toast.makeText(requireContext(), errorMsg ?: "Sign up failed", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.signInResult.observe(viewLifecycleOwner) { resultPair ->
            val (result, errorMsg) = resultPair
            if (result) {
                Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show()
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("SIGN_IN_RESULT", true)
                findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(requireContext(), errorMsg ?: "Sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOtpInputs(vararg editTexts: EditText) {
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editTexts[i].text.isNotEmpty()) {
                        editTexts[i].text.clear()
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}