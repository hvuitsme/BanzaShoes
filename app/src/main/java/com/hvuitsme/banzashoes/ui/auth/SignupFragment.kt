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
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.repository.AuthRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentSignupBinding
import com.hvuitsme.banzashoes.service.GoogleAuthClient
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleAuthClient: GoogleAuthClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AuthRepoImpl()
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
            val phone = binding.etPhone.text.toString().trim()
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                viewModel.register(username, email, password, if (phone.isEmpty()) null else phone) { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            if (phone.isNotEmpty()) {
                                val uid = firebaseAuth.currentUser?.uid ?: return@register
                                viewModel.updateUser(
                                    user = User(
                                        id = uid,
                                        username = username,
                                        email = email,
                                        phone = phone,
                                        password = password
                                    )
                                ) { updateResult ->
                                    when (updateResult) {
                                        is AuthResult.Success -> {
                                            findNavController().popBackStack()
                                        }
                                        is AuthResult.Failure -> {
                                            Toast.makeText(context, "Cập nhật số điện thoại thất bại: ${updateResult.error}", Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {}
                                    }
                                }
                            } else {
                                findNavController().navigate(R.id.action_signupFragment_to_compSigninFragment)
                            }
                        }
                        is AuthResult.Failure -> {
                            Toast.makeText(context, "Đăng ký thất bại: ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }

        binding.googleBtn.setOnClickListener {
            lifecycleScope.launch {
                if (googleAuthClient.isSingedIn()) {
                    googleAuthClient.signOut()
                } else {
                    val result = googleAuthClient.signIn()
                    if (result) {
                        findNavController().previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("SIGN_IN_RESULT", true)
                        findNavController().popBackStack()
                    }
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