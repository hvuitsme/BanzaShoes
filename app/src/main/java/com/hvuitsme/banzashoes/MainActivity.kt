package com.hvuitsme.banzashoes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.databinding.ActivityMainBinding
import com.hvuitsme.banzashoes.databinding.NavHeaderBinding
import com.hvuitsme.banzashoes.ui.home.HomeFragment
import com.hvuitsme.banzashoes.ui.login.SigninFragment
import com.hvuitsme.test2.GoogleAuthClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var headerBinding: NavHeaderBinding

    private lateinit var googleAuthClinet: GoogleAuthClient

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        googleAuthClinet = GoogleAuthClient(this)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, HomeFragment.newInstance())
                .commit()
        }

        val navHeader = binding.navView.getHeaderView(0)

        headerBinding = NavHeaderBinding.bind(navHeader)

        updateUi()

        navHeader.setOnClickListener {

            if (googleAuthClinet.isSingedIn()){

            }else{
                binding.drawlayoutMain.closeDrawers()

                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.pop_slide_in_from_left,
                        R.anim.pop_slide_out_from_right,
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left
                    )
                    .replace(binding.container.id, SigninFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.llSignout.setOnClickListener {
            handler.postDelayed({
                lifecycleScope.launch {
                    if (googleAuthClinet.isSingedIn()){
                        googleAuthClinet.signOut()
                        updateUi()
                    }
                }
            },2000)
        }
    }

    fun onLoginSuccess() {
        updateUi()
        supportFragmentManager.popBackStack()
        supportFragmentManager
            .beginTransaction()
            .replace(binding.container.id, HomeFragment.newInstance())
            .commit()
    }

    private fun updateUi(){
        val ll_Signout = binding.llSignout
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            headerBinding.tvName.visibility = View.VISIBLE
            headerBinding.tvName.text = currentUser.displayName ?: "User"
            headerBinding.tvEmail.visibility = View.VISIBLE
            headerBinding.tvEmail.text = currentUser.email
            Glide.with(this)
                .load(currentUser.photoUrl)
                .placeholder(R.drawable.ic_guest)
                .into(headerBinding.avatar)
            ll_Signout.visibility = View.VISIBLE
            headerBinding.tvLogin.visibility = View.GONE
        }else{
            headerBinding.tvName.visibility = View.GONE
            headerBinding.tvEmail.visibility = View.GONE
            headerBinding.avatar.setImageResource(R.drawable.ic_guest)
            ll_Signout.visibility = View.GONE
            headerBinding.tvLogin.visibility = View.VISIBLE
        }
    }
}