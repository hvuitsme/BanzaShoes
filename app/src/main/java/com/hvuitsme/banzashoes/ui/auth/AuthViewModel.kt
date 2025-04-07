package com.hvuitsme.banzashoes.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import com.hvuitsme.banzashoes.data.repository.AuthRepo
import com.hvuitsme.banzashoes.service.EmailSenderService
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepo
) : ViewModel() {
    private val _signUpResult = MutableLiveData<Pair<Boolean, String?>>()
    val signUpResult: LiveData<Pair<Boolean, String?>> = _signUpResult

    private val _signInResult = MutableLiveData<Pair<Boolean, String?>>()
    val signInResult: LiveData<Pair<Boolean, String?>> = _signInResult

    fun sendOtp(identifier: String) {
        viewModelScope.launch {
            val recipientEmail = if (identifier.contains("@")) {
                identifier
            } else {
                val authDataSource = AuthDataSource()
                authDataSource.getEmailByIdentifier(identifier)
            }
            if (recipientEmail != null) {
                EmailSenderService.sendOtpEmail(recipientEmail)
            } else {
                _signInResult.value = Pair(false, "Error sending OTP")
            }
        }
    }

    fun verifyOtpAndSignUp(user: User, otpInput: String) {
        if (EmailSenderService.verifyOtp(otpInput)) {
            viewModelScope.launch {
                val result = repository.signUp(user)
                _signUpResult.value = result
            }
        } else {
            _signUpResult.value = Pair(false, "Invalid OTP")
        }
    }

    fun verifyOtpAndSignIn(email: String, password: String, otpInput: String) {
        if (EmailSenderService.verifyOtp(otpInput)) {
            viewModelScope.launch {
                val result = repository.signIn(email, password)
                _signInResult.value = result
            }
        } else {
            FirebaseAuth.getInstance().signOut()
            _signInResult.value = Pair(false, "Invalid OTP")
        }
    }
}