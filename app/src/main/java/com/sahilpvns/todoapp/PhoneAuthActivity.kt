package com.sahilpvns.todoapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumberEditText: EditText
    private lateinit var sendVerificationButton: Button
    private lateinit var verificationCodeEditText: EditText
    private lateinit var verifyCodeButton: Button
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        auth = FirebaseAuth.getInstance()

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        sendVerificationButton = findViewById(R.id.sendVerificationButton)
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText)
        verifyCodeButton = findViewById(R.id.verifyCodeButton)

        sendVerificationButton.setOnClickListener {
            val phoneNumber = String.format("+91" + phoneNumberEditText.text.toString().trim())
            sendVerificationCode(phoneNumber)
        }

        verifyCodeButton.setOnClickListener {
            val code = verificationCodeEditText.text.toString().trim()
            verifyCode(code)
        }

    }


    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@PhoneAuthActivity, "$e Verification failed", Toast.LENGTH_LONG).show()
                }


                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    this@PhoneAuthActivity.verificationId = verificationId
                    verificationCodeEditText.visibility = View.VISIBLE
                    verifyCodeButton.visibility = View.VISIBLE
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(this, "$user success", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "failure", Toast.LENGTH_LONG).show()
                }
            }
    }


}