package com.hvuitsme.banzashoes.service

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Calendar
import java.util.Properties
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage

object EmailSenderService {
    private const val SENDER_EMAIL = "hvuits.me@gmail.com"
    private const val SENDER_PASSWORD = "casp vwfs jpkq jhto"
    private const val SENDER_NAME = "BanzaShoes"
    private const val otpExpireMinutes = 1

    private var currentOtp: String? = null

    fun generateOtp(length: Int = 6): String {
        val charset = "0123456789"
        val random = SecureRandom()
        return (1..length)
            .map { charset[random.nextInt(charset.length)] }
            .joinToString("")
    }

    fun sendOtpEmail(recipientEmail: String) {
        currentOtp = generateOtp()
        val mailHtml = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; padding: 20px;">
              <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                <h2 style="font-size: 18px; color: #555555;">Mã OTP của bạn là: <strong>$currentOtp</strong></h2>
                <p style="font-size: 16px; color: #555555;">Mã này có hiệu lực trong $otpExpireMinutes phút.</p>
                <p style="font-size: 14px; color: #888888;">Nếu bạn không yêu cầu mã OTP này, vui lòng bỏ qua email này.</p>
                <hr style="border: none; border-top: 1px solid #eeeeee;">
                <p style="font-size: 12px; color: #bbbbbb;">&copy; ${Calendar.getInstance().get(Calendar.YEAR)} $SENDER_NAME. All rights reserved.</p>
              </div>
            </body>
            </html>
        """.trimIndent()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                    put("mail.smtp.ssl.trust", "smtp.gmail.com")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL, SENDER_NAME))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    subject = ""
                    setContent(mailHtml, "text/html; charset=utf-8")
                    addHeader("X-Mailer", "BanzaShoes OTP Service")
                }

                Transport.send(message)
                Log.d("EmailSenderService", "Gửi email OTP thành công tới $recipientEmail")
            } catch (e: MessagingException) {
                Log.e("EmailSenderService", "Lỗi khi gửi email: ${e.message}", e)
            }
        }
    }

    fun verifyOtp(input: String): Boolean {
        return if (input == currentOtp) {
            currentOtp = null
            true
        } else {
            false
        }
    }
}