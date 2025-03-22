package com.hvuitsme.banzashoes.ui.signin

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.databinding.FragmentSigninBinding
import com.hvuitsme.banzashoes.ui.signin.SigninViewModel
import com.hvuitsme.test2.GoogleAuthClient
import kotlinx.coroutines.launch

class SigninFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleAuthClient: GoogleAuthClient
    private lateinit var signInButton: ImageView

    companion object {
        fun newInstance() = SigninFragment()
    }

    private val viewModel: SigninViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.loginToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        signInButton = view.findViewById(R.id.google_btn)

        signInButton.setOnClickListener {
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
    }
}