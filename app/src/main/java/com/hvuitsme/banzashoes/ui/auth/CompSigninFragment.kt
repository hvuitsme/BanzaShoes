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
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import com.hvuitsme.banzashoes.data.repository.AuthRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentCompSigninBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_comp_signin, container, false)
        _binding = FragmentCompSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmBtn.setOnClickListener { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}