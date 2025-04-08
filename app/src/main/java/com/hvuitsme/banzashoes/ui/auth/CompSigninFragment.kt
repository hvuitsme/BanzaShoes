package com.hvuitsme.banzashoes.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import com.hvuitsme.banzashoes.data.repository.AuthRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentCompSigninBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompSigninFragment : Fragment() {
    private var _binding: FragmentCompSigninBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CompSigninFragment()
    }

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = AuthRepoImpl(AuthDataSource())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCompSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.compToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.confirmBtn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            if (username.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val user = User(
                        id = currentUser.uid,
                        username = username,
                        email = currentUser.email ?: "",
                        password = ""
                    )
                    val ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.uid)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            ref.setValue(user).await()
                            withContext(Dispatchers.Main) {
                                val bundle = Bundle().apply {
                                    putString("email", currentUser.email)
                                    putString("password", "")
                                    putString("source", "signin")
                                }
                                findNavController().navigate(R.id.action_compSigninFragment_to_otpFragment, bundle)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Error updating username: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Current user not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter username", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}