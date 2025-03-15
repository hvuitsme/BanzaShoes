package com.hvuitsme.banzashoes.ui.cart

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.databinding.FragmentCartBinding
import com.hvuitsme.banzashoes.viewmodel.CartViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CartFragment()
    }

    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_cart, container, false)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartToolbar.setNavigationOnClickListener{
            val navOptions = navOptions {
                anim {
                    enter = R.anim.pop_slide_in_from_left
                    exit = R.anim.pop_slide_out_from_right
                    popEnter = R.anim.slide_in_from_right
                    popExit = R.anim.slide_out_to_left
                }
            }
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment, null, navOptions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}